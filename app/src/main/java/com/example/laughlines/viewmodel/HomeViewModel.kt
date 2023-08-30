package com.example.laughlines.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.laughlines.data.repo.HomeRepository
import com.example.laughlines.model.Friend
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(private val repository: HomeRepository) : ViewModel() {

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