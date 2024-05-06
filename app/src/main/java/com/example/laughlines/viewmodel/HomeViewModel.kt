package com.example.laughlines.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.laughlines.model.Account
import com.example.laughlines.repository.HomeRepository
import com.example.laughlines.utils.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
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

    fun getFriendIdList() : MutableLiveData<UiState<List<String>>> {
        val mLiveData = MutableLiveData<UiState<List<String>>>()
        viewModelScope.launch(Dispatchers.IO) {
            repository.getFriendIdList {
                mLiveData.postValue(it)
            }
        }
        return mLiveData
    }

    fun getMyAccount() : MutableLiveData<Account> {
        val mLiveData = MutableLiveData<Account>()

        viewModelScope.launch(Dispatchers.IO) {
            repository.getMyAccount {
                mLiveData.postValue(it)
            }
        }
        return mLiveData
    }

}