package com.example.laughlines.ui.qr_code

import android.graphics.Color
import android.util.Log
import androidmads.library.qrgenearator.QRGContents
import androidmads.library.qrgenearator.QRGEncoder
import androidx.navigation.findNavController
import com.example.laughlines.R
import com.example.laughlines.base.BaseFragment
import com.example.laughlines.databinding.FragmentQrCodeGeneratorBinding
import com.google.zxing.WriterException
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class QrCodeGeneratorFragment : BaseFragment<FragmentQrCodeGeneratorBinding>() {
    override val layoutId: Int = R.layout.fragment_qr_code_generator

    override fun initView() {
        super.initView()
        val qrgEncoder = QRGEncoder("Dung dep zai vl", null, QRGContents.Type.TEXT, 300)
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
        super.initAction()
        binding.toolbar.btnBack.setOnClickListener { requireView().findNavController().popBackStack() }
    }

}