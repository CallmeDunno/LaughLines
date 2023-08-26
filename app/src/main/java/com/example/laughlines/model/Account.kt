package com.example.laughlines.model

data class Account(
    val id: String,
    val name: String,
    val email: String,
    val password: String?,
    val avatarUrl: String?
)