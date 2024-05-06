package com.example.laughlines.viewmodel

import androidx.lifecycle.ViewModel
import com.example.laughlines.repository.ChatRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(private val repository: ChatRepository) : ViewModel() {

}