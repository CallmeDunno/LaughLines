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
import java.util.*
import javax.inject.Inject

class HomeRepository @Inject constructor(private val fDb: FirebaseFirestore, private val sharedPref: SharedPreferencesManager) {

    fun getListFriend(id: String) {
        fDb.collection(Constant.Collection.User.name)
            .document(id)
            .collection(Constant.Collection.Friends.name)
            .addSnapshotListener { value, error ->
                if (error != null) {
                    Log.e("Dunno", "Error: $error")
                    return@addSnapshotListener
                }
                Log.w("Dunno", value!!.documents.size.toString())
                for (v in value!!.documentChanges) {
                    when(v.type) {
                        DocumentChange.Type.ADDED -> {
                            Log.d("Dunno", "Add: ${v.document.id}")
                        }
                        DocumentChange.Type.MODIFIED -> {
                            Log.d("Dunno", "Modified: ${v.document.id}")
                        }
                        DocumentChange.Type.REMOVED -> {
                            Log.d("Dunno", "Remove: ${v.document.id}")
                        }
                    }
                }
            }
    }

    fun getRequest(result: (UiState<Int>) -> Unit) {
        result.invoke(UiState.Loading)
        fDb.collection(Constant.Collection.User.name)
            .document(sharedPref.getString(Constant.Key.ID.name) ?: Constant.ID_DEFAULT)
            .collection(Constant.Collection.Requests.name)
            .get()
            .addOnSuccessListener { result.invoke(UiState.Success(it.documents.size)) }
            .addOnFailureListener { result.invoke(UiState.Failure("Error: ${it.message}")) }
    }

//    fun getAllFriend(result: (UiState<List<Contact>>) -> Unit) {
//        result.invoke(UiState.Loading)
//        fDb.collection(Constant.Collection.User.name).document(sharedPref.getString(Constant.Key.ID.name) ?: Constant.ID_DEFAULT)
//            .collection(Constant.Collection.Friends.name).get().addOnSuccessListener {
//            val arr = ArrayList<Contact>()
//            if (it.isEmpty) result.invoke(UiState.Success(arr))
//            else {
//                for (i in it) {
//                    val id = i.id
//                    val chatId = i.data["chatId"].toString()
//                    val friendId = i.data["friendId"].toString()
//                    getInformation(friendId) { account ->
//                        getLastTime(chatId) {lastTime ->
//                            arr.add(Contact(id, chatId, friendId, account, lastTime))
//                            Collections.sort(arr, Contact.Companion.SortByTimestamp())
//                            result.invoke(UiState.Success(arr))
//                        }
//                    }
//                }
//            }
//        }.addOnFailureListener { result.invoke(UiState.Failure(it.message.toString())) }
//    }

    fun getAllFriend(result: (List<Contact>) -> Unit) {
        val arr = ArrayList<Contact>()
        fDb.collection(Constant.Collection.User.name)
            .document(sharedPref.getString(Constant.Key.ID.name) ?: Constant.ID_DEFAULT)
            .collection(Constant.Collection.Friends.name)
            .addSnapshotListener { value, error ->
                if (error != null) {
                    Log.e("Dunno", "Error: $error")
                    return@addSnapshotListener
                }
                Log.w("Dunno", value!!.documents.size.toString())
                for (v in value.documentChanges) {
                    when(v.type) {
                        DocumentChange.Type.ADDED -> {
                            val id = v.document.id
                            val chatId = v.document.data["chatId"].toString()
                            val friendId = v.document.data["friendId"].toString()
                            getInformation(friendId) { account ->
                                getLastTime(chatId) {lastTime ->
                                    arr.add(Contact(id, chatId, friendId, account, lastTime))
                                    Collections.sort(arr, Contact.Companion.SortByTimestamp())
                                    result.invoke(arr)
                                }
                            }
                        }
                        DocumentChange.Type.MODIFIED -> {}
                        DocumentChange.Type.REMOVED -> {}
                    }
                }
            }
    }

    fun getMyAccount(result: (Account) -> Unit) {
        val myId = sharedPref.getString(Constant.Key.ID.name) ?: Constant.ID_DEFAULT

        fDb.collection(Constant.Collection.User.name)
            .document(myId)
            .get()
            .addOnSuccessListener {
                val name = it.data?.get("name").toString()
                val email = it.data?.get("email").toString()
                val avatar = it.data?.get("avatarUrl").toString()
                val status = it.data?.get("status").toString()
                val numberPhone = it.data?.get("numberPhone").toString()
                result.invoke(Account(myId, name, email, avatar, status, numberPhone))
            }
            .addOnFailureListener { Log.e("Dunno", it.message.toString()) }
    }

    fun updateStatus(status: String, result: (UiState<Boolean>) -> Unit) {
        result.invoke(UiState.Loading)
        val myId = sharedPref.getString(Constant.Key.ID.name) ?: Constant.ID_DEFAULT

        fDb.collection(Constant.Collection.User.name)
            .document(myId)
            .update("status", status)
            .addOnSuccessListener { result.invoke(UiState.Success(true)) }
            .addOnFailureListener { result.invoke(UiState.Failure(it.message.toString())) }
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
        fDb.collection(Constant.Collection.Chats.name)
            .document(id)
            .get()
            .addOnSuccessListener {
                val lastTime = it.data?.get("lastTime").toString().toLong()
                result.invoke(lastTime)
            }

    }
}