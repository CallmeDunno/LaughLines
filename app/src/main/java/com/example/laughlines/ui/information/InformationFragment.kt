package com.example.laughlines.ui.information

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.text.TextUtils
import android.util.Log
import android.view.MotionEvent
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import com.bumptech.glide.Glide
import com.example.laughlines.R
import com.example.laughlines.base.BaseFragment
import com.example.laughlines.databinding.FragmentInformationBinding
import com.example.laughlines.dialog.LoadingDialog
import com.example.laughlines.model.Account
import com.example.laughlines.utils.Constant
import com.example.laughlines.utils.UiState
import com.example.laughlines.utils.extensions.hide
import com.example.laughlines.utils.extensions.hideKeyboard
import com.example.laughlines.viewmodel.InformationViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class InformationFragment : BaseFragment<FragmentInformationBinding>() {

    override val layoutId: Int = R.layout.fragment_information

    private val viewModel by viewModels<InformationViewModel>()
    private lateinit var loadingDialog: LoadingDialog
    private var account: Account? = null
    private var uri: Uri? = null

    override fun initView() {
        super.initView()
        loadingDialog = LoadingDialog(requireContext())
        binding.toolbar.apply {
            btnSettings.hide()
            tvToolbar.text = getString(R.string.change_information)
        }
    }

    override fun onObserve() {
        super.onObserve()
        viewModel.getInformation().observe(viewLifecycleOwner) {
            when (it) {
                is UiState.Loading -> { loadingDialog.show() }
                is UiState.Failure -> {
                    loadingDialog.dismiss()
                    notify(getString(R.string.error))
                    Log.e("Dunno", it.message.toString())
                }
                is UiState.Success -> {
                    loadingDialog.dismiss()
                    account = it.data
                    if (account!!.avatar == "" || account!!.avatar == "null"){
                        binding.imgAvatar.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.logo_chat_app))
                    } else {
                        Glide.with(requireContext()).load(account!!.avatar).into(binding.imgAvatar)
                    }
                    binding.apply {
                        edtName.setText(account!!.name)
                        edtPhoneNumber.setText(account!!.numberPhone)
                    }
                }
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun initAction() {
        super.initAction()
        binding.apply {

            root.setOnTouchListener { view, event ->
                if (event.action == MotionEvent.ACTION_DOWN) {
                    view.hideKeyboard()
                }
                false
            }

            toolbar.btnBack.setOnClickListener { requireView().findNavController().popBackStack() }
            btnSave.setOnClickListener { handleSave() }
            imgAvatar.setOnClickListener { handleGallery() }
        }
    }

    private fun handleSave() {
        val strName = binding.edtName.text.toString().trim()
        val strNumber = binding.edtPhoneNumber.text.toString().trim()
        if (uri == null) {
            if (!TextUtils.isEmpty(strName) && (!TextUtils.isEmpty(strNumber))) {
                viewModel.updateInformation(strName, strNumber).observe(viewLifecycleOwner) {
                    when(it) {
                        is UiState.Loading -> loadingDialog.show()
                        is UiState.Failure -> {
                            loadingDialog.dismiss()
                            notify(getString(R.string.error))
                            Log.e("Dunno", it.message.toString())
                        }
                        is UiState.Success -> {
                            loadingDialog.dismiss()
                            notify(getString(R.string.update_successful))
                            requireView().findNavController().popBackStack()
                        }
                    }
                }
            } else {
                notify(getString(R.string.cannot_be_left_blank))
            }
        } else {
            if (!TextUtils.isEmpty(strName) && (!TextUtils.isEmpty(strNumber))) {
                viewModel.updateInformation(strName, strNumber, uri!!).observe(viewLifecycleOwner) {
                    when(it) {
                        is UiState.Loading -> loadingDialog.show()
                        is UiState.Failure -> {
                            loadingDialog.dismiss()
                            notify(getString(R.string.error))
                            Log.e("Dunno", it.message.toString())
                        }
                        is UiState.Success -> {
                            loadingDialog.dismiss()
                            notify(getString(R.string.update_successful))
                            requireView().findNavController().popBackStack()
                        }
                    }
                }
            } else {
                notify(getString(R.string.cannot_be_left_blank))
            }
        }
    }

    private fun handleGallery() {
        val i = Intent()
        i.type = "image/*"
        i.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(i, "Select Picture"), Constant.GALLERY_REQUEST_CODE)
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && data != null && requestCode == Constant.GALLERY_REQUEST_CODE) {
            uri = data.data
            Glide.with(requireContext()).asBitmap().load(Uri.parse(data.data.toString())).into(binding.imgAvatar)
        }
    }

}