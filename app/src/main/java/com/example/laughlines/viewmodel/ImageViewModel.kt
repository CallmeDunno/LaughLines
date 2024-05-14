package com.example.laughlines.viewmodel

import android.net.Uri
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.laughlines.model.Messages
import com.example.laughlines.repository.ImageRepository
import com.example.laughlines.utils.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ImageViewModel @Inject constructor(private val repository: ImageRepository) : ViewModel() {

    fun pushMessage(chatId: String, messages: Messages) : MutableLiveData<UiState<Boolean>> {
        val mLiveData = MutableLiveData<UiState<Boolean>>()
        viewModelScope.launch(Dispatchers.IO) {
            repository.uploadImageToStorage(Uri.parse(messages.message)) {
                when (it) {
                    is UiState.Loading -> {}
                    is UiState.Success -> {
                        repository.pushMessage(chatId, Messages(it.data, messages.sender, messages.timestamp, messages.type)) { uiState ->
                            mLiveData.postValue(uiState)
                        }
                    }
                    is UiState.Failure -> {
                        Log.e("Dunno", it.message.toString())
                    }
                }
            }
        }
        return mLiveData
    }



}