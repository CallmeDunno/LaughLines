package com.example.laughlines.ui.change_pass_detail

import androidx.navigation.findNavController
import com.example.laughlines.R
import com.example.laughlines.base.BaseFragment
import com.example.laughlines.databinding.FragmentChangePasswordBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ChangePasswordFragment : BaseFragment<FragmentChangePasswordBinding>() {
    override val layoutId: Int = R.layout.fragment_change_password

    override fun initView() {
        super.initView()
    }

    override fun initAction() {
        binding.apply {
            btnBack.setOnClickListener { requireView().findNavController().popBackStack() }
            btnSave.setOnClickListener { requireView().findNavController().popBackStack() }
        }
    }

}