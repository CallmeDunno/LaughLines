package com.example.laughlines.view.login.data

data class User(
    val id: String,
    val name: String,
    val email: String,
    val password: String?,
    val avatarUrl: String?
)