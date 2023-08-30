package com.example.laughlines.data.repo

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class HomeRepository @Inject constructor(private val fDb: FirebaseFirestore) {

    suspend fun getUserById(id: String) = fDb.collection("User")
        .whereEqualTo("id", id)
        .get()
        .await()!!

    suspend fun getFriendList(id: String) = fDb.collection("User")
        .document(id)
        .collection("Friends")
        .get()
        .await()!!

}