package com.example.laughlines.ui.profile

import androidx.navigation.findNavController
import com.example.laughlines.R
import com.example.laughlines.base.BaseFragment
import com.example.laughlines.databinding.FragmentProfileBinding
import com.example.laughlines.utils.Constant
import com.example.laughlines.utils.SharedPreferencesManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ProfileFragment : BaseFragment<FragmentProfileBinding>() {
    override val layoutId: Int = R.layout.fragment_profile

    @Inject
    lateinit var sharedPref: SharedPreferencesManager

    override fun initView() {
        super.initView()

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
                requireView().findNavController().navigate(R.id.action_profileFragment_to_changePasswordDetailFragment)
            }
            btnChatbot.setOnClickListener {}
            btnSignOut.setOnClickListener {
//                viewModel.signOut()
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