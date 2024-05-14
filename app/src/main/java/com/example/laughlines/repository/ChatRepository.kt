package com.example.laughlines.repository

import android.util.Log
import com.example.laughlines.model.Account
import com.example.laughlines.model.Messages
import com.example.laughlines.utils.Constant
import com.example.laughlines.utils.SharedPreferencesManager
import com.example.laughlines.utils.UiState
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*
import javax.inject.Inject

class ChatRepository @Inject constructor(private val fDb: FirebaseFirestore, private val sharedPref: SharedPreferencesManager) {

    fun getInformationOfFriend(id: String, result: (UiState<Account>) -> Unit) {
        result.invoke(UiState.Loading)
        fDb.collection(Constant.Collection.User.name)
            .document(id)
            .get()
            .addOnSuccessListener {
                val name = it.data?.get("name").toString()
                val email = it.data?.get("email").toString()
                val status = it.data?.get("status").toString()
                val avatarUrl = if (it.data?.get("avatarUrl").toString() == "null") "" else it.data?.get("avatarUrl").toString()
                val numberPhone = it.data?.get("numberPhone").toString()
                result.invoke(UiState.Success(Account(id, name, email, avatarUrl, status, numberPhone)))
            }
            .addOnFailureListener { result.invoke(UiState.Failure(it.message.toString())) }
    }

    fun getMessageList(chatId: String, result: (List<Messages>) -> Unit) {
        val arr = ArrayList<Messages>()
        var message: String
        var sender: String
        var timestamp: Long
        var type: Int
        fDb.collection(Constant.Collection.Chats.name)
            .document(chatId)
            .collection(Constant.Collection.Messages.name)
            .addSnapshotListener { value, error ->
                if (error != null) {
                    Log.e("Dunno", "Error: $error")
                    return@addSnapshotListener
                }
                Log.w("Dunno", value!!.documents.size.toString())
                for (v in value.documentChanges) {
                    when(v.type) {
                        DocumentChange.Type.ADDED -> {
                            message = v.document.data["message"].toString()
                            sender = v.document.data["sender"].toString()
                            timestamp = v.document.data["timestamp"].toString().toLong()
                            type = v.document.data["type"].toString().toInt()
                            arr.add(Messages(message, sender, timestamp, type))
                            Collections.sort(arr, Messages.Companion.SortByTimestamp())
                            result.invoke(arr)
                        }
                        DocumentChange.Type.MODIFIED -> {}
                        DocumentChange.Type.REMOVED -> {}
                    }
                }
            }
    }

    fun pushMessage(chatId: String, messages: Messages, result: (UiState<Boolean>) -> Unit) {
        fDb.collection(Constant.Collection.Chats.name)
            .document(chatId)
            .collection(Constant.Collection.Messages.name)
            .add(messages)
            .addOnSuccessListener { result.invoke(UiState.Success(true)) }
            .addOnFailureListener { result.invoke(UiState.Failure(it.message.toString())) }
        fDb.collection(Constant.Collection.Chats.name)
            .document(chatId)
            .update("lastTime", messages.timestamp)
            .addOnSuccessListener { Log.i("Dunno", "Success") }
            .addOnFailureListener { Log.e("Dunno", "Failure") }
    }

}