package com.example.laughlines.view.login

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.example.laughlines.R
import com.example.laughlines.databinding.FragmentRegisterBinding

class RegisterFragment : Fragment() {

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initAction()

    }

    private fun initAction() {
        binding.apply {
            btnBackRegister.setOnClickListener { requireView().findNavController().popBackStack() }
            btnSignInRegister.setOnClickListener {
                requireView().findNavController().popBackStack()
                requireView().findNavController()
                    .navigate(R.id.action_loginFragment_to_signinFragment)
            }
            btnCreateAccountRegister.setOnClickListener {
                isValid()
            }
        }
    }

    private fun isValid() {
        val strName = binding.edtNameRegister.text.toString().trim()
        val strEmail = binding.edtEmailRegister.text.toString().trim()
        val strPass = binding.edtPasswordRegister.text.toString().trim()
        val strConfirmPass = binding.edtConfirmPasswordRegister.text.toString().trim()
        if (isValidName(strName) && isValidEmail(strEmail) && isValidPass(strPass) && isValidConfirmPassword(strPass, strConfirmPass)) {
            Toast.makeText(requireContext(), "Done", Toast.LENGTH_LONG).show()
        }
    }

    private fun isValidConfirmPassword(s1: String, s2: String): Boolean {
        if (TextUtils.isEmpty(s2)) {
            setTextWarning(4, "Can't be left blank")
            stateVisibleWarning(4, true)
            return false
        }

        if (s1 != s2) {
            setTextWarning(4, "Doesn't match the password")
            stateVisibleWarning(4, true)
            return false
        }

        stateVisibleWarning(4, false)
        return true
    }

    private fun isValidPass(s: String): Boolean {
        if (TextUtils.isEmpty(s)) {
            setTextWarning(3, "Can't be left blank")
            stateVisibleWarning(3, true)
            return false
        }

        if (s.length < 8){
            setTextWarning(3, "Password must be at least 8 characters")
            stateVisibleWarning(3, true)
            return false
        }

        stateVisibleWarning(3, false)
        return true
    }

    private fun isValidEmail(s: String): Boolean {
        if (TextUtils.isEmpty(s)) {
            setTextWarning(2, "Can't be left blank")
            stateVisibleWarning(2, true)
            return false
        }

        //compare to list account that get from database for checking exist email

        if (!s.matches(Regex("^[\\w-.]+@([\\w-]+\\.)+[\\w-]{2,4}\$"))) {
            setTextWarning(2, "Invalid email")
            stateVisibleWarning(2, true)
            return false
        }

        stateVisibleWarning(2, false)
        return true
    }

    private fun isValidName(s: String): Boolean {
        if (s.length < 8){
            setTextWarning(1, "Your name must be at least 8 characters")
            stateVisibleWarning(1, true)
            return false
        }

        if (TextUtils.isEmpty(s)) {
            setTextWarning(1, "Can't be left blank")
            stateVisibleWarning(1, true)
            return false
        }

        stateVisibleWarning(1, false)
        return true
    }

    private fun stateVisibleWarning(t: Int, b: Boolean) { //true is visible, false is invisible
        if (b) {
            when (t) {
                1 -> binding.tvWarning1Register.visibility = View.VISIBLE
                2 -> binding.tvWarning2Register.visibility = View.VISIBLE
                3 -> binding.tvWarning3Register.visibility = View.VISIBLE
                4 -> binding.tvWarning4Register.visibility = View.VISIBLE
            }
        } else {
            when (t) {
                1 -> binding.tvWarning1Register.visibility = View.INVISIBLE
                2 -> binding.tvWarning2Register.visibility = View.INVISIBLE
                3 -> binding.tvWarning3Register.visibility = View.INVISIBLE
                4 -> binding.tvWarning4Register.visibility = View.INVISIBLE
            }
        }
    }

    private fun setTextWarning(t: Int, s: String) {
        when (t) {
            1 -> binding.tvWarning1Register.text = s
            2 -> binding.tvWarning2Register.text = s
            3 -> binding.tvWarning3Register.text = s
            4 -> binding.tvWarning4Register.text = s
        }
    }

}