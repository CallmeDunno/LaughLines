package com.example.laughlines.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.laughlines.model.Account
import com.example.laughlines.repository.LoginRepository
import com.example.laughlines.utils.UiState
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
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
        viewModelScope.launch(Dispatchers.IO) {
            repository.checkAccountExists(user.email!!) {
                when(it) {
                    is UiState.Loading -> {}
                    is UiState.Failure -> {}
                    is UiState.Success -> {
                        if (it.data) repository.saveUserToFireStore(user)
                        else repository.getIdDocument(user.uid) {state ->
                            when (state) {
                                is UiState.Loading -> {}
                                is UiState.Failure -> {}
                                is UiState.Success -> {
                                    repository.addIdToSharedPref(state.data.first)
                                }
                            }
                        }
                    }
                }
            }
        }
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

    fun getID(uid: String) : MutableLiveData<UiState<Pair<String, String>>> {
        val mLiveData = MutableLiveData<UiState<Pair<String, String>>>()
        repository.getIdDocument(uid) {
            mLiveData.postValue(it)
        }
        return mLiveData
    }

}