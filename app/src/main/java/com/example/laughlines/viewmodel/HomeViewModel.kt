package com.example.laughlines.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.laughlines.data.repo.HomeRepository
import com.example.laughlines.model.Friend
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {
    private val repository = HomeRepository()

//    fun getAccountList(): MutableLiveData<List<User>> {
//        val mutableLiveData = MutableLiveData<List<User>>()
//        val list = ArrayList<User>()
//        viewModelScope.launch(Dispatchers.IO) {
//            val def = async {
//                for (doc in repository.getAllAccount()) {
//
//                }
//                return@async list
//            }
//            mutableLiveData.postValue(def.await())
//        }
//        return mutableLiveData
//    }

    fun fetchFriendList(uid: String): MutableLiveData<List<Friend>> {
        val mutableLiveData = MutableLiveData<List<Friend>>()
        val list = ArrayList<Friend>()
        viewModelScope.launch {
            val def = async {
                val did = repository.getUserById(uid).documents[0].id
                for (doc in repository.getFriendList(did)) {
                    val cid = doc.data["cid"].toString()
                    val fid = doc.data["fid"].toString()
                    for (u in repository.getUserById(fid)){
                        val name = u.data["name"].toString()
                        val avatarUrl = u.data["avatarUrl"].toString()
                        list.add(Friend(fid, cid, name, avatarUrl))
                    }
                }
                return@async list
            }
            mutableLiveData.postValue(def.await())
        }
        return mutableLiveData
    }

}