package com.example.laughlines.repository

import android.net.Uri
import com.example.laughlines.model.Account
import com.example.laughlines.utils.Constant
import com.example.laughlines.utils.SharedPreferencesManager
import com.example.laughlines.utils.UiState
import com.example.laughlines.utils.extensions.formatDateTime
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import javax.inject.Inject

class InformationRepository @Inject constructor(private val fDb: FirebaseFirestore, private val fStorage: FirebaseStorage, private val sharedPref: SharedPreferencesManager) {

    fun updateInformation(name: String, numberPhone: String, uri: Uri, result: (UiState<Boolean>) -> Unit) {
        result.invoke(UiState.Loading)
        val myId = sharedPref.getString(Constant.Key.ID.name) ?: Constant.ID_DEFAULT
        uploadImageToStorage(uri) {
            when(it) {
                is UiState.Loading -> {}
                is UiState.Failure -> { result.invoke(UiState.Failure(it.message)) }
                is UiState.Success -> {
                    val update = hashMapOf<String, Any>(
                        "name" to name, "numberPhone" to numberPhone, "avatarUrl" to it.data
                    )
                    fDb.collection(Constant.Collection.User.name)
                        .document(myId)
                        .update(update)
                        .addOnSuccessListener { result.invoke(UiState.Success(true)) }
                        .addOnFailureListener { exc -> result.invoke(UiState.Failure(exc.message)) }
                }
            }
        }
    }

    fun updateInformation(name: String, numberPhone: String, result: (UiState<Boolean>) -> Unit) {
        result.invoke(UiState.Loading)
        val myId = sharedPref.getString(Constant.Key.ID.name) ?: Constant.ID_DEFAULT
        val update = hashMapOf<String, Any>(
            "name" to name, "numberPhone" to numberPhone
        )
        fDb.collection(Constant.Collection.User.name)
            .document(myId)
            .update(update)
            .addOnSuccessListener { result.invoke(UiState.Success(true)) }
            .addOnFailureListener { exc -> result.invoke(UiState.Failure(exc.message)) }
    }

    fun getInformation(result: (UiState<Account>) -> Unit) {
        result.invoke(UiState.Loading)
        val myId = sharedPref.getString(Constant.Key.ID.name) ?: Constant.ID_DEFAULT
        result.invoke(UiState.Loading)
        fDb.collection(Constant.Collection.User.name)
            .document(myId)
            .get()
            .addOnSuccessListener {
                val name = it.data?.get("name").toString()
                val email = it.data?.get("email").toString()
                val status = it.data?.get("status").toString()
                val avatarUrl = if (it.data?.get("avatarUrl").toString() == "null") "" else it.data?.get("avatarUrl").toString()
                val numberPhone = it.data?.get("numberPhone").toString()
                result.invoke(UiState.Success(Account(myId, name, email, avatarUrl, status, numberPhone)))
            }
            .addOnFailureListener { result.invoke(UiState.Failure(it.message.toString())) }
    }

    private fun uploadImageToStorage(uri: Uri, result: (UiState<String>) -> Unit) {
        fStorage.reference
            .child("image/${formatDateTime(Constant.DateTimeFormat2)}")
            .putFile(uri)
            .addOnSuccessListener {
                it.storage
                    .downloadUrl
                    .addOnCompleteListener { downloadTask ->
                        if (downloadTask.isSuccessful){
                            result.invoke(UiState.Success(downloadTask.result.toString()))
                        }
                    }
                    .addOnFailureListener { exc -> result.invoke(UiState.Failure(exc.message.toString())) }
            }
            .addOnFailureListener {exc -> result.invoke(UiState.Failure(exc.message.toString())) }
    }

}