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
import com.example.laughlines.databinding.FragmentSigninBinding

class SigninFragment : Fragment() {

    private var _binding: FragmentSigninBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSigninBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initAction()
    }

    private fun initAction() {
        binding.apply {
            btnBackSignIn.setOnClickListener { requireView().findNavController().popBackStack() }
            btnSignIn.setOnClickListener {
                isValid()
            }
            btnRegisterSignIn.setOnClickListener {
                requireView().findNavController().popBackStack()
                requireView().findNavController()
                    .navigate(R.id.action_loginFragment_to_registerFragment)
            }
        }
    }

    private fun isValid() {
        val strEmail = binding.edtEmailSignIn.text.toString().trim()
        val strPass = binding.edtPasswordSignIn.text.toString().trim()
        if (isValidEmail(strEmail) && isValidPassword(strPass)) {
            Toast.makeText(requireContext(), "Done", Toast.LENGTH_LONG).show()
            requireView().findNavController().popBackStack(R.id.login_navigation, true)
            requireView().findNavController().navigate(R.id.home_navigation)
        }
    }

    private fun isValidPassword(s: String): Boolean {
        if (TextUtils.isEmpty(s)) {
            setTextWarning(2, "Can't be left blank")
            stateVisibleWarning(2, true)
            return false
        }

        if (s.length < 8){
            setTextWarning(2, "Password must be at least 8 characters")
            stateVisibleWarning(2, true)
            return false
        }

        //compare to email's account that get from database for checking exist email

        stateVisibleWarning(2, false)
        return true
    }

    private fun isValidEmail(s: String): Boolean {
        if (TextUtils.isEmpty(s)) {
            setTextWarning(1, "Can't be left blank")
            stateVisibleWarning(1, true)
            return false
        }

        //compare to list account that get from database for checking exist email

        if (!s.matches(Regex("^[\\w-.]+@([\\w-]+\\.)+[\\w-]{2,4}\$"))) {
            setTextWarning(1, "Invalid email")
            stateVisibleWarning(1, true)
            return false
        }

        stateVisibleWarning(1, false)
        return true
    }

    private fun stateVisibleWarning(t: Int, b: Boolean) { //true is visible, false is invisible
        if (b) {
            if (t == 1) binding.tvWarning1SignIn.visibility = View.VISIBLE
            else binding.tvWarning2SignIn.visibility = View.VISIBLE
        } else {
            if (t == 1) binding.tvWarning1SignIn.visibility = View.INVISIBLE
            else binding.tvWarning2SignIn.visibility = View.INVISIBLE
        }
    }

    private fun setTextWarning(t: Int, s: String) {
        if (t == 1) binding.tvWarning1SignIn.text = s
        else binding.tvWarning2SignIn.text = s
    }

}