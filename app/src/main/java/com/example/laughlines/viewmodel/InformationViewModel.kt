package com.example.laughlines.viewmodel

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.laughlines.model.Account
import com.example.laughlines.repository.InformationRepository
import com.example.laughlines.utils.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class InformationViewModel @Inject constructor(private val repository: InformationRepository) : ViewModel() {

    fun updateInformation(name: String, numberPhone: String, uri: Uri) : MutableLiveData<UiState<Boolean>> {
        val mLiveData = MutableLiveData<UiState<Boolean>>()
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateInformation(name, numberPhone, uri) {
                mLiveData.postValue(it)
            }
        }
        return mLiveData
    }

    fun updateInformation(name: String, numberPhone: String) : MutableLiveData<UiState<Boolean>> {
        val mLiveData = MutableLiveData<UiState<Boolean>>()
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateInformation(name, numberPhone) {
                mLiveData.postValue(it)
            }
        }
        return mLiveData
    }

    fun getInformation() : MutableLiveData<UiState<Account>> {
        val mLiveData = MutableLiveData<UiState<Account>>()
        viewModelScope.launch(Dispatchers.IO) {
            repository.getInformation {
                mLiveData.postValue(it)
            }
        }
        return mLiveData
    }

}