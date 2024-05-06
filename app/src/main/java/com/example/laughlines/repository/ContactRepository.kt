package com.example.laughlines.repository

import android.util.Log
import com.example.laughlines.model.Account
import com.example.laughlines.model.Contact
import com.example.laughlines.utils.Constant
import com.example.laughlines.utils.SharedPreferencesManager
import com.example.laughlines.utils.UiState
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

class ContactRepository @Inject constructor(private val fDb: FirebaseFirestore, private val sharedPref: SharedPreferencesManager) {

    fun getAllFriend(result: (UiState<List<Contact>>) -> Unit) {
        result.invoke(UiState.Loading)
        fDb.collection(Constant.Collection.User.name).document(sharedPref.getString(Constant.Key.ID.name) ?: "").collection(Constant.Collection.Friends.name).get().addOnSuccessListener {
            val arr = ArrayList<Contact>()
            if (it.isEmpty) result.invoke(UiState.Success(arr))
            else {
                for (i in it) {
                    val id = i.id
                    val chatId = i.data["chatId"].toString()
                    val friendId = i.data["friendId"].toString()
                    getInformation(friendId) { account ->
                        arr.add(Contact(id, chatId, friendId, account))
                        result.invoke(UiState.Success(arr))
                    }
                }
            }
        }.addOnFailureListener { result.invoke(UiState.Failure(it.message.toString())) }
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

    fun deleteFriend(chatId: String, friendId: String) {
        val myId = sharedPref.getString(Constant.Key.ID.name) ?: ""

        fDb.collection(Constant.Collection.Chats.name)
            .document(chatId)
            .delete()
            .addOnSuccessListener { Log.d("Dunno", "Delete in Chats successfully") }
            .addOnFailureListener { Log.e("Dunno", "Delete Chats: ${it.message}") }

        fDb.collection(Constant.Collection.User.name)
            .document(friendId)
            .collection(Constant.Collection.Friends.name)
            .whereEqualTo("chatId", chatId)
            .whereEqualTo("friendId", myId)
            .get()
            .addOnSuccessListener {
                fDb.collection(Constant.Collection.User.name)
                    .document(friendId)
                    .collection(Constant.Collection.Friends.name)
                    .document(it.documents[0].id)
                    .delete()
            }

        fDb.collection(Constant.Collection.User.name)
            .document(myId)
            .collection(Constant.Collection.Friends.name)
            .whereEqualTo("chatId", chatId)
            .whereEqualTo("friendId", friendId)
            .get()
            .addOnSuccessListener {
                fDb.collection(Constant.Collection.User.name)
                    .document(myId)
                    .collection(Constant.Collection.Friends.name)
                    .document(it.documents[0].id)
                    .delete()
            }
    }


}