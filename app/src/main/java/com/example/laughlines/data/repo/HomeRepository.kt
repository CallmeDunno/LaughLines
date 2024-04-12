package com.example.laughlines.data.repo

import android.util.Log
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
            .document(sharedPref.getString(Constant.Key.ID.name)!!)
            .collection(Constant.Collection.Requests.name)
            .get()
            .addOnSuccessListener { result.invoke(UiState.Success(it.documents.size)) }
            .addOnFailureListener { result.invoke(UiState.Failure("Error: ${it.message}")) }
    }

}