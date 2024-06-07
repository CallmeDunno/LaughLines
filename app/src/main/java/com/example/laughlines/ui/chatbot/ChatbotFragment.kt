package com.example.laughlines.ui.chatbot

import android.app.Activity
import android.content.Intent
import android.content.res.ColorStateList
import android.net.Uri
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import com.example.laughlines.R
import com.example.laughlines.base.BaseFragment
import com.example.laughlines.databinding.FragmentChatbotBinding
import com.example.laughlines.utils.Constant
import com.example.laughlines.utils.StorageUtils
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ChatbotFragment : BaseFragment<FragmentChatbotBinding>() {
    override val layoutId: Int = R.layout.fragment_chatbot

    private val chatbotList = ArrayList<Chatbot>()
    private val viewModel by viewModels<ChatbotViewModel>()
    private val adapter by lazy { ChatbotAdapter() }

    override fun initView() {
        super.initView()
        binding.rcv.adapter = adapter
    }

    override fun onObserve() {
        super.onObserve()
        viewModel.state.observe(viewLifecycleOwner) {
            chatbotList.add(it)
            adapter.submitList(chatbotList)
            adapter.notifyItemInserted(chatbotList.size - 1)
            binding.rcv.post { binding.rcv.smoothScrollToPosition(chatbotList.size + 1) }
        }
    }

    override fun initAction() {
        super.initAction()
        binding.apply {
            btnBack.setOnClickListener { requireView().findNavController().popBackStack() }
            btnSend.setOnClickListener { handleSend() }
            edtMessage.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    if (p0.toString().isNotEmpty()) {
                        btnSend.imageTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.jungle_green))
                    } else {
                        btnSend.imageTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.gainsboro))
                    }
                }

                override fun afterTextChanged(p0: Editable?) {
                    if (p0.toString().isNotEmpty()) {
                        btnSend.imageTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.jungle_green))
                    } else {
                        btnSend.imageTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.gainsboro))
                    }
                }
            })
            btnGallery.setOnClickListener { handleGallery() }
        }

    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && data != null && requestCode == Constant.GALLERY_REQUEST_CODE) {
            Log.e("Dunno", "Uri: ${data.data.toString()}")
            Log.e("Dunno", "Uri: ${data.dataString}")
            val bitmap = StorageUtils.uriToBitmap(requireContext(), Uri.parse(data.data.toString()))
            chatbotList.add(Chatbot("", bitmap, true))
            adapter.submitList(chatbotList)
            adapter.notifyItemInserted(chatbotList.size - 1)
            bitmap?.let { viewModel.sendMessage(bitmap) }
        }
    }

    private fun handleSend() {
        val text = binding.edtMessage.text.toString().trim()
        if (!TextUtils.isEmpty(text)) {
            binding.edtMessage.setText("")
            viewModel.sendMessage(text)
            chatbotList.add(Chatbot(text, null, true))
            adapter.submitList(chatbotList)
            adapter.notifyItemInserted(chatbotList.size - 1)
        }
    }

    private fun handleGallery() {
        val i = Intent()
        i.type = "image/*"
        i.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(i, "Select Picture"), Constant.GALLERY_REQUEST_CODE)
    }

}