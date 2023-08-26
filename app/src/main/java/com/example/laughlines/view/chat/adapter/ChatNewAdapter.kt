package com.example.laughlines.view.chat.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncDifferConfig
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.laughlines.databinding.ItemListMessageChatBinding
import com.example.laughlines.model.Messages

//Class tham khảo, không sử dụng

class ChatNewAdapter(private val uid: String, private val onClick: (Messages) -> Unit) :
    ListAdapter<Messages, ChatNewAdapter.ViewHolder>(
        AsyncDifferConfig.Builder(object : DiffUtil.ItemCallback<Messages>() {
            override fun areItemsTheSame(
                oldItem: Messages, newItem: Messages
            ): Boolean {
                return oldItem.messageID == newItem.messageID
            }

            override fun areContentsTheSame(
                oldItem: Messages, newItem: Messages
            ): Boolean {
                return oldItem == newItem
            }

        }).build()
    ) {

    class ViewHolder(private val binding: ItemListMessageChatBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(onClick: (Messages) -> Unit, item: Messages, uid: String) {

            itemView.setOnClickListener {
                onClick.invoke(item)
            }
//            binding.apply {
//                if (uid == item.sender) {
//                    layoutRecipientChat.visibility = View.GONE
//                    layoutSenderChat.visibility = View.VISIBLE
//                    tvTimestampSenderChat.text = item.timestamp
//                    tvMessageSender.text = item.message
//                } else {
//                    layoutSenderChat.visibility = View.GONE
//                    layoutRecipientChat.visibility = View.VISIBLE
//                    tvTimestampRecipientChat.text = item.timestamp
//                    tvMessageRecipient.text = item.message
//                }
//            }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemListMessageChatBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(onClick, getItem(position), uid)
    }
}