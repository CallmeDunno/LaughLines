package com.example.laughlines.model

data class Account(
    val id: String,
    override val name: String,
    override val email: String,
    val password: String?,
    override val avatarUrl: String = "https://firebasestorage.googleapis.com/v0/b/chat-application-6b4b9.appspot.com/o/user.png?alt=media&token=55e48478-d441-4945-8f42-03f84101bac0"
) : Person(name, email, avatarUrl)