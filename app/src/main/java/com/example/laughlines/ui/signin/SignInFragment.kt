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
import com.example.laughlines.dialog.LoadingDialog
import com.example.laughlines.utils.Constant
import com.example.laughlines.utils.SharedPreferencesManager
import com.example.laughlines.utils.UiState
import com.example.laughlines.utils.extensions.hideKeyboard
import com.example.laughlines.viewmodel.LoginViewModel
import com.zegocloud.uikit.prebuilt.call.ZegoUIKitPrebuiltCallService
import com.zegocloud.uikit.prebuilt.call.invite.ZegoUIKitPrebuiltCallInvitationConfig
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SignInFragment : BaseFragment<FragmentSigninBinding>() {
    override val layoutId: Int = R.layout.fragment_signin

    private val viewModel by viewModels<LoginViewModel>()

    @Inject
    lateinit var sharedPref: SharedPreferencesManager

    override fun initView() {
        super.initView()
        binding.toolbar.tvHeader.text = getText(R.string.sign_in)
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
            btnSignIn.setOnClickListener { isValid() }
            btnRegister.setOnClickListener {
                requireView().findNavController().popBackStack(R.id.signinFragment, true)
                requireView().findNavController().navigate(R.id.registerFragment)
            }
        }
    }

    private fun isValid() {
        val strEmail = binding.edtEmail.text.toString().trim()
        val strPass = binding.edtPassword.text.toString().trim()
        if (isValidEmail(strEmail) && isValidPassword(strPass)) {
            val loadingDialog = LoadingDialog(requireContext())
            viewModel.signIn(strEmail, strPass).observe(viewLifecycleOwner) {
                when (it) {
                    is UiState.Loading -> {
                        loadingDialog.show()
                    }
                    is UiState.Success -> {
                        if (it.data.isNotEmpty()) {
                            viewModel.getID(it.data).observe(viewLifecycleOwner) {it2 ->
                                when(it2) {
                                    is UiState.Loading -> {}
                                    is UiState.Failure -> {}
                                    is UiState.Success -> {
                                        sharedPref.putString(Constant.Key.ID.name, it2.data.first)
                                        loadingDialog.dismiss()

                                        val application = requireActivity().application // Android's application context
                                        val appID: Long = 1386242158   // yourAppID
                                        val appSign = "ffb364052c3c90ef160dbae68eb65c89e2332dfb4756bb33e528698130309d70"  // yourAppSign
                                        val userID = it2.data.first // yourUserID, userID should only contain numbers, English characters, and '_'.
                                        val userName = it2.data.second   // yourUserName
                                        val callInvitationConfig = ZegoUIKitPrebuiltCallInvitationConfig()
                                        ZegoUIKitPrebuiltCallService.init(application, appID, appSign, userID, userName, callInvitationConfig);

                                        requireView().findNavController().popBackStack(R.id.login_navigation, true)
                                        requireView().findNavController().navigate(R.id.home_navigation)
                                    }
                                }
                            }
                        }
                    }
                    is UiState.Failure -> {
                        loadingDialog.dismiss()
                        showDialogSignInFail()
                    }
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
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)
        window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        dialog.findViewById<Button>(R.id.btn_ok_dialog_sign_in_fail).setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun isValidPassword(s: String): Boolean {
        if (TextUtils.isEmpty(s)) {
            setTextWarning(2, getString(R.string.cannot_be_left_blank))
            stateVisibleWarning(2, true)
            return false
        }

        if (s.length < 8) {
            setTextWarning(2, getString(R.string.password_must_be_at_least_8_characters))
            stateVisibleWarning(2, true)
            return false
        }

        stateVisibleWarning(2, false)
        return true
    }

    private fun isValidEmail(s: String): Boolean {
        if (TextUtils.isEmpty(s)) {
            setTextWarning(1, getString(R.string.cannot_be_left_blank))
            stateVisibleWarning(1, true)
            return false
        }

        if (!s.matches(Regex("^[\\w-.]+@([\\w-]+\\.)+[\\w-]{2,4}\$"))) {
            setTextWarning(1, getString(R.string.invalid_email))
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