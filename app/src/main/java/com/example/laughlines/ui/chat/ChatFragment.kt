package com.example.laughlines.ui.chat

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import com.bumptech.glide.Glide
import com.example.laughlines.databinding.FragmentChatBinding
import com.example.laughlines.log.Logger
import com.example.laughlines.model.DateTime
import com.example.laughlines.model.Messages
import com.example.laughlines.ui.chat.adapter.ChatAdapter
import com.example.laughlines.utils.SharedPreferencesManager
import com.example.laughlines.utils.UiState
import com.example.laughlines.viewmodel.ChatViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class ChatFragment : Fragment() {

    private var _itemBinding: FragmentChatBinding? = null
    private val itemBinding get() = _itemBinding!!
    private lateinit var chatAdapter: ChatAdapter

    //    private val chatAdapter by lazy {
//        ChatNewAdapter(uid) {
//            Logger.d(it.messageID)
//        }
//    }
    private val viewModel by viewModels<ChatViewModel>()
    @Inject lateinit var sharedPreManager: SharedPreferencesManager

    private lateinit var uid: String
    private lateinit var cid: String
    private lateinit var fid: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _itemBinding = FragmentChatBinding.inflate(inflater, container, false)
        return itemBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initVariables(arguments!!)
        initView()
        initAction()
    }

    private fun initVariables(arguments: Bundle) {
        arguments.apply {
            uid = sharedPreManager.getString("uid")!!
            cid = this.getString("cid").toString()
            fid = this.getString("fid").toString()
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initAction() {
        itemBinding.apply {

            // Khi click vào rcv, bàn phím sẽ ẩn
            rcvMessageListChat.setOnTouchListener { _, event ->
                if (event.action == MotionEvent.ACTION_DOWN) {
                    hideKeyboard(rcvMessageListChat)
                }
                false
            }

            btnBackChat.setOnClickListener { requireView().findNavController().popBackStack() }
            btnCamera.setOnClickListener {}
            btnSendMessageChat.setOnClickListener {
                val message = itemBinding.edtMessageChat.text.toString().trim()
                val sender = uid
                val recipient = fid
                val timestamp = DateTime(getCurrentTime()).toString()
                if (!TextUtils.isEmpty(message)) {
                    val m = Messages(null, message, recipient, sender, timestamp)
                    viewModel.insertMessage(m, cid).observe(viewLifecycleOwner) {
                        when(it) {
                            is UiState.Success -> {
                                itemBinding.edtMessageChat.setText("")
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

    private fun initView() {
        chatAdapter = ChatAdapter(uid)

        itemBinding.rcvMessageListChat.adapter = chatAdapter

        initViewModel()

    }

    private fun initViewModel() {
        viewModel.fetchMessage(cid).observe(viewLifecycleOwner) {
            when (it) {
                is UiState.Success -> {
                    Collections.sort(it.data, Messages.Companion.SortByDateTime())
                    chatAdapter.submitList(it.data)
                    chatAdapter.notifyItemInserted(it.data.size + 1)
                    itemBinding.rcvMessageListChat.post { // Là một phương thức được sử dụng để thực thi một tác vụ trong vòng lặp chính của RecyclerView.
                        itemBinding.rcvMessageListChat.smoothScrollToPosition(it.data.size + 1) //Là một phương thức được sử dụng để cuộn danh sách đến vị trí được chỉ định.
                    }
                }
                is UiState.Failure -> {
                    Logger.e(it.message.toString())
                }
            }

        }

        viewModel.fetchFriend(fid, cid).observe(viewLifecycleOwner) {
            when (it) {
                is UiState.Success -> {
                    itemBinding.apply {
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

    private fun hideKeyboard(view: View) {
        val imm = view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

}