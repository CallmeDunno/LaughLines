package com.example.laughlines.view.home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.laughlines.databinding.ItemListChatsHomeBinding
import com.example.laughlines.view.home.data.Chat

class ChatAdapter(diffUtil: DiffUtil.ItemCallback<Chat>) :
    ListAdapter<Chat, ChatAdapter.ChatVH>(diffUtil) {

    interface IChatIemClick {
        fun onClickChatItem(chat: Chat)
    }

    private lateinit var iChatIemClick: IChatIemClick

    fun setOnClickChatItem(iChatIemClick: IChatIemClick){
        this.iChatIemClick = iChatIemClick
    }

    inner class ChatVH(itemBinding: ItemListChatsHomeBinding, iChatIemClick: IChatIemClick) :
        RecyclerView.ViewHolder(itemBinding.root) {
        private var _itemBinding: ItemListChatsHomeBinding? = null
        private val itemBinding get() = _itemBinding!!

        init {
            this._itemBinding = itemBinding
        }

        fun bindData(chat: Chat) {
            itemBinding.apply {
                itemView.setOnClickListener {
                    iChatIemClick.onClickChatItem(chat)
                }
                tvMessItemListChats.text = chat.message
                tvNameItemListChats.text = chat.name
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatVH {
        val itemBinding =
            ItemListChatsHomeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ChatVH(itemBinding, iChatIemClick)
    }

    override fun onBindViewHolder(holder: ChatVH, position: Int) {
        getItem(position)?.let {
            holder.bindData(it)
        }
    }

}