package com.example.laughlines.ui.chatbot

import android.graphics.Bitmap
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatbotViewModel @Inject constructor(private val repository: ChatbotRepository) : ViewModel() {

    private var _state = MutableLiveData<Chatbot>()
    val state get() = _state

    fun sendMessage(message: String) {
        CoroutineScope(Job() + Dispatchers.IO).launch {
            _state.postValue(repository.getResponse(message))
        }
    }

    fun sendMessage(bitmap: Bitmap) {
        CoroutineScope(Job() + Dispatchers.IO).launch {
            _state.postValue(repository.getResponse(bitmap))
        }
    }

}