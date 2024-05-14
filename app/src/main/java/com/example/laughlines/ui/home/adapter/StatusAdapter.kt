package com.example.laughlines.ui.home.adapter

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.AsyncDifferConfig
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.example.laughlines.R
import com.example.laughlines.databinding.ItemStatusBinding
import com.example.laughlines.model.Account

class StatusAdapter(private val onClick: (Account) -> Unit) : ListAdapter<Account, StatusAdapter.StatusVH>(AsyncDifferConfig.Builder(object : DiffUtil.ItemCallback<Account>() {
    override fun areItemsTheSame(oldItem: Account, newItem: Account): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Account, newItem: Account): Boolean {
        return oldItem == newItem
    }
}).build()) {

    inner class StatusVH(private val binding: ItemStatusBinding) : ViewHolder(binding.root) {

        fun bindData(account: Account) {
            itemView.setOnClickListener { onClick.invoke(account) }
            binding.apply {
                if (account.avatar.isEmpty()) {
                    Glide.with(itemView.context).load(R.drawable.logo_chat_app).into(imgAvatar)
                    imgAvatar.imageTintList = ColorStateList.valueOf(ContextCompat.getColor(itemView.context, R.color.white))
                } else {
                    Glide.with(itemView.context).load(account.avatar).into(imgAvatar)
                }
                tvName.text = account.name
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StatusVH {
        return StatusVH(ItemStatusBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: StatusVH, position: Int) {
        holder.bindData(getItem(position))
    }

}