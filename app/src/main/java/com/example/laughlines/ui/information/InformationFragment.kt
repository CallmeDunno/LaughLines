package com.example.laughlines.ui.information

import android.annotation.SuppressLint
import android.text.Editable
import android.text.TextWatcher
import android.view.MotionEvent
import androidx.navigation.findNavController
import com.example.laughlines.R
import com.example.laughlines.base.BaseFragment
import com.example.laughlines.databinding.FragmentInformationBinding
import com.example.laughlines.utils.extensions.hide
import com.example.laughlines.utils.extensions.hideKeyboard
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class InformationFragment : BaseFragment<FragmentInformationBinding>() {

    override val layoutId: Int = R.layout.fragment_information

    override fun initView() {
        super.initView()
        binding.toolbar.apply {
            btnSettings.hide()
            tvToolbar.text = getString(R.string.change_information)
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
            btnSave.setOnClickListener { requireView().findNavController().popBackStack() }
            imgAvatar.setOnClickListener { }
            edtName.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

                override fun afterTextChanged(p0: Editable?) {
                    val content = edtName.text.toString().trim()
                    edtName.error = if (content.length >= 6) null else "Minimum length is 6"
                }
            })
        }
    }

}