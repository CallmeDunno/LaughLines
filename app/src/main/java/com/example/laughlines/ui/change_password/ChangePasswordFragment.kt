package com.example.laughlines.ui.change_password

import androidx.navigation.findNavController
import com.example.laughlines.R
import com.example.laughlines.base.BaseFragment
import com.example.laughlines.databinding.FragmentChangePasswordBinding
import com.example.laughlines.utils.extensions.hide
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ChangePasswordFragment : BaseFragment<FragmentChangePasswordBinding>() {
    override val layoutId: Int = R.layout.fragment_change_password

    override fun initView() {
        super.initView()
        binding.toolbar.apply {
            btnSettings.hide()
            tvToolbar.text = getString(R.string.change_password)
        }
    }

    override fun initAction() {
        super.initAction()
        binding.apply {
            toolbar.btnBack.setOnClickListener { requireView().findNavController().popBackStack() }
            btnSave.setOnClickListener { requireView().findNavController().popBackStack() }
        }
    }

}