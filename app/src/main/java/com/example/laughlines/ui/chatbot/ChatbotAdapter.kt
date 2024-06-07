package com.example.laughlines.ui.chatbot

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.AsyncDifferConfig
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.laughlines.databinding.ItemMessageChatbotBinding
import com.example.laughlines.utils.extensions.hide
import com.example.laughlines.utils.extensions.show

class ChatbotAdapter : ListAdapter<Chatbot, ChatbotAdapter.ChatbotVH>(AsyncDifferConfig.Builder(object: DiffUtil.ItemCallback<Chatbot>(){
    override fun areItemsTheSame(oldItem: Chatbot, newItem: Chatbot): Boolean {
        return false
    }

    override fun areContentsTheSame(oldItem: Chatbot, newItem: Chatbot): Boolean {
        return oldItem == newItem
    }
}).build()) {

    inner class ChatbotVH(private val binding: ItemMessageChatbotBinding) : ViewHolder(binding.root) {

        fun bindData(chatbot: Chatbot) {
            binding.apply {
                item = chatbot
                layoutRecipientChat.isVisible = !chatbot.byUser
                layoutSenderChat.isVisible = chatbot.byUser
                if (chatbot.byUser && chatbot.message == "") {
                    cvSender.show()
                    tvSender.hide()
                }
                if (chatbot.byUser && chatbot.message != "") {
                    cvSender.hide()
                    tvSender.show()
                }
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatbotVH {
        return ChatbotVH(ItemMessageChatbotBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ChatbotVH, position: Int) {
        holder.bindData(getItem(position))
    }

}