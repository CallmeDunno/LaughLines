package com.example.laughlines.ui.camera

import android.app.AlertDialog
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Handler
import android.provider.MediaStore
import android.provider.Settings
import android.text.Html
import android.util.Log
import android.view.View
import android.view.WindowManager
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.example.laughlines.R
import com.example.laughlines.base.BaseFragment
import com.example.laughlines.databinding.FragmentCameraBinding
import com.example.laughlines.dialog.LoadingDialog
import com.example.laughlines.model.Messages
import com.example.laughlines.utils.Constant
import com.example.laughlines.utils.SharedPreferencesManager
import com.example.laughlines.utils.UiState
import com.example.laughlines.utils.extensions.hide
import com.example.laughlines.utils.extensions.show
import com.example.laughlines.viewmodel.ImageViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import javax.inject.Inject

@AndroidEntryPoint
class CameraFragment : BaseFragment<FragmentCameraBinding>() {
    override val layoutId: Int = R.layout.fragment_camera

    @Inject
    lateinit var sharedPref: SharedPreferencesManager

    private val viewModel by viewModels<ImageViewModel>()

    private lateinit var chatId: String
    private lateinit var myId: String
    private val arg: CameraFragmentArgs by navArgs()

    private var imageCapture: ImageCapture? = null
    private lateinit var cameraExecutor: ExecutorService
    private lateinit var cameraProvider: ProcessCameraProvider
    private var orientationCamera = Constant.CameraStateEnum.CAMERA_BACK.name
    private lateinit var camera: Camera
    private var isFlashing = false
    private lateinit var uri: Uri

    override fun initData() {
        super.initData()
        chatId = arg.cid
        myId = sharedPref.getString(Constant.Key.ID.name) ?: Constant.ID_DEFAULT
    }

    override fun initView() {
        super.initView()
        requestCamera()
        cameraExecutor = Executors.newSingleThreadExecutor()
    }

    override fun initAction() {
        super.initAction()
        binding.apply {
            btnBack.setOnClickListener { handleBack() }
            btnFlash.setOnClickListener { handleFlash() }
            btnSwapCamera.setOnClickListener { handleSwapCamera() }
            btnShutter.setOnClickListener { handleShutter() }
            btnClose.setOnClickListener { handleClosePreview() }
            btnDone.setOnClickListener { handleSendImage() }
        }
    }

    private fun handleSendImage() {
        binding.btnClose.isEnabled = false
        binding.btnDone.isEnabled = false
        val loadingDialog = LoadingDialog(requireContext())
        loadingDialog.show()
        viewModel.pushMessage(chatId, Messages(uri.toString(), myId, System.currentTimeMillis(), Constant.IMAGE)).observe(viewLifecycleOwner) {
            when (it) {
                is UiState.Loading -> {}
                is UiState.Failure -> { Log.e("Dunno", it.message.toString()) }
                is UiState.Success -> {
                    loadingDialog.dismiss()
                    requireView().findNavController().popBackStack() }
            }
        }
    }

    private fun handleClosePreview() {
        binding.layoutPreview.visibility = View.GONE
    }

    @Deprecated("Deprecated in Java")
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            Constant.PER_CAMERA_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startCamera()
                } else {
                    showGoToSettingDialog()
                }
            }
            Constant.PER_STORAGE_CODE -> {
                if (grantResults.isNotEmpty()) {
                    if (grantResults.size == 2) {
                        if ((grantResults[0] == PackageManager.PERMISSION_GRANTED) && (grantResults[1] == PackageManager.PERMISSION_GRANTED)) {
//                            handleSave()
                        } else {
                            showGoToSettingDialog()
                        }
                    }
                    if (grantResults.size == 3) {
                        if ((grantResults[0] == PackageManager.PERMISSION_GRANTED) && (grantResults[1] == PackageManager.PERMISSION_GRANTED) && (grantResults[2] == PackageManager.PERMISSION_GRANTED)) {
//                            handleSave()
                        } else {
                            showGoToSettingDialog()
                        }
                    }
                } else {
                    showGoToSettingDialog()
                }
            }
        }
    }

    private fun showGoToSettingDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setMessage(getString(R.string.requested_permission_message)).setTitle(getString(R.string.requested_permission_title)).setCancelable(false).setPositiveButton(Html.fromHtml("<font color='#21A884'>${getString(R.string.permission_setting)}</font>")) { p0, _ ->
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            val uri = Uri.fromParts("package", requireActivity().packageName, null)
            intent.data = uri
            startActivity(intent)
            p0.dismiss()
        }
        builder.show()
    }

    private fun requestCamera() {
        if (checkSelfPermission(requireContext(), Constant.PERMISSION_CAMERA) == PackageManager.PERMISSION_GRANTED) {
            startCamera(orientationCamera)
        } else {
            requestPermissions(arrayOf(Constant.PERMISSION_CAMERA), Constant.PER_CAMERA_CODE)
        }
    }

    private fun startCamera(orientation: String = Constant.CameraStateEnum.CAMERA_BACK.name) {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())
        cameraProvider = cameraProviderFuture.get()
        cameraProviderFuture.addListener({
                                             val preview = Preview.Builder().build().also {
                                                 it.setSurfaceProvider(binding.viewFinder.surfaceProvider)
                                             }
                                             imageCapture = ImageCapture.Builder().build()
                                             val cameraSelector: CameraSelector
                                             if (orientation == Constant.CameraStateEnum.CAMERA_BACK.name) {
                                                 cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
                                             } else {
                                                 cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA
                                             }
                                             binding.viewFinder.hide()
                                             Handler().postDelayed({
                                                                       try {
                                                                           cameraProvider.unbindAll()
                                                                           camera = cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture)
                                                                           camera.cameraControl.enableTorch(isFlashing)
                                                                           binding.viewFinder.show()
                                                                       } catch (exc: Exception) {
                                                                           Log.e("Dunno", "Use case binding failed", exc)
                                                                       }
                                                                   }, 500)

                                         }, ContextCompat.getMainExecutor(requireContext()))
    }

    private fun stopCamera() {
        try {
            imageCapture = null
            cameraProvider.unbindAll()
            cameraExecutor.shutdownNow()
        } catch (e: Exception) {
            Log.d("Dunno", e.message.toString())
        }
    }

    private fun switchFlash(state: Boolean) {
        camera.cameraControl.enableTorch(state)
        if (state) {
            binding.btnFlash.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_flash_on))
        } else {
            binding.btnFlash.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_flash_off))
        }
    }

    private fun takePhoto() {
        val imageCapture = imageCapture ?: return

        val contentValues = ContentValues()

        val outputOptions = ImageCapture.OutputFileOptions.Builder(requireActivity().contentResolver, MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues).build()

        imageCapture.takePicture(outputOptions, ContextCompat.getMainExecutor(requireContext()), object : ImageCapture.OnImageSavedCallback {
            override fun onError(exc: ImageCaptureException) {
                Log.e("Dunno", "Photo capture failed: ${exc.message}", exc)
            }

            override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                uri = output.savedUri!!
                Log.d("Dunno", Uri.parse(uri.toString()).toString())
                notify(getString(R.string.take_photos_successfully))
                binding.layoutPreview.show()
                Glide.with(requireView()).load(Uri.parse(uri.toString())).into(binding.imgPreview)
            }
        })
    }

    private fun handleShutter() {
        if (isFlashing) switchFlash(false)
        takePhoto()
    }

    private fun handleSwapCamera() {
        if (orientationCamera == Constant.CameraStateEnum.CAMERA_BACK.name) {
            orientationCamera = Constant.CameraStateEnum.CAMERA_FRONT.name
            if (isFlashing) {
                isFlashing = !isFlashing
                switchFlash(isFlashing)
            }
        } else {
            orientationCamera = Constant.CameraStateEnum.CAMERA_BACK.name
        }
        Constant.ORIENTATION_CAMERA = orientationCamera
        startCamera(orientationCamera)
    }

    private fun handleFlash() {
        if (orientationCamera == Constant.CameraStateEnum.CAMERA_FRONT.name) {
//            notify(getString(R.string.text_flash_camera))
        } else {
            isFlashing = !isFlashing
            switchFlash(isFlashing)
        }
    }

    private fun handleBack() {
        stopCamera()
        requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
        requireView().findNavController().popBackStack()
    }

}