package com.example.laughlines.ui.signin

import android.annotation.SuppressLint
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.text.TextUtils
import android.view.*
import android.widget.Button
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import com.example.laughlines.R
import com.example.laughlines.base.BaseFragment
import com.example.laughlines.databinding.FragmentSigninBinding
import com.example.laughlines.utils.SharedPreferencesManager
import com.example.laughlines.utils.UiState
import com.example.laughlines.utils.extensions.hideKeyboard
import com.example.laughlines.viewmodel.LoginViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SigninFragment : BaseFragment<FragmentSigninBinding>() {
    override val layoutId: Int = R.layout.fragment_signin

    private val viewModel by viewModels<LoginViewModel>()

    @Inject
    lateinit var sharedPreManager: SharedPreferencesManager

    override fun initView() {
        super.initView()
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun initAction() {
        binding.apply {

            root.setOnTouchListener { view, event ->
                if (event.action == MotionEvent.ACTION_DOWN) { view.hideKeyboard() }
                false
            }

            btnBackSignIn.setOnClickListener { requireView().findNavController().popBackStack() }
            btnSignIn.setOnClickListener { isValid() }
            btnRegister.setOnClickListener {
                requireView().findNavController().popBackStack(R.id.signinFragment, true)
                requireView().findNavController().navigate(R.id.registerFragment)
            }
        }
    }

    private fun isValid() {
        val strEmail = binding.edtEmailSignIn.text.toString().trim()
        val strPass = binding.edtPasswordSignIn.text.toString().trim()
        if (isValidEmail(strEmail) && isValidPassword(strPass)) {
            viewModel.signIn(strEmail, strPass).observe(viewLifecycleOwner) {
                when (it) {
                    is UiState.Success -> {
                        if (it.data.isNotEmpty()){
                            if (binding.cbRememberSignIn.isChecked) {
                                sharedPreManager.putString("uid", it.data)
                            }
                            requireView().findNavController().popBackStack(R.id.login_navigation, true)
                            requireView().findNavController().navigate(R.id.home_navigation)
                        }
                    }
                    is UiState.Failure -> { showDialogSignInFail() }
                }
            }
        }
    }

    private fun showDialogSignInFail() {
        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_sign_in_fail)
        dialog.setCanceledOnTouchOutside(false)
        val window = dialog.window ?: return
        window.setGravity(Gravity.CENTER)
        window.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        dialog.findViewById<Button>(R.id.btn_ok_dialog_sign_in_fail).setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun isValidPassword(s: String): Boolean {
        if (TextUtils.isEmpty(s)) {
            setTextWarning(2, "Can't be left blank")
            stateVisibleWarning(2, true)
            return false
        }

        if (s.length < 8) {
            setTextWarning(2, "Password must be at least 8 characters")
            stateVisibleWarning(2, true)
            return false
        }

        stateVisibleWarning(2, false)
        return true
    }

    private fun isValidEmail(s: String): Boolean {
        if (TextUtils.isEmpty(s)) {
            setTextWarning(1, "Can't be left blank")
            stateVisibleWarning(1, true)
            return false
        }

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