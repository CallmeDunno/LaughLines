package com.example.laughlines.ui.profile

import android.util.Log
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import com.bumptech.glide.Glide
import com.example.laughlines.R
import com.example.laughlines.base.BaseFragment
import com.example.laughlines.databinding.FragmentProfileBinding
import com.example.laughlines.dialog.LoadingDialog
import com.example.laughlines.utils.Constant
import com.example.laughlines.utils.SharedPreferencesManager
import com.example.laughlines.utils.UiState
import com.example.laughlines.utils.extensions.hide
import com.example.laughlines.viewmodel.ProfileViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ProfileFragment : BaseFragment<FragmentProfileBinding>() {
    override val layoutId: Int = R.layout.fragment_profile

    @Inject
    lateinit var sharedPref: SharedPreferencesManager

    private var email = ""

    private val viewModel by viewModels<ProfileViewModel>()

    override fun initView() {
        super.initView()
        binding.apply {
            toolbar.btnSettings.hide()
        }
    }

    override fun onObserve() {
        super.onObserve()
        viewModel.getAccount().observe(viewLifecycleOwner) {
            when (it) {
                is UiState.Loading -> {}
                is UiState.Failure -> {
                    notify(getString(R.string.error))
                    Log.e("Dunno", it.message.toString())
                }
                is UiState.Success -> {
                    val p = it.data
                    binding.tvName.text = p.name
                    binding.tvEmail.text = p.email
                    email = p.email
                    if (p.avatar == "") {
                        Glide.with(requireContext()).load(R.drawable.logo_chat_app).into(binding.imgAvatar)
                    } else {
                        Glide.with(requireContext()).load(p.avatar).into(binding.imgAvatar)
                    }
                }
            }
        }
    }

    override fun initAction() {
        super.initAction()
        binding.apply {
            toolbar.btnBack.setOnClickListener { requireView().findNavController().popBackStack() }
            btnMorseCode.setOnClickListener { requireView().findNavController().navigate(R.id.action_profileFragment_to_morseCodeFragment) }
            btnChangeInfor.setOnClickListener {
                requireView().findNavController().navigate(R.id.action_profileFragment_to_inforDetailFragment)
            }
            btnChangePass.setOnClickListener {
//                requireView().findNavController().navigate(R.id.action_profileFragment_to_changePasswordDetailFragment)
                val loadingDialog = LoadingDialog(requireContext())
                if (email != "") {
                    viewModel.resetPassword(email).observe(viewLifecycleOwner) {
                        when (it) {
                            is UiState.Loading -> {
                                loadingDialog.show()
                            }
                            is UiState.Failure -> {
                                loadingDialog.dismiss()
                                notify(getString(R.string.error))
                                Log.e("Dunno", it.message.toString())
                            }
                            is UiState.Success -> {
                                loadingDialog.dismiss()
                                notify("Please check your email!")
                            }
                        }
                    }
                }
            }
            btnChatbot.setOnClickListener { requireView().findNavController().navigate(R.id.action_profileFragment_to_chatbotFragment) }
            btnSignOut.setOnClickListener {
                viewModel.signOut()
                sharedPref.removeKey(Constant.Key.ID.name)
                requireView().findNavController().popBackStack(R.id.home_navigation, true)
                requireView().findNavController().navigate(R.id.login_navigation)
            }
            btnQrCode.setOnClickListener {
                requireView().findNavController().navigate(R.id.action_profileFragment_to_qrCodeFragment)
            }
        }
    }

}