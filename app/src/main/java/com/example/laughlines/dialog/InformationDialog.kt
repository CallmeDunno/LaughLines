package com.example.laughlines.dialog

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.example.laughlines.R
import com.example.laughlines.base.BaseDialog
import com.example.laughlines.databinding.DialogInformationBinding
import com.example.laughlines.model.Account


class InformationDialog(private val context: Context, private val account: Account, private val onMessageClick: () -> Unit) : BaseDialog<DialogInformationBinding>(context) {
    override val layoutId: Int = R.layout.dialog_information

    override fun initView() {
        binding.apply {
            if (account.avatar == "null" || account.avatar == null || account.avatar == "") {
                Glide.with(context).load(ContextCompat.getDrawable(context, R.drawable.logo_chat_app)).into(binding.imgAvatar)
            } else {
                Glide.with(context).load(account.avatar).into(imgAvatar)
            }
            tvName.text = account.name
            tvEmail.text = account.email
            tvPhoneNumber.text = account.numberPhone
        }
    }

    override fun initAction() {
        binding.apply {
            btnClose.setOnClickListener { this@InformationDialog.dismiss() }
            btnEmail.setOnClickListener { handleSendEmail() }
            btnPhoneCall.setOnClickListener { handleCall() }
            btnSms.setOnClickListener { handleSendSms() }
            btnMessage.setOnClickListener { handleMessage() }
            btnQrCode.setOnClickListener { handleQrCode() }
        }
    }

    private fun handleMessage() {
        this.dismiss()
        onMessageClick.invoke()
    }

    private fun handleQrCode() {
        val qrCodeDialog = QrCodeDialog(context, account.name, account.id)
        qrCodeDialog.show()
    }

    private fun handleSendSms() {
        val uri = Uri.parse("smsto:${account.numberPhone}")
        val intent = Intent(Intent.ACTION_SENDTO, uri)
        intent.putExtra("sms_body", "From LaughLines: ")
        context.startActivity(intent)
    }

    private fun handleCall() {
        val intent = Intent(Intent.ACTION_DIAL)
        intent.data = Uri.parse("tel:${account.numberPhone}")
        context.startActivity(intent)
    }

    private fun handleSendEmail() {
        val intent = Intent(Intent.ACTION_SENDTO)
        intent.data = Uri.parse("mailto:${account.email}")
        context.startActivity(intent)
    }

    override fun onDismissListener() {}
}