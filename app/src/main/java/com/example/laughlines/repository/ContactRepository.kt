package com.example.laughlines.repository

import android.util.Log
import com.example.laughlines.model.Account
import com.example.laughlines.model.Contact
import com.example.laughlines.utils.Constant
import com.example.laughlines.utils.SharedPreferencesManager
import com.example.laughlines.utils.UiState
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

class ContactRepository @Inject constructor(private val fDb: FirebaseFirestore, private val sharedPref: SharedPreferencesManager) {

    fun getAllFriend(result: (UiState<List<Contact>>) -> Unit) {
        result.invoke(UiState.Loading)
        val arr = ArrayList<Contact>()
        fDb.collection(Constant.Collection.User.name).document(sharedPref.getString(Constant.Key.ID.name) ?: "").collection(Constant.Collection.Friends.name).addSnapshotListener { value, error ->
            if (error != null) {
                Log.e("Dunno", "Error: $error")
                result.invoke(UiState.Failure(error.message.toString()))
                return@addSnapshotListener
            }
            if (value!!.documentChanges.isEmpty()) result.invoke(UiState.Success(arr))
            else {
                for (v in value.documentChanges) {
                    when (v.type) {
                        DocumentChange.Type.ADDED -> {
                            val id = v.document.id
                            val chatId = v.document.data["chatId"].toString()
                            val friendId = v.document.data["friendId"].toString()
                            getInformation(friendId) { account ->
                                getLastTime(chatId) { lastTime ->
                                    arr.add(Contact(id, chatId, friendId, account, lastTime))
                                    result.invoke(UiState.Success(arr))
                                }
                            }
                        }
                        DocumentChange.Type.MODIFIED -> {}
                        DocumentChange.Type.REMOVED -> {}
                    }
                }
            }
        }
    }

    private fun getInformation(id: String, result: (Account) -> Unit) {
        fDb.collection(Constant.Collection.User.name).document(id).get().addOnSuccessListener {
            CoroutineScope(Job() + Dispatchers.IO).launch {
                val name = it.data?.get("name").toString()
                val email = it.data?.get("email").toString()
                val status = it.data?.get("status").toString()
                val avatarUrl = if (it.data?.get("avatarUrl").toString() == "null") "" else it.data?.get("avatarUrl").toString()
                val numberPhone = it.data?.get("numberPhone").toString()
                result.invoke(Account(id, name, email, avatarUrl, status, numberPhone))
            }
        }
    }

    private fun getLastTime(id: String, result: (Long) -> Unit) {
        fDb.collection(Constant.Collection.Chats.name).document(id).get().addOnSuccessListener {
            val lastTime = it.data?.get("lastTime").toString().toLong()
            result.invoke(lastTime)
        }
    }

    fun deleteFriend(chatId: String, friendId: String) {
        val myId = sharedPref.getString(Constant.Key.ID.name) ?: ""

        fDb.collection(Constant.Collection.Chats.name).document(chatId).delete().addOnSuccessListener { Log.d("Dunno", "Delete in Chats successfully") }.addOnFailureListener { Log.e("Dunno", "Delete Chats: ${it.message}") }

        fDb.collection(Constant.Collection.User.name).document(friendId).collection(Constant.Collection.Friends.name).whereEqualTo("chatId", chatId).whereEqualTo("friendId", myId).get().addOnSuccessListener {
            fDb.collection(Constant.Collection.User.name).document(friendId).collection(Constant.Collection.Friends.name).document(it.documents[0].id).delete()
        }

        fDb.collection(Constant.Collection.User.name).document(myId).collection(Constant.Collection.Friends.name).whereEqualTo("chatId", chatId).whereEqualTo("friendId", friendId).get().addOnSuccessListener {
            fDb.collection(Constant.Collection.User.name).document(myId).collection(Constant.Collection.Friends.name).document(it.documents[0].id).delete()
        }
    }


}