package com.example.laughlines.repository

import com.example.laughlines.model.Person
import com.example.laughlines.utils.Constant
import com.example.laughlines.utils.SharedPreferencesManager
import com.example.laughlines.utils.UiState
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import javax.inject.Inject

class ProfileRepository @Inject constructor(
    private val fDb: FirebaseFirestore,
    private val fAuth: FirebaseAuth,
    private val sharedPreManager: SharedPreferencesManager
) {

    fun getAccount(result: (UiState<Person>) -> Unit) {
        val myId = sharedPreManager.getString(Constant.Key.ID.name) ?: Constant.ID_DEFAULT
        fDb.collection(Constant.Collection.User.name)
            .document(myId)
            .get()
            .addOnSuccessListener {
                val name = it.data?.get("name").toString()
                val email = it.data?.get("email").toString()
                val avatar = if (it.data?.get("avatar").toString() == "null") "" else it.data?.get("avatar").toString()
                result.invoke(UiState.Success(Person(name, email, avatar)))
            }
            .addOnFailureListener { result.invoke(UiState.Failure(it.message)) }
    }

    fun resetPassword(email: String, result: (UiState<Boolean>) -> Unit) {
        result.invoke(UiState.Loading)
        fAuth.sendPasswordResetEmail(email)
            .addOnSuccessListener { result.invoke(UiState.Success(true)) }
            .addOnFailureListener { result.invoke(UiState.Failure(it.message)) }
    }

    fun signOut() {
        fAuth.signOut()
        sharedPreManager.clear()
    }


}