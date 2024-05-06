package com.example.laughlines.repository

import android.util.Log
import com.example.laughlines.model.Account
import com.example.laughlines.model.QrResult
import com.example.laughlines.model.RequestModel2
import com.example.laughlines.utils.Constant
import com.example.laughlines.utils.SharedPreferencesManager
import com.example.laughlines.utils.UiState
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class RequestRepository @Inject constructor(private val fDb: FirebaseFirestore, private val sharedPref: SharedPreferencesManager) {

    fun getAllRequest(result: (UiState<List<RequestModel2>>) -> Unit) {
        result.invoke(UiState.Loading)
        fDb.collection(Constant.Collection.User.name).document(sharedPref.getString(Constant.Key.ID.name)!!).collection(Constant.Collection.Requests.name).get().addOnSuccessListener {
            val arr = ArrayList<RequestModel2>()
            for (i in it.documents) {
                val id = i.id
                val idRequest = i.data!!["idRequest"].toString()
                val timeRequest = i.data!!["timeRequest"].toString()

                getAccount(idRequest) { str ->
                    val field = str.split("_")
                    arr.add(RequestModel2(id, idRequest, field[0], timeRequest, field[1]))
                    result.invoke(UiState.Success(arr))
                }
            }
        }.addOnFailureListener { result.invoke(UiState.Failure("Error: ${it.message}")) }
    }

    fun deleteRequest(id: String, result: (UiState<Boolean>) -> Unit) {
        result.invoke(UiState.Loading)
        fDb.collection(Constant.Collection.User.name).document(sharedPref.getString(Constant.Key.ID.name)!!).collection(Constant.Collection.Requests.name).document(id).delete().addOnSuccessListener { result.invoke(UiState.Success(true)) }.addOnFailureListener { result.invoke(UiState.Failure(it.message.toString())) }
    }

    private fun getAccount(id: String, result: (String) -> Unit) {
        fDb.collection(Constant.Collection.User.name).document(id).get().addOnSuccessListener {
            val name = it.data!!["name"].toString()
            val avatarUrl = it.data!!["avatarUrl"].toString()
            result.invoke("${name}_$avatarUrl")
        }.addOnFailureListener {
            Log.e("Dunno", "Error: ${it.message}")
        }

    }

    fun acceptRequest(myId: String, yourID: String) {
        CoroutineScope(Job() + Dispatchers.IO).launch {
            val chat = hashMapOf("id1" to myId, "id2" to yourID)
            val def = addToChat(chat)
            launch { addFriend(myId, yourID, def) }
            launch { addFriend(yourID, myId, def) }
        }
    }

    private suspend fun addToChat(chat: HashMap<String, String>) = fDb.collection(Constant.Collection.Chats.name).add(chat).await().id

    private fun addFriend(id1: String, id2: String, chatId: String) {
        val data = hashMapOf("friendId" to id2, "chatId" to chatId)

        fDb.collection(Constant.Collection.User.name).document(id1).collection(Constant.Collection.Friends.name).add(data)
    }

    fun getInformation(id: String, result: (UiState<QrResult>) -> Unit) {
        result.invoke(UiState.Loading)
        getSumFriend(id) { sum ->
            getInfor(id) { account ->
                result.invoke(UiState.Success(QrResult(account.id, account.avatar, account.name, account.email, sum)))
            }
        }
    }

    private fun getSumFriend(id: String, result: (Int) -> Unit) {
        fDb.collection(Constant.Collection.User.name).document(id).collection(Constant.Collection.Friends.name).get().addOnSuccessListener {
                result.invoke(it.documents.size)
            }.addOnFailureListener { Log.e("Dunno", it.message.toString()) }
    }

    private fun getInfor(id: String, result: (Account) -> Unit) {
        fDb.collection(Constant.Collection.User.name).document(id).get().addOnSuccessListener {
                val id = it.id
                val avatar = it.data?.get("avatar").toString()
                val name = it.data?.get("name").toString()
                val email = it.data?.get("email").toString()
                val status = it.data?.get("status").toString()
                val numberPhone = it.data?.get("numberPhone").toString()
                result.invoke(Account(id, name, email, avatar, status, numberPhone))
            }.addOnFailureListener {
                Log.e("Dunno", it.message.toString())
            }
    }
}