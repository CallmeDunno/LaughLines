package com.example.laughlines.ui.chat.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncDifferConfig
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.laughlines.databinding.ItemListMessageChatBinding
import com.example.laughlines.model.Messages

class ChatAdapter(private val uid: String) : ListAdapter<Messages, ChatAdapter.ChatVH>(AsyncDifferConfig.Builder(object : DiffUtil.ItemCallback<Messages>() {
    override fun areItemsTheSame(oldItem: Messages, newItem: Messages): Boolean {
        return oldItem.messageID == newItem.messageID
    }

    override fun areContentsTheSame(oldItem: Messages, newItem: Messages): Boolean {
        return oldItem == newItem
    }

}).build()) {

    inner class ChatVH(private val itemBinding: ItemListMessageChatBinding) : ViewHolder(itemBinding.root) {

        fun bindData(messages: Messages) {
            itemBinding.apply {
                if (uid == messages.sender) {
                    layoutRecipientChat.visibility = View.GONE
                    layoutSenderChat.visibility = View.VISIBLE
                    tvTimestampSenderChat.text = messages.timestamp
                    tvMessageSender.text = messages.message
                } else {
                    layoutSenderChat.visibility = View.GONE
                    layoutRecipientChat.visibility = View.VISIBLE
                    tvTimestampRecipientChat.text = messages.timestamp
                    tvMessageRecipient.text = messages.message
                }
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatVH {
        val itemBinding = ItemListMessageChatBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ChatVH(itemBinding)
    }

    override fun onBindViewHolder(holder: ChatVH, position: Int) {
        getItem(position)?.let {
            holder.bindData(it)
        }
    }

}