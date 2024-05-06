package com.example.laughlines.repository

import android.util.Log
import com.example.laughlines.model.Account
import com.example.laughlines.utils.Constant
import com.example.laughlines.utils.SharedPreferencesManager
import com.example.laughlines.utils.UiState
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
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
            .document(sharedPref.getString(Constant.Key.ID.name) ?: "")
            .collection(Constant.Collection.Requests.name)
            .get()
            .addOnSuccessListener { result.invoke(UiState.Success(it.documents.size)) }
            .addOnFailureListener { result.invoke(UiState.Failure("Error: ${it.message}")) }
    }

    fun getFriendIdList(result: (UiState<List<String>>) -> Unit) {
        val myId = sharedPref.getString(Constant.Key.ID.name) ?: ""
        result.invoke(UiState.Loading)
        fDb.collection(Constant.Collection.User.name)
            .document(myId)
            .collection(Constant.Collection.Friends.name)
            .get()
            .addOnSuccessListener {
                var id = ""
                val arr = ArrayList<String>()
                if (it.documents.size == 0) {
                    result.invoke(UiState.Success(arr))
                } else {
                    for (str in it.documents) {
                        id = str.data?.get("friendId").toString()
                        arr.add(id)
                        result.invoke(UiState.Success(arr))
                    }
                }

            }
            .addOnFailureListener { result.invoke(UiState.Failure(it.message.toString())) }
    }

    fun getMyAccount(result: (Account) -> Unit) {
        val myId = sharedPref.getString(Constant.Key.ID.name) ?: ""

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

}