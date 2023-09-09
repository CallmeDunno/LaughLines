package com.example.laughlines.view.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.example.laughlines.R
import com.example.laughlines.databinding.FragmentProfileBinding
import com.example.laughlines.log.Logger
import com.example.laughlines.utils.SharedPreferencesManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ProfileFragment : Fragment() {

    private var _binding : FragmentProfileBinding? = null
    private val binding get() = _binding!!

    @Inject lateinit var sharedPreManager: SharedPreferencesManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Logger.d(sharedPreManager.getString("uid").toString())
        initAction()
    }


    private fun initAction() {
        binding.apply {
            btnBack.setOnClickListener { requireView().findNavController().popBackStack() }
            btnChangeInfor.setOnClickListener { requireView().findNavController().navigate(R.id.action_profileFragment_to_inforDetailFragment) }
            btnChangePass.setOnClickListener { requireView().findNavController().navigate(R.id.action_profileFragment_to_changePasswordDetailFragment) }
            btnChatbot.setOnClickListener {  }
            btnSignOut.setOnClickListener {  }
        }
    }

}