package com.example.laughlines.viewmodel

import androidx.lifecycle.ViewModel
import com.example.laughlines.data.repo.ProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(private val repository: ProfileRepository) : ViewModel() {




}