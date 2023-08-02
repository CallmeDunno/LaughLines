package com.example.laughlines.view.login

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.laughlines.R
import com.example.laughlines.databinding.FragmentLoginBinding
import com.example.laughlines.log.Logger

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initAction()
    }

    private fun initAction() {
        binding.apply {
            btnLoginWithGoogle.setOnClickListener {
                Logger.d("Google")
            }
            btnLoginWithEmail.setOnClickListener {
                findNavController().navigate(R.id.action_loginFragment_to_signinFragment)
            }
            tvRegisterLogIn.setOnClickListener {
                findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
            }
        }
    }

}