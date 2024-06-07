package com.example.laughlines.ui.chat.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.AsyncDifferConfig
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.example.laughlines.databinding.ItemMessageBinding
import com.example.laughlines.model.Messages
import com.example.laughlines.utils.Constant
import com.example.laughlines.utils.EncryptMessages
import com.example.laughlines.utils.extensions.hide
import com.example.laughlines.utils.extensions.show

class MessageAdapter(private val onClick: (Messages) -> Unit) : ListAdapter<Messages, MessageAdapter.MessageVH>(AsyncDifferConfig.Builder(object : DiffUtil.ItemCallback<Messages>() {
    override fun areItemsTheSame(oldItem: Messages, newItem: Messages): Boolean {
        return oldItem.timestamp == newItem.timestamp
    }

    override fun areContentsTheSame(oldItem: Messages, newItem: Messages): Boolean {
        return oldItem == newItem
    }
}).build()) {

    private var myId: String = ""
    private val encryptMessages = EncryptMessages()

    fun setMyId(id: String) {
        this.myId = id
    }

    fun createKey(str: String) {
        encryptMessages.createKey(str)
    }

    inner class MessageVH(private val binding: ItemMessageBinding) : ViewHolder(binding.root) {
        fun bindData(message: Messages) {
//            val text =
//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) encryptMessages.decode(m.message) else
//                m.message
//            val message = Messages(text, m.sender, m.timestamp, m.type)
            itemView.setOnClickListener { onClick.invoke(message) }
            binding.apply {
                layoutRecipientChat.isVisible = message.sender != myId
                layoutSenderChat.isVisible = message.sender == myId

                if (message.sender == myId) {
                    when (message.type) {
                        Constant.MESSAGE -> {
                            tvSender.text = message.message
                            tvSender.show()
                            cvSender.hide()
                            mapSender.hide()
                        }
                        Constant.IMAGE -> {
                            tvSender.hide()
                            cvSender.show()
                            Glide.with(itemView.context).load(message.message).into(imgSender)
                            mapSender.hide()
                        }
                        Constant.LOCATION -> {
                            tvSender.hide()
                            cvSender.hide()
                            mapSender.show()
                        }
                    }
                } else {
                    when (message.type) {
                        Constant.MESSAGE -> {
                            tvRecipient.text = message.message
                            tvRecipient.show()
                            cvRecipient.hide()
                            mapRecipient.hide()
                        }
                        Constant.IMAGE -> {
                            tvRecipient.hide()
                            cvRecipient.show()
                            Glide.with(itemView.context).load(message.message).into(imgRecipient)
                            mapRecipient.hide()
                        }
                        Constant.LOCATION -> {
                            tvRecipient.hide()
                            cvRecipient.hide()
                            mapRecipient.show()
                        }
                    }
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageAdapter.MessageVH {
        return MessageVH(ItemMessageBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: MessageVH, position: Int) {
        holder.bindData(getItem(position))
    }

}