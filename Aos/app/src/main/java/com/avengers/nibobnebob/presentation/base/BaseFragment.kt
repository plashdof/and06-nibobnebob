package com.avengers.nibobnebob.presentation.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

abstract class BaseFragment<B : ViewDataBinding>(
    @LayoutRes private val layoutRes: Int
) : Fragment() {

    private var _binding: B? = null
    protected val binding get() = _binding!!
    protected abstract val parentViewModel : BaseActivityViewModel


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = DataBindingUtil.inflate(inflater, layoutRes, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        repeatOnStarted {
            parentViewModel.networkState.collect{
                when(it){
                    NetWorkState.NETWORK_DISCONNECTED -> {}
                    NetWorkState.NETWORK_CONNECTED -> {
                        // todo initNetworkView 실행
                    }
                    else -> {}
                }
            }
        }
    }
    

    fun LifecycleOwner.repeatOnStarted(block: suspend CoroutineScope.() -> Unit) {
        viewLifecycleOwner.lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED, block)
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}