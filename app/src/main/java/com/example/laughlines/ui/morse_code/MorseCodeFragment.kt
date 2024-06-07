package com.example.laughlines.ui.morse_code

import android.text.Editable
import android.text.TextWatcher
import android.text.method.ScrollingMovementMethod
import android.view.View
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import com.example.laughlines.R
import com.example.laughlines.base.BaseFragment
import com.example.laughlines.databinding.FragmentMorseCodeBinding
import com.example.laughlines.model.MorseCode
import com.example.laughlines.utils.extensions.hideKeyboard
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MorseCodeFragment : BaseFragment<FragmentMorseCodeBinding>() {
    override val layoutId: Int = R.layout.fragment_morse_code

    private lateinit var morseCode: MorseCode
    private var isSosMode = false

    override fun initView() {
        super.initView()
        morseCode = MorseCode(requireContext())
        binding.apply {
            edtText.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    if (p0.toString().isEmpty()) {
                        with(tvLabelInput) {
                            text = getString(R.string.please_fill_in_this_box)
                            setTextColor(ContextCompat.getColor(requireContext(), R.color.orange_red))
                        }
                        imgWarning.visibility = View.VISIBLE
                    } else {
                        with(tvLabelInput) {
                            text = getString(R.string.your_text)
                            setTextColor(ContextCompat.getColor(requireContext(), R.color.davy_grey))
                        }
                        imgWarning.visibility = View.GONE
                    }
                }

                override fun afterTextChanged(p0: Editable?) {}
            })
        }
    }

    override fun initAction() {
        super.initAction()
        binding.apply {
            binding.apply {
                layout.setOnClickListener { it.hideKeyboard() }
                btnBack.setOnClickListener {
                    morseCode.cancelFlashMorseCode()
                    requireView().findNavController().popBackStack()
                }
                btnTransmit.setOnClickListener {
                    it.hideKeyboard()
                    edtText.clearFocus()
                    val text = edtText.text.toString().trim()
                    if (text.isNotEmpty()) {
                        btnTransmit.background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_border_gray_conner_10)
                        with(edtMorseCode) {
                            setText(morseCode.getMorseCodeFromText(text))
                            movementMethod = ScrollingMovementMethod()
                            this.visibility = View.VISIBLE
                        }
                        tvLabelMorseCode.visibility = View.VISIBLE
                    } else {
                        btnTransmit.background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_border_gray_conner_10)
                        with(edtMorseCode) {
                            setText("")
                            this.visibility = View.GONE
                        }
                        tvLabelMorseCode.visibility = View.GONE
                    }
                    if (!tvStart.isEnabled && !tvOff.isEnabled) {
                        tvStart.isEnabled = true
                        tvOff.isEnabled = false
                        tvStart.setTextColor(ContextCompat.getColor(requireContext(), R.color.jungle_green))
                        tvOff.setTextColor(ContextCompat.getColor(requireContext(), R.color.davy_grey))
                        btnTransmit.background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_border_gray_conner_10)
                    }
                }
                tvStart.setOnClickListener {
                    if (tvStart.isEnabled) {
                        tvStart.isEnabled = false
                        tvOff.isEnabled = true
                        tvStart.setTextColor(ContextCompat.getColor(requireContext(), R.color.davy_grey))
                        tvOff.setTextColor(ContextCompat.getColor(requireContext(), R.color.jungle_green))
                        morseCode.setMorseCode(edtMorseCode.text.toString().trim())
                        morseCode.flashMorseCode()
                        morseCode.isCompleted.observe(viewLifecycleOwner) {
                            if (it) {
                                tvStart.isEnabled = true
                                tvOff.isEnabled = false
                                tvStart.setTextColor(ContextCompat.getColor(requireContext(), R.color.jungle_green))
                                tvOff.setTextColor(ContextCompat.getColor(requireContext(), R.color.davy_grey))
                                morseCode.isCompleted.postValue(false)
                            }
                        }
                    }
                }
                tvOff.setOnClickListener {
                    if (tvOff.isEnabled) {
                        tvStart.isEnabled = true
                        tvOff.isEnabled = false
                        morseCode.cancelFlashMorseCode()
                        binding.tvOff.setTextColor(ContextCompat.getColor(requireContext(), R.color.davy_grey))
                        binding.tvStart.setTextColor(ContextCompat.getColor(requireContext(), R.color.jungle_green))
                    }
                }
                edtText.setOnFocusChangeListener { _, b ->
                    if (b) {
                        btnTransmit.background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_border_green_conner_10)
                    }
                }
                btnSos.setOnClickListener {
                    isSosMode = !isSosMode
                    if (isSosMode) {
                        tvSos.text = "Off"
                        morseCode.cancelFlashMorseCode()
                        morseCode.setMorseCode(morseCode.textToMorse("sos"))
                        morseCode.flashMorseCode()
                        morseCode.isCompleted.observe(viewLifecycleOwner) {
                            if (it) {
                                morseCode.cancelFlashMorseCode()
                                morseCode.setMorseCode(" ${morseCode.textToMorse("sos")}")
                                morseCode.flashMorseCode()
                            } else {
                                morseCode.cancelFlashMorseCode()
                            }
                        }
                    } else {
                        tvSos.text = "SOS"
                        morseCode.isCompleted.postValue(false)
                    }

                }
            }
        }
    }

}