package com.example.laughlines.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.laughlines.data.repo.ChatRepository
import com.example.laughlines.model.Friend
import com.example.laughlines.model.Messages
import com.example.laughlines.utils.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(private val repository: ChatRepository) : ViewModel() {

    fun fetchMessage(cid: String): MutableLiveData<UiState<List<Messages>>> {
        var mutableLiveData = MutableLiveData<UiState<List<Messages>>>()
        viewModelScope.launch {
            repository.getMessages(cid) {
                mutableLiveData.postValue(it)
            }
        }
        return mutableLiveData
    }

    fun insertMessage(messages: Messages, cid: String): MutableLiveData<UiState<String>> {
        val mutableLiveData = MutableLiveData<UiState<String>>()
        viewModelScope.launch {
            repository.insertMessage(messages, cid) {
                mutableLiveData.postValue(it)
            }
        }
        return mutableLiveData
    }

    fun fetchFriend(fid: String, cid: String): MutableLiveData<UiState<Friend>> {
        var mutableLiveData = MutableLiveData<UiState<Friend>>()
        viewModelScope.launch {
            repository.getFriend(fid, cid) {
                mutableLiveData.postValue(it)
            }
        }
        return mutableLiveData
    }
}