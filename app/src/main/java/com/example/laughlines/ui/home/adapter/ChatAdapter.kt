package com.example.laughlines.ui.home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.AsyncDifferConfig
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.example.laughlines.R
import com.example.laughlines.databinding.ItemChatBinding
import com.example.laughlines.model.Contact

class ChatAdapter(private val onClick: (Contact) -> Unit) : ListAdapter<Contact, ChatAdapter.ChatVH>(AsyncDifferConfig.Builder(object : DiffUtil.ItemCallback<Contact>() {
    override fun areItemsTheSame(oldItem: Contact, newItem: Contact): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Contact, newItem: Contact): Boolean {
        return oldItem == newItem
    }
}).build()) {

    inner class ChatVH(private val binding: ItemChatBinding) : ViewHolder(binding.root) {

        fun bindData(contact: Contact) {
            binding.apply {
                if (contact.account.avatar == "" || contact.account.avatar == "null") {
                    Glide.with(itemView.context).load(ContextCompat.getDrawable(itemView.context, R.drawable.logo_chat_app)).into(imgAvatar)
                } else {
                    Glide.with(itemView.context).load(contact.account.avatar).into(imgAvatar)
                }
                tvName.text = contact.account.name
            }
            itemView.setOnClickListener { onClick.invoke(contact) }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatAdapter.ChatVH {
        return ChatVH(ItemChatBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ChatVH, position: Int) {
        holder.bindData(getItem(position))
    }

}