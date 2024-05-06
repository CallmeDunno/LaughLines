package com.example.laughlines.dialog

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.example.laughlines.R
import com.example.laughlines.base.BaseDialog
import com.example.laughlines.databinding.DialogStatusBinding
import com.example.laughlines.model.Account
import com.example.laughlines.utils.extensions.show

class StatusDialog(private val context: Context, private val account: Account, private val owner: Boolean = true, private val onDoneClick: (String) -> Unit) : BaseDialog<DialogStatusBinding>(context) {
    override val layoutId: Int = R.layout.dialog_status

    override fun initView() {
        binding.apply {
            if (!owner) {
                tvLength.visibility = View.INVISIBLE
                btnDone.visibility = View.INVISIBLE
                edtStatus.isEnabled = false
            }
            if (account.avatar == "null" || account.avatar == "" || account.avatar == null) {
                Glide.with(context).load(ContextCompat.getDrawable(context, R.drawable.logo_chat_app)).into(imgAvatar)
            } else {
                Glide.with(context).load(account.avatar).into(imgAvatar)
            }
            edtStatus.setText(account.status)
            if (account.status!!.isNotEmpty()) {
                btnDone.show()
            } else {
                btnDone.visibility = View.INVISIBLE
            }
        }
    }

    override fun initAction() {
        binding.apply {
            btnClose.setOnClickListener { this@StatusDialog.dismiss() }
            btnDone.setOnClickListener {
                val strStatus = edtStatus.text.toString().trim()
                onDoneClick.invoke(strStatus)
            }
            edtStatus.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    val textLength = p0.toString().length
                    tvLength.text = "${textLength}/50"
                    if (textLength > 0) {
                        btnDone.show()
                    } else {
                        btnDone.visibility = View.INVISIBLE
                    }
                }

                override fun afterTextChanged(p0: Editable?) {
                    val textLength = p0.toString().length
                    if (textLength > 0) {
                        btnDone.show()
                    } else {
                        btnDone.visibility = View.INVISIBLE
                    }
                }
            })
        }
    }

    override fun onDismissListener() {}

}