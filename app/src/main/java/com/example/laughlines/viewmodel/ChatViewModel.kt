package com.example.laughlines.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.laughlines.model.Account
import com.example.laughlines.model.Messages
import com.example.laughlines.repository.ChatRepository
import com.example.laughlines.utils.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(private val repository: ChatRepository) : ViewModel() {

    fun getInformationOfFriend(id: String) : MutableLiveData<UiState<Account>> {
        val mLiveData = MutableLiveData<UiState<Account>>()
        viewModelScope.launch(Dispatchers.IO) {
            repository.getInformationOfFriend(id) {
                mLiveData.postValue(it)
            }
        }
        return mLiveData
    }

    fun getMessageList(chatId: String) : LiveData<List<Messages>> {
        val mLiveData = MutableLiveData<List<Messages>>()
        viewModelScope.launch(Dispatchers.IO) {
            repository.getMessageList(chatId) {
                mLiveData.postValue(it)
            }
        }
        return mLiveData
    }

    fun pushMessage(chatId: String, messages: Messages) : MutableLiveData<UiState<Boolean>> {
        val mLiveData = MutableLiveData<UiState<Boolean>>()
        viewModelScope.launch(Dispatchers.IO) {
            repository.pushMessage(chatId, messages) {
                mLiveData.postValue(it)
            }
        }
        return mLiveData
    }

}