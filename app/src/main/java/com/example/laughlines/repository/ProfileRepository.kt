package com.example.laughlines.repository

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

    }


    fun signOut() {
        fAuth.signOut()
        sharedPreManager.clear()
    }


}