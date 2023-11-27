package com.avengers.nibobnebob.presentation.ui.main.home


import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.avengers.nibobnebob.data.model.BaseState
import com.avengers.nibobnebob.data.repository.HomeRepository
import com.avengers.nibobnebob.presentation.ui.main.home.mapper.toUiRestaurantData
import com.avengers.nibobnebob.presentation.ui.main.home.model.UiFilterData
import com.avengers.nibobnebob.presentation.ui.main.home.model.UiRestaurantData
import com.avengers.nibobnebob.presentation.util.Constants.MY_LIST
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeUiState(
    val locationTrackingState: TrackingState = TrackingState.TryOn,
    val filterList: List<UiFilterData> = emptyList(),
    val markerList: List<UiRestaurantData> = emptyList(),
    val curFilter: String = MY_LIST,
    val latitude: Double = 0.0,
    val longitude: Double = 0.0
)

sealed class TrackingState {
    data object TryOn : TrackingState()
    data object On : TrackingState()
    data object Off : TrackingState()
}

sealed class HomeEvents {
    data object NavigateToSearchRestaurant : HomeEvents()
    data object SetNewMarkers : HomeEvents()
    data object RemoveMarkers : HomeEvents()
}

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val homeRepository: HomeRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<HomeEvents>()
    val events: SharedFlow<HomeEvents> = _events.asSharedFlow()

    fun updateLocation(latitude: Double, longitude: Double) {
        _uiState.update { state ->
            state.copy(
                latitude = latitude,
                longitude = longitude
            )
        }
    }

    fun locationBtnClicked() {
        _uiState.update { state ->
            state.copy(
                locationTrackingState = if (_uiState.value.locationTrackingState == TrackingState.Off) TrackingState.TryOn
                else TrackingState.Off
            )
        }
    }

    fun trackingOn() {
        _uiState.update { state ->
            state.copy(
                locationTrackingState = TrackingState.On
            )
        }
    }

    fun trackingOff() {
        _uiState.update { state ->
            state.copy(
                locationTrackingState = TrackingState.Off
            )
        }
    }

    fun getFilterList() {
        homeRepository.followList().onEach {
            when (it) {
                is BaseState.Success -> {
                    val initialFilterData = UiFilterData(MY_LIST, true, ::onFilterItemClicked)
                    val filterList = listOf(initialFilterData) + it.data.body.map { name ->
                        UiFilterData(name, false, ::onFilterItemClicked)
                    }
                    _uiState.update { state ->
                        state.copy(
                            filterList = filterList,
                            curFilter = MY_LIST
                        )
                    }
                }

                is BaseState.Error -> {
                    _uiState.update { state ->
                        state.copy(
                            filterList = listOf(UiFilterData(MY_LIST, true, ::onFilterItemClicked)),
                            curFilter = MY_LIST
                        )
                    }
                    Log.d("FilterList Test", "Error : $it")
                }
            }

        }.launchIn(viewModelScope)
    }

    fun getMarkerList() {
        viewModelScope.launch {
            when(_uiState.value.curFilter){
                MY_LIST ->{
                    homeRepository.myRestaurantList().onEach {
                        _events.emit(HomeEvents.RemoveMarkers)
                        when(it){
                            is BaseState.Success ->{
                                _uiState.update { state ->
                                    state.copy(markerList = it.data.body.map { data ->
                                        data.toUiRestaurantData()
                                    })
                                }
                            }
                            is BaseState.Error -> Log.d("Filter Test","Error : $it")
                        }
                        _events.emit(HomeEvents.SetNewMarkers)
                    }.launchIn(viewModelScope)
                }
                else ->{
                    homeRepository.filterRestaurantList(
                        _uiState.value.curFilter,
                        "${_uiState.value.latitude} ${_uiState.value.longitude}",
                        50000
                    ).onEach {
                        _events.emit(HomeEvents.RemoveMarkers)
                        when(it){
                            is BaseState.Success -> {
                                _uiState.update {state ->
                                    state.copy(markerList = it.data.body.map { data ->
                                        data.toUiRestaurantData()
                                    })
                                }
                            }
                            is BaseState.Error -> Log.d("Filter Test","Error : $it")
                        }
                        _events.emit(HomeEvents.SetNewMarkers)
                    }.launchIn(viewModelScope)
                }
            }
        }
    }

    private fun onFilterItemClicked(name: String) {
        _uiState.update { state ->
            state.copy(
                curFilter = name,
                filterList = state.filterList.map {
                    if (it.name == name) {
                        it.copy(
                            isSelected = true
                        )
                    } else if (it.isSelected) {
                        it.copy(
                            isSelected = false
                        )
                    } else {
                        it
                    }
                },
                locationTrackingState = TrackingState.Off
            )
        }

        getMarkerList()
    }

    fun navigateToSearchRestaurant() {
        viewModelScope.launch {
            _events.emit(HomeEvents.NavigateToSearchRestaurant)
        }
    }

}