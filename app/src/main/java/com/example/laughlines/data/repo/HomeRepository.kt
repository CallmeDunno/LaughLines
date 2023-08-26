package com.example.laughlines.data.repo

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class HomeRepository {

    private val fDb = FirebaseFirestore.getInstance()

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