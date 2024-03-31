package com.example.laughlines.utils

sealed class UiState<out T> {
    data class Success<T>(val data: T) : UiState<T>()
    data class Failure<T>(val message: String?) : UiState<T>()
    object Loading: UiState<Nothing>()
}