package com.example.laughlines.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.laughlines.data.repo.ProfileRepository
import com.example.laughlines.model.Person
import com.example.laughlines.model.QrResult
import com.example.laughlines.utils.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(private val repository: ProfileRepository) : ViewModel() {

    fun getAccount(): MutableLiveData<UiState<Person>> {
        val mutableLiveData = MutableLiveData<UiState<Person>>()
//        repository.getAccount { mutableLiveData.postValue(it) }
        return mutableLiveData
    }



    fun signOut() = repository.signOut()

}