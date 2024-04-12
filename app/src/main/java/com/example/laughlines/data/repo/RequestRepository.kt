package com.example.laughlines.data.repo

import android.util.Log
import com.example.laughlines.model.RequestModel2
import com.example.laughlines.utils.Constant
import com.example.laughlines.utils.SharedPreferencesManager
import com.example.laughlines.utils.UiState
import com.google.firebase.firestore.FirebaseFirestore
import javax.inject.Inject

class RequestRepository @Inject constructor(private val fDb: FirebaseFirestore, private val sharedPref: SharedPreferencesManager) {

    fun getAllRequest(result: (UiState<List<RequestModel2>>) -> Unit) {
        result.invoke(UiState.Loading)
        fDb.collection(Constant.Collection.User.name)
            .document(sharedPref.getString(Constant.Key.ID.name)!!)
            .collection(Constant.Collection.Requests.name)
            .get()
            .addOnSuccessListener {
                val arr = ArrayList<RequestModel2>()
                for (i in it.documents){
                    val id = i.id
                    val idRequest = i.data!!["idRequest"].toString()
                    val timeRequest = i.data!!["timeRequest"].toString()

                    getAccount(idRequest) {str ->
                        val field = str.split("_")
                        arr.add(RequestModel2(id, idRequest, field[0], timeRequest, field[1]))
                        result.invoke(UiState.Success(arr))
                    }
                }
            }
            .addOnFailureListener { result.invoke(UiState.Failure("Error: ${it.message}")) }
    }

    fun deleteRequest(id: String, result: (UiState<Boolean>) -> Unit) {
        result.invoke(UiState.Loading)
        fDb.collection(Constant.Collection.User.name)
            .document(sharedPref.getString(Constant.Key.ID.name)!!)
            .collection(Constant.Collection.Requests.name)
            .document(id)
            .delete()
            .addOnSuccessListener { result.invoke(UiState.Success(true)) }
            .addOnFailureListener { result.invoke(UiState.Failure(it.message.toString())) }
    }

    private fun getAccount(id: String, result: (String) -> Unit) {
        fDb.collection(Constant.Collection.User.name)
            .document(id)
            .get()
            .addOnSuccessListener {
                val name = it.data!!["name"].toString()
                val avatarUrl = it.data!!["avatarUrl"].toString()
                result.invoke("${name}_$avatarUrl")
            }
            .addOnFailureListener {
                Log.e("Dunno", "Error: ${it.message}")
            }

    }

}