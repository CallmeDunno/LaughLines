package com.example.laughlines.data.repo

import com.example.laughlines.model.Account
import com.example.laughlines.utils.UiState
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import javax.inject.Inject

class LoginRepository @Inject constructor(
    private val fDb: FirebaseFirestore,
    private val fAuth: FirebaseAuth
) {

    fun createAccount(email: String, password: String, result: (UiState<String>) -> Unit) =
        fAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { result.invoke(UiState.Success(it.result.user?.uid.toString())) }
            .addOnFailureListener { result.invoke(UiState.Failure("Error: ${it.message.toString()}")) }

    fun signIn(email: String, password: String, result: (UiState<String>) -> Unit) {
        fAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { result.invoke(UiState.Success(it.result.user?.uid.toString())) }
            .addOnFailureListener { result.invoke(UiState.Failure(it.message.toString())) }
    }

    fun signInWithCredential(credential: AuthCredential, result: (UiState<FirebaseUser>) -> Unit) {
        fAuth.signInWithCredential(credential)
            .addOnCompleteListener { result.invoke(UiState.Success(fAuth.currentUser!!)) }
            .addOnFailureListener { result.invoke(UiState.Failure("Error: ${it.message.toString()}")) }
    }

    fun saveUserToFireStore(u: FirebaseUser) {
        val name = u.displayName.toString()
        val email = u.email.toString()
        val avatarUrl = u.photoUrl.toString()
        val uid = u.uid
        val account = Account(uid, name, email, null, avatarUrl)
        saveUserToFireStore(account)
    }

    fun saveUserToFireStore(account: Account) {
        checkAccountExists(account.email) { result ->
            when (result) {
                is UiState.Success -> {
                    if (result.data) addAccountToFireStore(account)
                }
                is UiState.Failure -> {}
            }
        }
    }

    private fun checkAccountExists(email: String, result: (UiState<Boolean>) -> Unit) =
        fDb.collection("User")
            .whereEqualTo("email", email)
            .get()
            .addOnSuccessListener {
                if (it.isEmpty) result.invoke(UiState.Success(true))
                else result.invoke(UiState.Success(false))
            }
            .addOnFailureListener { result.invoke(UiState.Failure("Error: ${it.message.toString()}")) }

    private fun addAccountToFireStore(account: Account) = fDb.collection("User")
        .add(account)
}