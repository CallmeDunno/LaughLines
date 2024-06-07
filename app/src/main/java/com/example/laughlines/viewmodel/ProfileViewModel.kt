package com.example.laughlines.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.laughlines.model.Person
import com.example.laughlines.repository.ProfileRepository
import com.example.laughlines.utils.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(private val repository: ProfileRepository) : ViewModel() {

    fun getAccount(): MutableLiveData<UiState<Person>> {
        val mutableLiveData = MutableLiveData<UiState<Person>>()
        CoroutineScope(Job() + Dispatchers.IO).launch {
            repository.getAccount { mutableLiveData.postValue(it) }
        }
        return mutableLiveData
    }

    fun resetPassword(email: String) : MutableLiveData<UiState<Boolean>> {
        val mLiveData = MutableLiveData<UiState<Boolean>>()
        CoroutineScope(Job() + Dispatchers.IO).launch {
            repository.resetPassword(email){
                mLiveData.postValue(it)
            }
        }
        return mLiveData
    }

    fun signOut() = repository.signOut()

}