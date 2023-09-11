package com.example.laughlines.ui.start

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.example.laughlines.R
import com.example.laughlines.databinding.FragmentStartBinding
import com.example.laughlines.log.Logger
import com.example.laughlines.utils.SharedPreferencesManager
import com.example.laughlines.utils.UiState
import com.example.laughlines.viewmodel.LoginViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.GoogleAuthProvider
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class StartFragment : Fragment() {

    private val GOOGLE_SIGN_IN_CLIENT_CODE = 100
    private var _binding: FragmentStartBinding? = null
    private val binding get() = _binding!!
    private lateinit var client: GoogleSignInClient
    private val viewModel by viewModels<LoginViewModel>()

    @Inject lateinit var sharedPreManager: SharedPreferencesManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStartBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initAction()
    }

    private fun initAction() {
        binding.apply {
            btnLoginWithGoogle.setOnClickListener {
                val options = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(getString(R.string.default_web_client_id)) //Vào file build gradle (project) cập nhật classpath google-service lên phiên bản mới nhất là tự có
                    .requestEmail()
                    .build()
                client = GoogleSignIn.getClient(requireContext(), options)
                startActivityForResult(client.signInIntent, GOOGLE_SIGN_IN_CLIENT_CODE)
            }
            btnLoginWithEmail.setOnClickListener {
                findNavController().navigate(R.id.action_startFragment_to_signinFragment)
            }
            tvRegisterLogIn.setOnClickListener {
                findNavController().navigate(R.id.action_startFragment_to_registerFragment)
            }
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK){
            if (requestCode == GOOGLE_SIGN_IN_CLIENT_CODE) {
                val task = GoogleSignIn.getSignedInAccountFromIntent(data)
                val account = task.getResult(ApiException::class.java)
                val credential = GoogleAuthProvider.getCredential(account.idToken, null)
                viewModel.signInWithCredential(credential).observe(viewLifecycleOwner){
                    when(it){
                        is UiState.Success -> {
                            sharedPreManager.putString("uid", it.data.uid)
                            viewModel.saveUserToFireStore(it.data)
                            requireView().findNavController()
                                .popBackStack(R.id.login_navigation, true)
                            requireView().findNavController().navigate(R.id.home_navigation)
                        }
                        is UiState.Failure -> { Logger.e(it.message.toString()) }
                    }
                }
            }
        }
    }

}