package com.example.laughlines.ui.change_pass_detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.example.laughlines.databinding.FragmentChangePasswordDetailBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ChangePasswordDetailFragment : Fragment() {

    private var _binding: FragmentChangePasswordDetailBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChangePasswordDetailBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initAction()
    }

    private fun initAction() {
        binding.apply {
            btnBack.setOnClickListener { requireView().findNavController().popBackStack() }
            btnSave.setOnClickListener { requireView().findNavController().popBackStack() }
        }
    }

}