package com.example.laughlines.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.laughlines.model.Contact
import com.example.laughlines.repository.ContactRepository
import com.example.laughlines.utils.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ContactViewModel @Inject constructor(private val repository: ContactRepository) : ViewModel() {

    fun getAllFriend() : MutableLiveData<UiState<List<Contact>>> {
        val mLiveData = MutableLiveData<UiState<List<Contact>>>()

        viewModelScope.launch(Dispatchers.IO) {
            repository.getAllFriend { mLiveData.postValue(it) }
        }

        return mLiveData
    }

    fun deleteFriend(chatId: String, friendId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteFriend(chatId, friendId)
        }
    }
}