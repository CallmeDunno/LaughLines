package com.example.laughlines.ui.qr_code

import android.app.Activity.RESULT_OK
import android.content.Context.VIBRATOR_SERVICE
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.LinearGradient
import android.graphics.Shader
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import com.budiyev.android.codescanner.CodeScanner
import com.budiyev.android.codescanner.DecodeCallback
import com.bumptech.glide.Glide
import com.example.laughlines.R
import com.example.laughlines.base.BaseFragment
import com.example.laughlines.databinding.BottomSheetResultQrBinding
import com.example.laughlines.databinding.FragmentQrCodeBinding
import com.example.laughlines.dialog.LoadingDialog
import com.example.laughlines.utils.Constant
import com.example.laughlines.utils.SharedPreferencesManager
import com.example.laughlines.utils.StorageUtils
import com.example.laughlines.utils.UiState
import com.example.laughlines.utils.extensions.hide
import com.example.laughlines.utils.extensions.show
import com.example.laughlines.viewmodel.QrCodeViewModel
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.zxing.BinaryBitmap
import com.google.zxing.MultiFormatReader
import com.google.zxing.NotFoundException
import com.google.zxing.RGBLuminanceSource
import com.google.zxing.common.HybridBinarizer
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class QrCodeFragment : BaseFragment<FragmentQrCodeBinding>() {
    override val layoutId: Int = R.layout.fragment_qr_code

    private lateinit var codeScanner: CodeScanner
    private lateinit var loadingDialog: LoadingDialog
    private val GALLERY_REQUEST_CODE = 10

    private val viewModel by viewModels<QrCodeViewModel>()

    @Inject
    lateinit var sharedPref: SharedPreferencesManager

    override fun initView() {
        super.initView()

        binding.toolbar.apply {
            tvToolbar.text = getString(R.string.qr_code_scanner)
            btnSettings.hide()
        }
        binding.apply {
            btnGallery.paint.shader = LinearGradient(0f, 0f, 0f, btnGallery.lineHeight.toFloat(), intArrayOf(ContextCompat.getColor(requireContext(), R.color.jungle_green), ContextCompat.getColor(requireContext(), R.color.cyan_cornflower_blue)), null, Shader.TileMode.CLAMP)
            btnMyQrCode.paint.shader = LinearGradient(0f, 0f, 0f, btnMyQrCode.lineHeight.toFloat(), intArrayOf(ContextCompat.getColor(requireContext(), R.color.jungle_green), ContextCompat.getColor(requireContext(), R.color.cyan_cornflower_blue)), null, Shader.TileMode.CLAMP)
        }

        initScanner()
        loadingDialog = LoadingDialog(requireContext())
    }

    override fun initAction() {
        super.initAction()
        binding.toolbar.btnBack.setOnClickListener { requireView().findNavController().popBackStack() }
        binding.btnMyQrCode.setOnClickListener { requireView().findNavController().navigate(R.id.action_qrCodeFragment_to_qrCodeGeneratorFragment) }
        binding.btnGallery.setOnClickListener {
            val i = Intent()
            i.type = "image/*"
            i.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(Intent.createChooser(i, "Select Picture"), GALLERY_REQUEST_CODE)
        }
    }

    override fun onObserve() {
        super.onObserve()
        viewModel.requestLiveData.observe(viewLifecycleOwner) {
            when (it) {
                is UiState.Loading -> {
                    loadingDialog.show()
                }
                is UiState.Failure -> {
                    loadingDialog.dismiss()
                    notify(getString(R.string.qr_error))
                    Log.e("Dunno", it.message.toString())
                }
                is UiState.Success -> {
                    loadingDialog.dismiss()
                    if (it.data) {
                        notify(getString(R.string.request_successful))
                    } else {
                        notify(getString(R.string.the_request_has_been_sent))
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        codeScanner.startPreview()
    }

    override fun onPause() {
        codeScanner.releaseResources()
        super.onPause()
        codeScanner.stopPreview()

    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && data != null && requestCode == GALLERY_REQUEST_CODE) {
            Log.e("Dunno", "Uri: ${data.data.toString()}")
            StorageUtils.uriToBitmap(requireContext(), Uri.parse(data.data.toString()))?.let { bitmap ->
                decodeQRCode(bitmap)?.let { id ->
                    binding.scannerView.visibility = View.INVISIBLE
                    showResultBottomSheet(id)
                }
            }
        }
    }

    private fun initScanner() {
        codeScanner = CodeScanner(requireActivity(), binding.scannerView)
        Handler().postDelayed({ binding.scannerView.visibility = View.VISIBLE }, 100)
        codeScanner.decodeCallback = DecodeCallback { result ->
            requireActivity().runOnUiThread {
                codeScanner.stopPreview()
                Handler().postDelayed({
                                          val vibrator = requireContext().getSystemService(VIBRATOR_SERVICE) as Vibrator
                                          if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                              vibrator.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE))
                                          } else {
                                              vibrator.vibrate(500)
                                          }
                                          binding.scannerView.isMaskVisible = false
                                          showResultBottomSheet(result.text)
                                      }, 500)
            }
        }

        binding.scannerView.setOnClickListener { actionContinuePreview() }
    }

    private fun actionContinuePreview() {
        codeScanner.startPreview()
        binding.scannerView.isMaskVisible = true
    }

    private fun showResultBottomSheet(id: String) {
        binding.scanning.hide()
        val bottomSheetBinding = BottomSheetResultQrBinding.inflate(layoutInflater)
        val bottomSheet = BottomSheetDialog(requireContext())
        bottomSheet.setContentView(bottomSheetBinding.root)

        viewModel.getInformation(sharedPref.getString(Constant.Key.ID.name)!!, id).observe(viewLifecycleOwner) {
            when (it) {
                is UiState.Loading -> {
                    bottomSheetBinding.apply {
                        vLoading.show()
                        btnAddFriend.isEnabled = false
                        btnAddFriend.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.davy_grey))
                    }
                }
                is UiState.Failure -> {
                    Log.e("Dunno", "Error: ${it.message}")
                    notify(getString(R.string.qr_error))
                }
                is UiState.Success -> {
                    bottomSheetBinding.apply {
                        vLoading.hide()
                        btnAddFriend.isEnabled = true
                        btnAddFriend.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.jungle_green))

                        val qrResult = it.data
                        if (qrResult.avatar == null || qrResult.avatar == "null") {
                            Glide.with(requireView()).load(R.drawable.ic_person_green_24).into(imgAvatar)
                        } else {
                            Glide.with(requireView()).load(qrResult.avatar).into(imgAvatar)
                        }
                        tvName.text = qrResult.name
                        tvEmail.text = qrResult.email
                        tvFriend.text = "${qrResult.sumFriend} friends"
                    }
                }
            }
        }

        bottomSheetBinding.btnAddFriend.setOnClickListener {
            viewModel.requestFriend(sharedPref.getString(Constant.Key.ID.name)!!, id)
        }

        bottomSheet.setOnDismissListener {
            actionContinuePreview()
            binding.scanning.show()
            binding.scannerView.show()
            onResume()
        }
        bottomSheet.show()
    }

    private fun decodeQRCode(bitmap: Bitmap): String? {
        return try {
            val intArray = IntArray(bitmap.width * bitmap.height)
            bitmap.getPixels(intArray, 0, bitmap.width, 0, 0, bitmap.width, bitmap.height)
            val source = RGBLuminanceSource(bitmap.width, bitmap.height, intArray)
            val binaryBitmap = BinaryBitmap(HybridBinarizer(source))
            val reader = MultiFormatReader()
            val result = reader.decode(binaryBitmap)
            result.text
        } catch (e: NotFoundException) {
            Log.e("QRDecode", "Không tìm thấy mã QR trong hình ảnh", e)
            null
        }
    }
}