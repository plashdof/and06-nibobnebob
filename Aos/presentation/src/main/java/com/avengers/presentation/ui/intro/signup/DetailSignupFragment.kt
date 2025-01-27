package com.avengers.nibobnebob.presentation.ui.intro.signup

import android.content.Intent
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.avengers.nibobnebob.presentation.base.BaseFragment
import com.avengers.nibobnebob.presentation.customview.CalendarDatePicker
import com.avengers.nibobnebob.presentation.ui.intro.IntroViewModel
import com.avengers.nibobnebob.presentation.ui.main.MainActivity
import com.avengers.nibobnebob.presentation.ui.toMultiPart
import com.avengers.presentation.R
import com.avengers.presentation.databinding.FragmentDetailSignupBinding
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DetailSignupFragment :
    BaseFragment<FragmentDetailSignupBinding>(R.layout.fragment_detail_signup) {


    private val viewModel: DetailSignupViewModel by viewModels()
    override val parentViewModel: IntroViewModel by activityViewModels()

    private val args: DetailSignupFragmentArgs by navArgs()
    private val email by lazy { args.email }
    private val password by lazy { args.password }
    private val provider by lazy { args.provider }

    override fun initView() {
        binding.vm = viewModel
        viewModel.setDefaultData(email, password, provider)
        initImageObserver()
        setGenderRadioListener()
        setDateBtnListener()
        setLocationInputListener()
    }

    override fun initNetworkView() {
        //TODO : 네트워크
    }

    override fun initEventObserver() {
        repeatOnStarted {
            viewModel.events.collect {
                when (it) {
                    is DetailSignupEvents.NavigateToBack -> findNavController().navigateUp()
                    is DetailSignupEvents.ShowSnackMessage -> showSnackBar(it.msg)
                    is DetailSignupEvents.OpenGallery -> parentViewModel.openGallery()
                    is DetailSignupEvents.ShowLoading -> showLoading(requireContext())
                    is DetailSignupEvents.DismissLoading -> dismissLoading()
                    is DetailSignupEvents.GoToMainActivity -> {
                        val intent = Intent(requireContext(), MainActivity::class.java)
                            .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                        startActivity(intent)
                    }
                }
            }
        }
    }

    private fun initImageObserver() {
        repeatOnStarted {
            parentViewModel.image.collect {
                if (it.isNotBlank()) {
                    viewModel.setImage(it, it.toMultiPart(requireContext(), "profileImage"))
                    parentViewModel.uriCollected()
                }
            }
        }
    }

    private fun setGenderRadioListener() {
        binding.rgGender.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.rb_gender_female -> viewModel.setIsMale(false)
                R.id.rb_gender_male -> viewModel.setIsMale(true)
            }
        }
    }

    private fun setDateBtnListener() {
        binding.tilBirth.setEndIconOnClickListener {
            CalendarDatePicker {
                viewModel.setBirth(it)
            }.show(parentFragmentManager)
        }
    }

    private fun setLocationInputListener() {
        (binding.etLocation as MaterialAutoCompleteTextView).apply {
            simpleItemSelectedColor = ContextCompat.getColor(this.context, R.color.nn_primary1)
            setDropDownBackgroundTint(ContextCompat.getColor(this.context, R.color.nn_primary0))
            setSimpleItems(resources.getStringArray(R.array.location_list))
        }
    }
}

