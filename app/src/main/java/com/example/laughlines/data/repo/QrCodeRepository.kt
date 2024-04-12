package com.example.laughlines.data.repo

import android.util.Log
import com.example.laughlines.model.Account
import com.example.laughlines.model.QrResult
import com.example.laughlines.model.RequestModel
import com.example.laughlines.utils.Constant
import com.example.laughlines.utils.SharedPreferencesManager
import com.example.laughlines.utils.UiState
import com.example.laughlines.utils.extensions.formatDateTime
import com.google.firebase.firestore.FirebaseFirestore
import javax.inject.Inject

class QrCodeRepository @Inject constructor(private val fDb: FirebaseFirestore, private val sharedPreManager: SharedPreferencesManager) {

    fun getInformation(myId: String, id: String, result: (UiState<QrResult>) -> Unit) {
        result.invoke(UiState.Loading)
        checkFriendExists(myId, id) {
            when(it) {
                is UiState.Loading -> {}
                is UiState.Failure -> { result.invoke(UiState.Failure(it.message)) }
                is UiState.Success -> {
                    if (it.data) {
                        getSumFriend(id) { sum ->
                            getInformation(id) {account ->
                                result.invoke(UiState.Success(QrResult(account.id, account.avatar, account.name, account.email, sum)))
                            }
                        }
                    }
                }
            }
        }
    }

    fun requestFriend(myId: String, id: String, result: (UiState<Boolean>) -> Unit) {
        result.invoke(UiState.Loading)
        checkRequestExists(myId, id) {
            when (it) {
                is UiState.Loading -> {}
                is UiState.Failure -> { result.invoke(UiState.Failure(it.message)) }
                is UiState.Success -> {
                    if (it.data) {
                        val requestModel = RequestModel(myId, formatDateTime())
                        request(requestModel, id) { requestState ->
                            when (requestState) {
                                is UiState.Loading -> {}
                                is UiState.Failure -> { result.invoke(UiState.Failure(requestState.message)) }
                                is UiState.Success -> { result.invoke(UiState.Success(true))}
                            }
                        }
                    } else {
                        result.invoke(UiState.Success(false))
                    }
                }
            }
        }
    }

    private fun request(requestModel: RequestModel, id: String, result: (UiState<Boolean>) -> Unit) {
        fDb.collection(Constant.Collection.User.name)
            .document(id)
            .collection(Constant.Collection.Requests.name)
            .add(requestModel)
            .addOnSuccessListener { result.invoke(UiState.Success(true)) }
            .addOnFailureListener { result.invoke(UiState.Failure("Error: ${it.message}")) }
    }

    private fun getInformation(id: String, result: (Account) -> Unit) {
        fDb.collection(Constant.Collection.User.name)
            .document(id)
            .get()
            .addOnSuccessListener {
                val id = it.id
                val avatar = it.data?.get("avatar").toString()
                val name = it.data?.get("name").toString()
                val email = it.data?.get("email").toString()
                val status = it.data?.get("status").toString()
                result.invoke(Account(id, name, email, avatar, status))
            }
            .addOnFailureListener {
                Log.e("Dunno", it.message.toString())
            }
    }

    private fun getSumFriend(id: String, result: (Int) -> Unit) {
        fDb.collection(Constant.Collection.User.name)
            .document(id)
            .collection(Constant.Collection.Friends.name)
            .get()
            .addOnSuccessListener {
                result.invoke(it.documents.size)
            }
            .addOnFailureListener { Log.e("Dunno", it.message.toString()) }
    }

    private fun checkFriendExists(myId: String, id: String, result: (UiState<Boolean>) -> Unit) {
        fDb.collection(Constant.Collection.User.name)
            .document(myId)
            .collection(Constant.Collection.Friends.name)
            .whereEqualTo("id", id)
            .get()
            .addOnSuccessListener {
                if (it.isEmpty) result.invoke(UiState.Success(true))
                else result.invoke(UiState.Success(false))
            }
            .addOnFailureListener { result.invoke(UiState.Failure("Error: ${it.message}")) }
    }

    private fun checkRequestExists(myId: String, id: String, result: (UiState<Boolean>) -> Unit) {
        fDb.collection(Constant.Collection.User.name)
            .document(id)
            .collection(Constant.Collection.Requests.name)
            .whereEqualTo("idRequest", myId)
            .get()
            .addOnSuccessListener {
                if (it.isEmpty) result.invoke(UiState.Success(true))
                else result.invoke(UiState.Success(false))
            }
            .addOnFailureListener { result.invoke(UiState.Failure("Error: ${it.message}")) }
    }

}