package com.example.laughlines.ui.qr_code

import android.app.Dialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context.CLIPBOARD_SERVICE
import android.content.Context.VIBRATOR_SERVICE
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Shader
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Handler
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.Gravity
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import com.budiyev.android.codescanner.CodeScanner
import com.budiyev.android.codescanner.DecodeCallback
import com.example.laughlines.R
import com.example.laughlines.base.BaseFragment
import com.example.laughlines.databinding.FragmentQrCodeBinding
import com.example.laughlines.utils.extensions.hide
import com.example.laughlines.utils.extensions.show
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class QrCodeFragment : BaseFragment<FragmentQrCodeBinding>() {
    override val layoutId: Int = R.layout.fragment_qr_code

    private lateinit var codeScanner: CodeScanner

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
    }

    override fun initAction() {
        super.initAction()
        binding.toolbar.btnBack.setOnClickListener { requireView().findNavController().popBackStack() }
        binding.btnMyQrCode.setOnClickListener { requireView().findNavController().navigate(R.id.action_qrCodeFragment_to_qrCodeGeneratorFragment) }
    }

    override fun onResume() {
        super.onResume()
        codeScanner.startPreview()
    }

    override fun onPause() {
        codeScanner.releaseResources()
        super.onPause()
//        codeScanner.stopPreview()

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
                                          notify(result.text)
                                          showResultDialog(result.text)
                                      }, 500)
            }
        }

        binding.scannerView.setOnClickListener { actionContinuePreview() }
    }

    private fun actionContinuePreview() {
        codeScanner.startPreview()
        binding.scannerView.isMaskVisible = true
    }

    private fun showResultDialog(result: String): Dialog {
        binding.scanning.hide()
        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_qr_result)
        val window = dialog.window!!
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)
        window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val windowAttribute = window.attributes
        windowAttribute.gravity = Gravity.CENTER
        window.attributes = windowAttribute
        val txtResult = dialog.findViewById<TextView>(R.id.txtResult)
        val btnCopy = dialog.findViewById<ImageView>(R.id.btnCopy)
        val btnClose = dialog.findViewById<ImageView>(R.id.btnClose)
        btnClose.setOnClickListener { view1: View? ->
            dialog.dismiss()
            actionContinuePreview()
        }
        txtResult.text = result
        btnCopy.setOnClickListener { view1: View? ->
            val clipboard = requireActivity().getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("label", result)
            if (clipboard != null) {
                clipboard.setPrimaryClip(clip)
//                btnCopy.setImageResource(R.drawable.ic_check)
                btnCopy.imageTintList = ColorStateList.valueOf(Color.parseColor("#1CCF7A"))
                btnCopy.isClickable = false
            }
//            notify(getString(R.string.copy_successful))
        }
        dialog.setCancelable(false)

        dialog.setOnDismissListener {
            binding.scanning.show()
            onResume()
        }

        dialog.show()
        return dialog
    }

}