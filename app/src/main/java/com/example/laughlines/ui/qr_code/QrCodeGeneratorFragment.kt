package com.example.laughlines.ui.qr_code

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.util.Log
import androidmads.library.qrgenearator.QRGContents
import androidmads.library.qrgenearator.QRGEncoder
import androidx.core.graphics.drawable.toBitmap
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import com.example.laughlines.R
import com.example.laughlines.base.BaseFragment
import com.example.laughlines.databinding.FragmentQrCodeGeneratorBinding
import com.example.laughlines.dialog.LoadingDialog
import com.example.laughlines.utils.Constant
import com.example.laughlines.utils.SharedPreferencesManager
import com.example.laughlines.utils.StorageUtils
import com.example.laughlines.utils.UiState
import com.example.laughlines.utils.extensions.formatDateTime
import com.example.laughlines.utils.extensions.hide
import com.example.laughlines.viewmodel.QrCodeViewModel
import com.google.zxing.WriterException
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class QrCodeGeneratorFragment : BaseFragment<FragmentQrCodeGeneratorBinding>() {
    override val layoutId: Int = R.layout.fragment_qr_code_generator

    @Inject
    lateinit var sharedPref: SharedPreferencesManager

    private val viewModel by viewModels<QrCodeViewModel>()

    override fun initView() {
        super.initView()

        binding.toolbar.btnSettings.hide()

        val loadingDialog = LoadingDialog(requireContext())
        sharedPref.getString(Constant.Key.ID.name)?.let { id ->
            viewModel.getMyName(id).observe(viewLifecycleOwner) {
                when (it) {
                    is UiState.Loading -> {
                        loadingDialog.show()
                    }
                    is UiState.Success -> {
                        loadingDialog.dismiss()

                        binding.tvName.text = it.data

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
                    is UiState.Failure -> {
                        loadingDialog.dismiss()
                        Log.e("Dunno", it.message.toString())
                        notify(getString(R.string.error))
                    }
                }
            }
        }
    }

    override fun initAction() {
        super.initAction()
        binding.toolbar.btnBack.setOnClickListener { requireView().findNavController().popBackStack() }
        binding.btnSave.setOnClickListener { handleSave() }
        binding.btnShare.setOnClickListener { handleShare() }
    }

    private fun handleShare() {
        StorageUtils.shareImage(requireContext(), formatDateTime(Constant.DateTimeFormat2), createBitmap())
    }

    private fun handleSave() {
        StorageUtils.saveImage(requireContext(), formatDateTime(Constant.DateTimeFormat2), createBitmap())
    }

    private fun createBitmap() : Bitmap {
        val qrBitmap = binding.imgQr.drawable.toBitmap()
        val finalBitmap = Bitmap.createBitmap(qrBitmap.width * 3, qrBitmap.height * 3, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(finalBitmap)
        binding.layoutQrCode.draw(canvas)
        return finalBitmap
    }

}