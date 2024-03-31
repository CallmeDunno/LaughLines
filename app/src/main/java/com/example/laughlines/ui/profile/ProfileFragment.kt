package com.example.laughlines.ui.profile

import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import com.bumptech.glide.Glide
import com.example.laughlines.R
import com.example.laughlines.base.BaseFragment
import com.example.laughlines.databinding.FragmentProfileBinding
import com.example.laughlines.log.Logger
import com.example.laughlines.utils.SharedPreferencesManager
import com.example.laughlines.utils.UiState
import com.example.laughlines.viewmodel.ProfileViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ProfileFragment : BaseFragment<FragmentProfileBinding>() {
    override val layoutId: Int = R.layout.fragment_profile

    private val viewModel by viewModels<ProfileViewModel>()

    @Inject
    lateinit var sharedPreManager: SharedPreferencesManager

    override fun initView() {
        viewModel.getAccount().observe(viewLifecycleOwner) {
            when (it) {
                is UiState.Loading -> {}
                is UiState.Success -> {
                    binding.apply {
                        Glide.with(requireView()).load(it.data.avatarUrl).into(imgAvatar)
                        tvName.text = it.data.name
                        tvEmail.text = it.data.email
                    }
                }
                is UiState.Failure -> {
                    Logger.e(it.message.toString())
                }
            }
        }
    }

    override fun initAction() {
        binding.apply {
            btnBack.setOnClickListener { requireView().findNavController().popBackStack() }
            btnMorseCode.setOnClickListener { requireView().findNavController().navigate(R.id.action_profileFragment_to_morseCodeFragment) }
            btnChangeInfor.setOnClickListener {
                requireView().findNavController()
                    .navigate(R.id.action_profileFragment_to_inforDetailFragment)
            }
            btnChangePass.setOnClickListener {
                requireView().findNavController()
                    .navigate(R.id.action_profileFragment_to_changePasswordDetailFragment)
            }
            btnChatbot.setOnClickListener { }
            btnSignOut.setOnClickListener {
                viewModel.signOut()
                requireView().findNavController().popBackStack(R.id.home_navigation, true)
                requireView().findNavController().navigate(R.id.login_navigation)
            }
        }
    }

}