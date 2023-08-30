package com.example.laughlines.data.repo

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

class ProfileRepository {

    private val fDb = Firebase.firestore
    private val fAuth = FirebaseAuth.getInstance()

    fun getUID() = fAuth.currentUser?.uid.toString()

    suspend fun getUser(uid: String) = fDb.collection("User")
        .whereEqualTo("id", uid)
        .get()
        .await()!!

}