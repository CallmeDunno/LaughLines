package com.example.laughlines.ui.infor_detail

import android.annotation.SuppressLint
import android.text.Editable
import android.text.TextWatcher
import android.view.MotionEvent
import androidx.navigation.findNavController
import com.example.laughlines.R
import com.example.laughlines.base.BaseFragment
import com.example.laughlines.databinding.FragmentInforDetailBinding
import com.example.laughlines.utils.extensions.hideKeyboard
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class InforDetailFragment : BaseFragment<FragmentInforDetailBinding>() {

    override val layoutId: Int = R.layout.fragment_infor_detail

    override fun initView() {
        super.initView()
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun initAction() {
        binding.apply {

            root.setOnTouchListener { view, event ->
                if (event.action == MotionEvent.ACTION_DOWN) {
                    view.hideKeyboard()
                }
                false
            }

            btnBack.setOnClickListener { requireView().findNavController().popBackStack() }
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