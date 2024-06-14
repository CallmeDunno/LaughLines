package com.example.laughlines.ui.request

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncDifferConfig
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.example.laughlines.R
import com.example.laughlines.databinding.ItemRequestBinding
import com.example.laughlines.model.RequestModel2

class RequestAdapter(private val onClickItem: (RequestModel2) -> Unit, private val onClickAccept: (RequestModel2) -> Unit, private val onClickDelete: (String) -> Unit) : ListAdapter<RequestModel2, RequestAdapter.RequestVH>(AsyncDifferConfig.Builder(object : DiffUtil.ItemCallback<RequestModel2>() {
    override fun areItemsTheSame(oldItem: RequestModel2, newItem: RequestModel2): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: RequestModel2, newItem: RequestModel2): Boolean {
        return oldItem == newItem
    }
}).build()) {

    inner class RequestVH(private val binding: ItemRequestBinding) : ViewHolder(binding.root) {

        fun bindData(requestModel2: RequestModel2) {
            itemView.setOnClickListener { onClickItem.invoke(requestModel2) }
            binding.apply {
                btnAccept.setOnClickListener { onClickAccept.invoke(requestModel2) }
                btnDelete.setOnClickListener { onClickDelete.invoke(requestModel2.id) }
                Log.e("Dunno", requestModel2.avatar ?: "")
                if (requestModel2.avatar == null || requestModel2.avatar == "null" || requestModel2.avatar == "") {
                    Glide.with(itemView.context).load(R.drawable.logo_chat_app).into(img)
                } else {
                    Glide.with(itemView.context).load(requestModel2.avatar).into(img)
                }
                tvName.text = requestModel2.name
                tvTimeRequest.text = "Time request: ${requestModel2.timeRequest}"
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RequestVH {
        return RequestVH(ItemRequestBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: RequestVH, position: Int) {
        holder.bindData(getItem(position))
    }

}