package com.example.laughlines.view.home.data

import androidx.recyclerview.widget.DiffUtil

data class Friend(val id: Int, val avatarUrl: String, val name: String){
    companion object FriendDiffUtil : DiffUtil.ItemCallback<Friend>() {
        override fun areItemsTheSame(oldItem: Friend, newItem: Friend): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Friend, newItem: Friend): Boolean {
            return oldItem == newItem
        }
    }
}