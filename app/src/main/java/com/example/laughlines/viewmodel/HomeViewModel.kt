package com.example.laughlines.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.laughlines.data.repo.HomeRepository
import com.example.laughlines.utils.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(private val repository: HomeRepository) : ViewModel() {

    fun getList(id: String) {
        repository.getListFriend(id)
    }

    fun getRequest() : MutableLiveData<UiState<Int>> {
        val mLiveData = MutableLiveData<UiState<Int>>()
        viewModelScope.launch {
            repository.getRequest {
                mLiveData.postValue(it)
            }
        }
        return mLiveData
    }

}