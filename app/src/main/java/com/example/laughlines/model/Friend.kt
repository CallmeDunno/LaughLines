package com.example.laughlines.model

import androidx.recyclerview.widget.DiffUtil

/** Explain fid & cid
 * fid: id of your friend
 * cid: id of chat room that you and your friend created
 * */

data class Friend(val fid: String, val cid: String, val name: String, val avatarUrl: String?) {
    companion object FriendDiffUtil : DiffUtil.ItemCallback<Friend>() {
        override fun areItemsTheSame(oldItem: Friend, newItem: Friend): Boolean {
            return oldItem.fid == newItem.fid
        }

        override fun areContentsTheSame(oldItem: Friend, newItem: Friend): Boolean {
            return oldItem == newItem
        }


    }
}