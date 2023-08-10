package com.example.laughlines.view.login

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.TextUtils
import android.view.*
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.example.laughlines.MainActivity
import com.example.laughlines.R
import com.example.laughlines.databinding.FragmentSigninBinding
import com.example.laughlines.log.Logger
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class SigninFragment : Fragment() {

    private var _binding: FragmentSigninBinding? = null
    private val binding get() = _binding!!
    private val fAuth: FirebaseAuth = FirebaseAuth.getInstance()

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
            signIn(strEmail, strPass)
        }
    }

    private fun signIn(email: String, password: String) {
        fAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    if (binding.cbRememberSignIn.isChecked) saveDataToSharePreferences(email)
                    requireView().findNavController().popBackStack(R.id.login_navigation, true)
                    requireView().findNavController().navigate(R.id.home_navigation)
                }
            }
            .addOnFailureListener {
                showDialogSignInFail()
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

    private fun saveDataToSharePreferences(email: String) {
        val scope = CoroutineScope(Job() + Dispatchers.IO)
        val ioScope = scope.launch {
            val s = MainActivity.sharedPref.edit()
            s.clear()
            s.putString("email", email)
            s.apply()
        }
        ioScope.invokeOnCompletion {
            Logger.d("Save data successfully")
        }
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