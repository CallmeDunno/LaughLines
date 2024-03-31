package com.example.laughlines.ui.chat

import android.annotation.SuppressLint
import android.text.TextUtils
import android.view.MotionEvent
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import com.bumptech.glide.Glide
import com.example.laughlines.R
import com.example.laughlines.base.BaseFragment
import com.example.laughlines.databinding.FragmentChatBinding
import com.example.laughlines.log.Logger
import com.example.laughlines.model.DateTime
import com.example.laughlines.model.Messages
import com.example.laughlines.ui.chat.adapter.ChatAdapter
import com.example.laughlines.utils.SharedPreferencesManager
import com.example.laughlines.utils.UiState
import com.example.laughlines.utils.extensions.hideKeyboard
import com.example.laughlines.viewmodel.ChatViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class ChatFragment : BaseFragment<FragmentChatBinding>() {
    override val layoutId: Int = R.layout.fragment_chat

    private lateinit var chatAdapter: ChatAdapter

    //    private val chatAdapter by lazy {
//        ChatNewAdapter(uid) {
//            Logger.d(it.messageID)
//        }
//    }
    private val viewModel by viewModels<ChatViewModel>()
    @Inject lateinit var sharedPref: SharedPreferencesManager

    private lateinit var uid: String
    private lateinit var cid: String
    private lateinit var fid: String

    override fun initVariable() {
        super.initVariable()
        arguments.let {bundle ->
            bundle?.let {
                uid = sharedPref.getString("uid")!!
                cid = it.getString("cid").toString()
                fid = it.getString("fid").toString()
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun initAction() {
        super.initAction()
        binding.apply {
            rcvMessageListChat.setOnTouchListener { view, event ->
                if (event.action == MotionEvent.ACTION_DOWN) {
                    view.hideKeyboard()
                }
                false
            }

            btnBackChat.setOnClickListener { requireView().findNavController().popBackStack() }
            btnCamera.setOnClickListener {}
            btnSendMessageChat.setOnClickListener {
                val message = edtMessageChat.text.toString().trim()
                val sender = uid
                val recipient = fid
                val timestamp = DateTime(getCurrentTime()).toString()
                if (!TextUtils.isEmpty(message)) {
                    val m = Messages(null, message, recipient, sender, timestamp)
                    viewModel.insertMessage(m, cid).observe(viewLifecycleOwner) {
                        when(it) {
                            is UiState.Loading -> {}
                            is UiState.Success -> {
                                edtMessageChat.setText("")
                                Logger.d(it.data)
                            }
                            is UiState.Failure -> {
                                Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
            }
        }
    }

    override fun initView() {
        super.initView()
        chatAdapter = ChatAdapter(uid)

        binding.rcvMessageListChat.adapter = chatAdapter

        initViewModel()

    }

    private fun initViewModel() {
        viewModel.fetchMessage(cid).observe(viewLifecycleOwner) {
            when (it) {
                is UiState.Loading -> {}
                is UiState.Success -> {
                    Collections.sort(it.data, Messages.Companion.SortByDateTime())
                    chatAdapter.submitList(it.data)
                    chatAdapter.notifyItemInserted(it.data.size + 1)
                    binding.rcvMessageListChat.post { // Là một phương thức được sử dụng để thực thi một tác vụ trong vòng lặp chính của RecyclerView.
                        binding.rcvMessageListChat.smoothScrollToPosition(it.data.size + 1) //Là một phương thức được sử dụng để cuộn danh sách đến vị trí được chỉ định.
                    }
                }
                is UiState.Failure -> {
                    Logger.e(it.message.toString())
                }
            }

        }

        viewModel.fetchFriend(fid, cid).observe(viewLifecycleOwner) {
            when (it) {
                is UiState.Loading -> {}
                is UiState.Success -> {
                    binding.apply {
                        tvNameChat.text = it.data.name
                        Glide.with(requireView()).load(it.data.avatarUrl).into(imgAvtChat)
                    }
                }
                is UiState.Failure -> {
                    Logger.e(it.message.toString())
                }
            }
        }
    }

    @SuppressLint("SimpleDateFormat")
    private fun getCurrentTime(): String {
        val formatter = SimpleDateFormat("dd-MM-yyyy hh:mm")
        return formatter.format(Date())
    }

}