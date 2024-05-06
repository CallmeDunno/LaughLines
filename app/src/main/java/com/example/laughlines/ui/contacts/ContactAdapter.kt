package com.example.laughlines.ui.contacts

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.AsyncDifferConfig
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.example.laughlines.R
import com.example.laughlines.databinding.ItemContactBinding
import com.example.laughlines.model.Contact

class ContactAdapter(private val onItemClick: (Contact) -> Unit, private val onMoreClick: (Contact, View) -> Unit) : ListAdapter<Contact, ContactAdapter.ContactVH>(AsyncDifferConfig.Builder(object : DiffUtil.ItemCallback<Contact>() {
    override fun areItemsTheSame(oldItem: Contact, newItem: Contact): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Contact, newItem: Contact): Boolean {
        return oldItem == newItem
    }
}).build()) {

    inner class ContactVH(private val binding: ItemContactBinding) : ViewHolder(binding.root) {

        fun bindData(contact: Contact) {
            binding.apply {
                tvName.isSelected = true
                item = contact
                if (contact.account.avatar == null || contact.account.avatar == "null" || contact.account.avatar == "") {
                    Glide.with(itemView.context).load(ContextCompat.getDrawable(itemView.context, R.drawable.logo_chat_app)).into(imgAvatar)
                } else {
                    Glide.with(itemView.context).load(contact.account.avatar).into(imgAvatar)
                }
            }
            itemView.setOnClickListener { onItemClick.invoke(contact) }
            binding.btnMore.setOnClickListener { onMoreClick.invoke(contact, binding.btnMore) }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactVH {
        return ContactVH(ItemContactBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ContactVH, position: Int) {
        holder.bindData(getItem(position))
    }

    fun setFilterList(l: List<Contact>) {
        submitList(l)
        notifyDataSetChanged()
    }

}