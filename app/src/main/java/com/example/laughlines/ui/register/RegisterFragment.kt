package com.example.laughlines.ui.register

import android.annotation.SuppressLint
import android.text.TextUtils
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import com.example.laughlines.R
import com.example.laughlines.base.BaseFragment
import com.example.laughlines.databinding.FragmentRegisterBinding
import com.example.laughlines.dialog.LoadingDialog
import com.example.laughlines.model.Account
import com.example.laughlines.utils.UiState
import com.example.laughlines.utils.extensions.hideKeyboard
import com.example.laughlines.utils.extensions.show
import com.example.laughlines.viewmodel.LoginViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RegisterFragment : BaseFragment<FragmentRegisterBinding>() {
    override val layoutId: Int = R.layout.fragment_register

    private val viewModel by viewModels<LoginViewModel>()

    override fun initView() {
        super.initView()
        binding.toolbar.apply {
            tvHeader.text = getString(R.string.register_account)
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun initAction() {
        super.initAction()
        binding.apply {

            root.setOnTouchListener { view, event ->
                if (event.action == MotionEvent.ACTION_DOWN) {
                    view.hideKeyboard()
                }
                false
            }

            toolbar.btnBack.setOnClickListener { requireView().findNavController().popBackStack() }
            btnCreateAccount.setOnClickListener {
                isValid()
            }
        }
    }

    private fun isValid() {
        val strName = binding.edtNameRegister.text.toString().trim()
        val strEmail = binding.edtEmailRegister.text.toString().trim()
        val strNumberPhone = binding.edtNumberPhoneRegister.text.toString()
        val strPass = binding.edtPasswordRegister.text.toString().trim()
        val strConfirmPass = binding.edtConfirmPasswordRegister.text.toString().trim()
        if (isValidName(strName) && isValidEmail(strEmail) && isValidPass(strPass) && isValidConfirmPassword(strPass, strConfirmPass) && inValidNumberPhone(strNumberPhone)) {
            val dialogLoading = LoadingDialog(requireContext())
            dialogLoading.show()
            viewModel.createAccount(strEmail, strPass).observe(viewLifecycleOwner) {
                when (it) {
                    is UiState.Loading -> {}
                    is UiState.Success -> {
                        Toast.makeText(requireContext(), getString(R.string.create_account_successful), Toast.LENGTH_SHORT).show()
                        val account = Account(it.data, strName, strEmail, null, null, strNumberPhone)
                        viewModel.saveUserToFireStore(account)
                        clearEditText()
                        dialogLoading.dismiss()
                        requireView().findNavController().popBackStack()
                    }
                    is UiState.Failure -> {
                        setTextWarning(2, it.message.toString())
                        stateVisibleWarning(2, true)
                        dialogLoading.dismiss()
                    }
                }
            }
        }

    }

    private fun clearEditText() {
        binding.apply {
            edtNameRegister.setText("")
            edtEmailRegister.setText("")
            edtNumberPhoneRegister.setText("")
            edtPasswordRegister.setText("")
            edtConfirmPasswordRegister.setText("")
        }
    }

    private fun inValidNumberPhone(str: String): Boolean {
        if (str.length in 0 until 10) {
            binding.tvWarning5Register.text = getString(R.string.incorrect_format)
            binding.tvWarning5Register.show()
            return false
        }
        binding.tvWarning5Register.visibility = View.INVISIBLE
        return true
    }

    private fun isValidConfirmPassword(s1: String, s2: String): Boolean {
        if (TextUtils.isEmpty(s2)) {
            setTextWarning(4, getString(R.string.cannot_be_left_blank))
            stateVisibleWarning(4, true)
            return false
        }

        if (s1 != s2) {
            setTextWarning(4, getString(R.string.doesnot_match_the_password))
            stateVisibleWarning(4, true)
            return false
        }

        stateVisibleWarning(4, false)
        return true
    }

    private fun isValidPass(s: String): Boolean {
        if (TextUtils.isEmpty(s)) {
            setTextWarning(3, getString(R.string.cannot_be_left_blank))
            stateVisibleWarning(3, true)
            return false
        }

        if (s.length < 8) {
            setTextWarning(3, getString(R.string.password_must_be_at_least_8_characters))
            stateVisibleWarning(3, true)
            return false
        }

        stateVisibleWarning(3, false)
        return true
    }

    private fun isValidEmail(s: String): Boolean {
        if (TextUtils.isEmpty(s)) {
            setTextWarning(2, getString(R.string.cannot_be_left_blank))
            stateVisibleWarning(2, true)
            return false
        }

        if (!s.matches(Regex("^[\\w-.]+@([\\w-]+\\.)+[\\w-]{2,4}\$"))) {
            setTextWarning(2, getString(R.string.invalid_email))
            stateVisibleWarning(2, true)
            return false
        }

        stateVisibleWarning(2, false)
        return true
    }

    private fun isValidName(s: String): Boolean {
        if (TextUtils.isEmpty(s)) {
            setTextWarning(1, getString(R.string.cannot_be_left_blank))
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