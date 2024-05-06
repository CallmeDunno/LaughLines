package com.example.laughlines.dialog

import android.content.Context
import android.graphics.Color
import android.util.Log
import androidmads.library.qrgenearator.QRGContents
import androidmads.library.qrgenearator.QRGEncoder
import com.example.laughlines.R
import com.example.laughlines.base.BaseDialog
import com.example.laughlines.databinding.DialogQrCodeBinding
import com.google.zxing.WriterException

class QrCodeDialog(private val context: Context, private val name: String, private val id: String) : BaseDialog<DialogQrCodeBinding>(context) {
    override val layoutId: Int = R.layout.dialog_qr_code

    override fun initView() {
        binding.tvName.text = name
        val qrgEncoder = QRGEncoder(id, null, QRGContents.Type.TEXT, 300)
        qrgEncoder.colorBlack = Color.WHITE
        qrgEncoder.colorWhite = Color.BLACK
        try {
            val bitmap = qrgEncoder.bitmap
            binding.imgQr.setImageBitmap(bitmap)
        } catch (e: WriterException) {
            Log.v("Dunno", e.toString())
        }
    }

    override fun initAction() {
        binding.btnClose.setOnClickListener { this.dismiss() }
    }

    override fun onDismissListener() {}

}