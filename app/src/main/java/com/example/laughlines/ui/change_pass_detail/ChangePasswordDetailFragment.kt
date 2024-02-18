package com.example.laughlines.ui.change_pass_detail

import androidx.navigation.findNavController
import com.example.laughlines.R
import com.example.laughlines.base.BaseFragment
import com.example.laughlines.databinding.FragmentChangePasswordDetailBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ChangePasswordDetailFragment : BaseFragment<FragmentChangePasswordDetailBinding>() {
    override val layoutId: Int = R.layout.fragment_change_password_detail

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