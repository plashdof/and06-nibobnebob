package com.avengers.nibobnebob.presentation.ui.intro.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.avengers.nibobnebob.app.DataStoreManager
import com.avengers.nibobnebob.data.repository.LoginRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class LoginEvent {
    data object NavigateToMain : LoginEvent()
    data object NavigateToDetailSignup : LoginEvent()
    data object NavigateToDialog : LoginEvent()
}

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginRepository: LoginRepository,
    private val dataStoreManager: DataStoreManager
) : ViewModel() {
    private val TAG = "LoginViewModelDebug"

    private val _events = MutableSharedFlow<LoginEvent>()
    val events = _events.asSharedFlow()

    private val _uiState = MutableStateFlow(CommonRequest())
    val uiState = _uiState.asStateFlow()

    val email = MutableStateFlow("")
    val password = MutableStateFlow("")
    val token = MutableStateFlow("")
    val autoLogin = MutableStateFlow(false)

    init {
        observeEmail()
        observePassword()
    }

    private fun observeEmail(){
        email.onEach { newEmail ->
            _uiState.update { state ->
                state.copy(email = newEmail)
            }
        }.launchIn(viewModelScope)
    }

    private fun observePassword() {
        password.onEach { newPassword ->
            _uiState.update { state ->
                state.copy(password = newPassword)
            }
        }.launchIn(viewModelScope)
    }

    fun setAutoLogin(newState: Boolean) {
        autoLogin.value = newState
    }

    fun postCommonLogin(){
        //TODO : 일반로그인
    }

    fun naverLogin(){
        viewModelScope.launch {
            dataStoreManager.putAccessToken(token.value)
            loginRepository.loginNaver().onEach {response ->
                when(response.code) {
                    200 -> {
                        _events.emit(LoginEvent.NavigateToMain)}
                    401 -> {
                        //토큰 만료
                    }
                    404 -> {
                        _events.emit(LoginEvent.NavigateToDetailSignup)
                    }
                }
            }.launchIn(viewModelScope)
        }
    }
}