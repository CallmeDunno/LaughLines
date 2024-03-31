package com.example.laughlines.ui.qr_code

import android.os.Handler
import android.view.View
import com.budiyev.android.codescanner.CodeScanner
import com.budiyev.android.codescanner.DecodeCallback
import com.example.laughlines.R
import com.example.laughlines.base.BaseFragment
import com.example.laughlines.databinding.FragmentQrCodeScannerBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class QrCodeScannerFragment : BaseFragment<FragmentQrCodeScannerBinding>() {
    override val layoutId: Int = R.layout.fragment_qr_code_scanner

    private lateinit var codeScanner: CodeScanner

    override fun initView() {
        super.initView()
        initScanner()
    }

    override fun initAction() {
        super.initAction()

    }

    override fun onResume() {
        super.onResume()
        codeScanner.startPreview()
    }

    override fun onPause() {
        super.onPause()
        codeScanner.stopPreview()
        codeScanner.releaseResources()
    }

    private fun initScanner() {
        codeScanner = CodeScanner(requireContext(), binding.scannerView)
        Handler().postDelayed({ binding.scannerView.visibility = View.VISIBLE }, 100)
        codeScanner.decodeCallback = DecodeCallback { result ->
//            runOnUiThread {
//                codeScanner.stopPreview()
//                Handler().postDelayed({
//                                          val v = getSystemService(VIBRATOR_SERVICE) as Vibrator
//                                          if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                                              v.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE))
//                                          } else {
//                                              // deprecated in API 26
//                                              v.vibrate(500)
//                                          }
//                                          binding.scannerView.isMaskVisible = false
//                                          dialog = showResultDialog(result.text)
//                                      }, 500)
//            }
        }

        binding.scannerView.setOnClickListener { actionContinuePreview() }
    }

    private fun actionContinuePreview() {
        codeScanner.startPreview()
        binding.scannerView.isMaskVisible = true
    }
}