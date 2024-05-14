package com.example.laughlines.repository

import android.net.Uri
import com.example.laughlines.model.Messages
import com.example.laughlines.utils.Constant
import com.example.laughlines.utils.UiState
import com.example.laughlines.utils.extensions.formatDateTime
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import javax.inject.Inject

class ImageRepository @Inject constructor(private val fDb: FirebaseFirestore, private val fStorage: FirebaseStorage) {

    fun uploadImageToStorage(uri: Uri, result: (UiState<String>) -> Unit) {
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

    fun pushMessage(chatId: String, messages: Messages, result: (UiState<Boolean>) -> Unit) {
        result.invoke(UiState.Loading)
        fDb.collection(Constant.Collection.Chats.name)
            .document(chatId)
            .collection(Constant.Collection.Messages.name)
            .add(messages)
            .addOnSuccessListener { result.invoke(UiState.Success(true)) }
            .addOnFailureListener { result.invoke(UiState.Failure(it.message.toString())) }
    }

}