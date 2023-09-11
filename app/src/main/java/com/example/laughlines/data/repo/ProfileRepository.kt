package com.example.laughlines.data.repo

import com.example.laughlines.model.Person
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
        val uid = sharedPreManager.getString("uid")!!
        fDb.collection("User")
            .whereEqualTo("id", uid)
            .limit(1)
            .get()
            .addOnCompleteListener {
                if (it.isSuccessful)
                    for (p in it.result) {
                        val name = p.data["name"].toString()
                        val email = p.data["email"].toString()
                        val avatarUrl = p.data["avatarUrl"].toString()
                        val person = Person(name, email, avatarUrl)
                        result.invoke(UiState.Success(person))
                        break
                    }
            }
            .addOnFailureListener { result.invoke(UiState.Failure("Error: ${it.message.toString()}")) }
    }

    fun signOut() {
        fAuth.signOut()
        sharedPreManager.clear()
    }


}