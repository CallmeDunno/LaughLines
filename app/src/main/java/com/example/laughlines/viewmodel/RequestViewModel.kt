package com.example.laughlines.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.laughlines.repository.RequestRepository
import com.example.laughlines.model.QrResult
import com.example.laughlines.model.RequestModel2
import com.example.laughlines.utils.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RequestViewModel @Inject constructor(private val repository: RequestRepository) : ViewModel() {

    fun getRequest(): MutableLiveData<UiState<List<RequestModel2>>> {
        val mLiveData = MutableLiveData<UiState<List<RequestModel2>>>()
        viewModelScope.launch {
            repository.getAllRequest {
                mLiveData.postValue(it)
            }
        }
        return mLiveData
    }

    fun deleteRequest(id: String) : MutableLiveData<UiState<Boolean>> {
        val mLiveData = MutableLiveData<UiState<Boolean>>()
        viewModelScope.launch {
            repository.deleteRequest(id){
                mLiveData.postValue(it)
            }
        }
        return mLiveData
    }

    fun acceptRequest(myId: String, yourId: String) {
        repository.acceptRequest(myId, yourId)
    }

    fun getInformation(id: String) : MutableLiveData<UiState<QrResult>> {
        val mLiveData = MutableLiveData<UiState<QrResult>>()
        viewModelScope.launch {
            repository.getInformation(id) {
                mLiveData.postValue(it)
            }
        }
        return mLiveData
    }
}