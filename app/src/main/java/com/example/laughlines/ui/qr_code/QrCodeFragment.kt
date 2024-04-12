package com.example.laughlines.ui.qr_code

import android.content.Context.VIBRATOR_SERVICE
import android.content.res.ColorStateList
import android.graphics.LinearGradient
import android.graphics.Shader
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
import com.example.laughlines.utils.UiState
import com.example.laughlines.utils.extensions.hide
import com.example.laughlines.utils.extensions.show
import com.example.laughlines.viewmodel.QrCodeViewModel
import com.google.android.material.bottomsheet.BottomSheetDialog
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class QrCodeFragment : BaseFragment<FragmentQrCodeBinding>() {
    override val layoutId: Int = R.layout.fragment_qr_code

    private lateinit var codeScanner: CodeScanner
    private lateinit var loadingDialog: LoadingDialog

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
    }

    override fun onObserve() {
        super.onObserve()
        viewModel.requestLiveData.observe(viewLifecycleOwner) {
            when(it) {
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
            onResume()
        }
        bottomSheet.show()
    }

}