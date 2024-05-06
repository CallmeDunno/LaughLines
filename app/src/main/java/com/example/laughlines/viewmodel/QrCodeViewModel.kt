package com.example.laughlines.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.laughlines.repository.QrCodeRepository
import com.example.laughlines.model.QrResult
import com.example.laughlines.utils.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class QrCodeViewModel @Inject constructor(private val repository: QrCodeRepository) : ViewModel() {

    fun getInformation(myId: String, id: String) : MutableLiveData<UiState<QrResult>> {
        val mLiveData = MutableLiveData<UiState<QrResult>>()
        viewModelScope.launch {
            repository.getInformation(myId, id) {
                mLiveData.postValue(it)
            }
        }
        return mLiveData
    }

    val requestLiveData = MutableLiveData<UiState<Boolean>>()
    fun requestFriend(myId: String, id: String) {
        viewModelScope.launch {
            repository.requestFriend(myId, id) {
                requestLiveData.postValue(it)
            }
        }
    }

    fun getMyName(id: String) : MutableLiveData<UiState<String>> {
        val mLiveData = MutableLiveData<UiState<String>>()
        viewModelScope.launch {
            repository.getMyInformation(id) {
                mLiveData.postValue(it)
            }
        }
        return mLiveData
    }
}