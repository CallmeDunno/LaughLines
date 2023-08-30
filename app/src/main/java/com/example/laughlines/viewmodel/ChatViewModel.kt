package com.example.laughlines.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.laughlines.data.repo.ChatRepository
import com.example.laughlines.model.Friend
import com.example.laughlines.model.Messages
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(private val repository: ChatRepository) : ViewModel() {

    fun fetchMessage(cid: String): MutableLiveData<List<Messages>> {
        var mutableLiveData = MutableLiveData<List<Messages>>()
        viewModelScope.launch {
            mutableLiveData = repository.getMessages(cid)
        }
        return mutableLiveData
    }

    fun insertMessage(messages: Messages, cid: String) {
        viewModelScope.launch {
            repository.insertMessage(messages, cid)
        }
    }

    fun fetchFriend(fid: String, cid: String): MutableLiveData<Friend> {
        var mutableLiveData = MutableLiveData<Friend>()
        viewModelScope.launch {
            mutableLiveData = repository.getFriend(fid, cid)
        }
        return mutableLiveData
    }
}