package com.example.laughlines.view.home.data

import androidx.recyclerview.widget.DiffUtil

data class Chat(val id: Int, val avatarImg: String, val name: String, val message: String) {
    companion object ChatDiffUtil : DiffUtil.ItemCallback<Chat>() {
        override fun areItemsTheSame(oldItem: Chat, newItem: Chat): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Chat, newItem: Chat): Boolean {
            return oldItem == newItem
        }

    }
}