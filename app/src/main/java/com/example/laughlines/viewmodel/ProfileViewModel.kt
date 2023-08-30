package com.example.laughlines.viewmodel

import androidx.lifecycle.ViewModel
import com.example.laughlines.data.repo.ProfileRepository

class ProfileViewModel : ViewModel() {

    private val repository = ProfileRepository()


}