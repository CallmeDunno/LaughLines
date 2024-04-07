package com.example.laughlines.base

import android.content.Context
import android.util.Log
import com.example.laughlines.R
import com.example.laughlines.databinding.DialogQrResultBinding

class TestDialog(context: Context, private val onClick: (String) -> Unit, private val onDismiss: (String) -> Unit) : BaseDialog<DialogQrResultBinding>(context) {
    override val layoutId: Int = R.layout.dialog_qr_result

    var x = ""

    override fun initView() {
        binding.txtResult.text = x
    }

    override fun initAction() {
        binding.btnClose.setOnClickListener { this.dismiss() }
        binding.btnCopy.setOnClickListener {
            onClick.invoke("aaaaaaaaaaaaaaaaa")
        }
    }

    override fun onDismissListener() {
        Log.w("Dunno", "acsfds")
        onDismiss.invoke("bbbbbbbbbbbbbbbbb")
    }

}