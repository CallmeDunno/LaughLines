package com.example.laughlines.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.laughlines.data.repo.RequestRepository
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

}