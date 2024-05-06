package com.example.laughlines.dialog

import android.content.Context
import com.example.laughlines.R
import com.example.laughlines.base.BaseDialog
import com.example.laughlines.databinding.DialogDeleteFriendBinding

class DeleteFriendDialog(context: Context, private val onClick: () -> Unit) : BaseDialog<DialogDeleteFriendBinding>(context) {
    override val layoutId: Int = R.layout.dialog_delete_friend

    override fun initView() {}

    override fun initAction() {
        binding.apply {
            btnNo.setOnClickListener { this@DeleteFriendDialog.dismiss() }
            btnYes.setOnClickListener {
                onClick.invoke()
                this@DeleteFriendDialog.dismiss()
            }
        }
    }

    override fun onDismissListener() {}
}