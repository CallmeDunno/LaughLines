package com.example.laughlines.data.repo

import androidx.lifecycle.MutableLiveData
import com.example.laughlines.log.Logger
import com.example.laughlines.model.Friend
import com.example.laughlines.model.Messages
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

class ChatRepository {

    private val fDb = Firebase.firestore

    fun getFriend(fid: String, cid: String): MutableLiveData<Friend> {
        val mutableLiveData = MutableLiveData<Friend>()
        fDb.collection("User")
            .whereEqualTo("id", fid)
            .get()
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    for (f in it.result) {
                        val name = f["name"].toString()
                        val avatarUrl = f["avatarUrl"].toString()
                        val friend = Friend(fid, cid, name, avatarUrl)
                        mutableLiveData.postValue(friend)
                        break
                    }
                }
            }
        return mutableLiveData
    }

    fun getMessages(cid: String): MutableLiveData<List<Messages>> {
        val mutableLiveData = MutableLiveData<List<Messages>>()
        val list = ArrayList<Messages>()
        fDb.collection("Chats")
            .document(cid)
            .collection("Message")
            .addSnapshotListener { value, error ->
                if (error != null) {
                    Logger.d(error.message.toString())
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
                            val x = Messages(messId, message, recipient, sender, timestamp)
                            list.add(x)
                        }
                    }
                    mutableLiveData.postValue(list)
                }
            }
        return mutableLiveData
    }

    suspend fun insertMessage(messages: Messages, cid: String) {
        fDb.collection("Chats")
            .document(cid)
            .collection("Message")
            .add(messages)
            .await()!!
    }

}