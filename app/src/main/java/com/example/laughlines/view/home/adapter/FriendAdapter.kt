package com.example.laughlines.view.home.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.laughlines.databinding.ItemUserBinding
import com.example.laughlines.listener.IClickItem
import com.example.laughlines.model.Friend


class FriendAdapter(diffUtil: DiffUtil.ItemCallback<Friend>) :
    ListAdapter<Friend, FriendAdapter.FriendVH>(diffUtil) {

    private lateinit var iClickItem: IClickItem

    fun setOnClickUserItem(iClickItem: IClickItem) {
        this.iClickItem = iClickItem
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FriendVH {
        val itemBinding =
            ItemUserBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FriendVH(itemBinding, iClickItem)
    }

    override fun onBindViewHolder(holder: FriendVH, position: Int) {
        getItem(position)?.let {
            holder.bindData(it)
        }
    }

    inner class FriendVH(itemBinding: ItemUserBinding, private val iClickItem: IClickItem) :
        RecyclerView.ViewHolder(itemBinding.root) {

        private var _itemBinding: ItemUserBinding? = null
        private val itemBinding get() = _itemBinding!!

        init {
            this._itemBinding = itemBinding
        }

        @SuppressLint("SetTextI18n")
        fun bindData(friend: Friend) {
            itemBinding.apply {
                itemView.setOnClickListener {
                    iClickItem.setOnClickItemChat(friend)
                }
                friend.avatarUrl?.let {
                    Glide.with(itemView).load(it).into(imgAvtItemListChats)
                }
                tvNameItemListChats.text = friend.name
                tvMessItemListChats.text = "Tap to chat"
            }
        }
    }

}