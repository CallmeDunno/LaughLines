package com.example.laughlines.data.repo

import com.example.laughlines.log.Logger
import com.example.laughlines.model.Friend
import com.example.laughlines.model.Messages
import com.example.laughlines.utils.UiState
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class ChatRepository @Inject constructor(private val fDb: FirebaseFirestore) {

    fun getFriend(fid: String, cid: String, result: (UiState<Friend>) -> Unit) {
        fDb.collection("User")
            .whereEqualTo("id", fid)
            .get()
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    for (f in it.result) {
                        val name = f["name"].toString()
                        val avatarUrl = f["avatarUrl"].toString()
                        val friend = Friend(fid, cid, name, avatarUrl)
                        result.invoke(UiState.Success(friend))
                        break
                    }
                }
            }
            .addOnFailureListener { result.invoke(UiState.Failure("Error: ${it.message}")) }
    }

    fun getMessages(cid: String, result: (UiState<List<Messages>>) -> Unit) {
        val list = ArrayList<Messages>()
        fDb.collection("Chats")
            .document(cid)
            .collection("Message")
            .addSnapshotListener { value, error ->
                if (error != null) {
                    Logger.d(error.message.toString())
                    result.invoke(UiState.Failure("Error: ${error.message}"))
                    return@addSnapshotListener
                }
                if (value != null) {
                    for (m in value.documentChanges) {
                        if (m.type == DocumentChange.Type.ADDED) {
                            val messId = m.document.id
                            val message = m.document.data["message"].toString()
                            val recipient = m.document.data["recipient"].toString()
                            val sender = m.document.data["sender"].toString()
                            val timestamp = m.document.data["timestamp"].toString()
                            list.add(Messages(messId, message, recipient, sender, timestamp))
                        }
                    }
                    result.invoke(UiState.Success(list))
                }
            }
    }

    suspend fun insertMessage(messages: Messages, cid: String, result: (UiState<String>) -> Unit) {
        fDb.collection("Chats")
            .document(cid)
            .collection("Message")
            .add(messages)
            .addOnCompleteListener { result.invoke(UiState.Success("Insert message successful")) }
            .addOnFailureListener { result.invoke(UiState.Failure("Error: ${it.message}")) }
            .await()!!
    }

}