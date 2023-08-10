package com.example.laughlines.view.login

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.example.laughlines.R
import com.example.laughlines.databinding.FragmentLoginBinding
import com.example.laughlines.log.Logger
import com.example.laughlines.view.login.data.User
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class LoginFragment : Fragment() {

    private val GOOGLE_SIGN_IN_CLIENT_CODE = 100
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private lateinit var client: GoogleSignInClient
    private val fDb = Firebase.firestore
    private val fAuth: FirebaseAuth = FirebaseAuth.getInstance()

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
                val options = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(getString(R.string.default_web_client_id)) //Vào file build gradle (project) cập nhật classpath google-service lên phiên bản mới nhất là tự có
                    .requestEmail()
                    .build()
                client = GoogleSignIn.getClient(requireContext(), options)
                startActivityForResult(client.signInIntent, GOOGLE_SIGN_IN_CLIENT_CODE)
            }
            btnLoginWithEmail.setOnClickListener {
                findNavController().navigate(R.id.action_loginFragment_to_signinFragment)
            }
            tvRegisterLogIn.setOnClickListener {
                findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK){
            if (requestCode == GOOGLE_SIGN_IN_CLIENT_CODE) {
                val task = GoogleSignIn.getSignedInAccountFromIntent(data)
                val account = task.getResult(ApiException::class.java)
                val credential = GoogleAuthProvider.getCredential(account.idToken, null)
                fAuth.signInWithCredential(credential)
                    .addOnCompleteListener {
                        if (it.isSuccessful) {
                            val user = fAuth.currentUser
                            saveUserToFireStore(user)
                            requireView().findNavController()
                                .popBackStack(R.id.login_navigation, true)
                            requireView().findNavController().navigate(R.id.home_navigation)
                        }
                    }
                    .addOnFailureListener {
                        Logger.e("Login with Google account failure")
                    }
            }
        }

    }

    private fun saveUserToFireStore(u: FirebaseUser?) {
        u?.let {
            val name = it.displayName
            val email = it.email
            val avatarUrl = it.photoUrl.toString()
            val uid = it.uid
            fDb.collection("User").whereEqualTo("email", email)
                .get()
                .addOnCompleteListener { result ->
                    if (result.isSuccessful) {
                        if (result.result.isEmpty) {
                            val user = User(uid, name!!, email!!, null, avatarUrl)
                            fDb.collection("User")
                                .add(user)
                                .addOnCompleteListener { document ->
                                    if (document.isSuccessful) Logger.d("Save user to FireStore successfully")
                                }
                                .addOnFailureListener { e ->
                                    Logger.e("Save user to FireStore failure with ${e.message}")
                                }
                        }
                    }
                }
                .addOnFailureListener { exception ->
                    Logger.e("Fail ${exception.message}")
                }
        }
    }

}