package com.example.laughlines.data.repo

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class ProfileRepository @Inject constructor(private val fDb: FirebaseFirestore, private val fAuth: FirebaseAuth) {

    fun getUID() = fAuth.currentUser?.uid.toString()

    suspend fun getUser(uid: String) = fDb.collection("User")
        .whereEqualTo("id", uid)
        .get()
        .await()!!

}