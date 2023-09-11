package com.example.laughlines.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import com.bumptech.glide.Glide
import com.example.laughlines.R
import com.example.laughlines.databinding.FragmentProfileBinding
import com.example.laughlines.log.Logger
import com.example.laughlines.utils.SharedPreferencesManager
import com.example.laughlines.utils.UiState
import com.example.laughlines.viewmodel.ProfileViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<ProfileViewModel>()

    @Inject
    lateinit var sharedPreManager: SharedPreferencesManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initAction()
    }

    private fun initView() {
        viewModel.getAccount().observe(viewLifecycleOwner) {
            when (it) {
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

    private fun initAction() {
        binding.apply {
            btnBack.setOnClickListener { requireView().findNavController().popBackStack() }
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