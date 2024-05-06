package com.example.laughlines.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.laughlines.repository.LoginRepository
import com.example.laughlines.model.Account
import com.example.laughlines.utils.UiState
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(private val repository: LoginRepository) : ViewModel() {

    fun createAccount(email: String, password: String): MutableLiveData<UiState<String>> {
        val mutableLiveData = MutableLiveData<UiState<String>>()
        repository.createAccount(email, password) {
            mutableLiveData.postValue(it)
        }
        return mutableLiveData
    }

    fun saveUserToFireStore(account: Account) {
        repository.saveUserToFireStore(account)
    }

    fun saveUserToFireStore(user: FirebaseUser) {
        repository.saveUserToFireStore(user)
    }

    fun signIn(email: String, password: String): MutableLiveData<UiState<String>> {
        val mutableLiveData = MutableLiveData<UiState<String>>()
        repository.signIn(email, password) {
            mutableLiveData.postValue(it)
        }
        return mutableLiveData
    }

    fun signInWithCredential(credential: AuthCredential): MutableLiveData<UiState<FirebaseUser>> {
        val mutableLiveData = MutableLiveData<UiState<FirebaseUser>>()
        repository.signInWithCredential(credential) {
            mutableLiveData.postValue(it)
        }
        return mutableLiveData
    }

    fun getID(uid: String) : MutableLiveData<UiState<String>> {
        val mLiveData = MutableLiveData<UiState<String>>()
        repository.getIdDocument(uid) {
            mLiveData.postValue(it)
        }
        return mLiveData
    }

    fun resetPassword() {
        repository.resetPassword()
    }
}