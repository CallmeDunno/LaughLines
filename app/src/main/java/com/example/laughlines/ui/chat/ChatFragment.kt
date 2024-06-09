package com.example.laughlines.ui.chat

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.os.Build
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.example.laughlines.R
import com.example.laughlines.base.BaseFragment
import com.example.laughlines.databinding.BottomSheetShareContentBinding
import com.example.laughlines.databinding.FragmentChatBinding
import com.example.laughlines.dialog.LoadingDialog
import com.example.laughlines.model.Messages
import com.example.laughlines.ui.chat.adapter.MessageAdapter
import com.example.laughlines.utils.Constant
import com.example.laughlines.utils.Constant.GALLERY_REQUEST_CODE
import com.example.laughlines.utils.EncryptMessages
import com.example.laughlines.utils.SharedPreferencesManager
import com.example.laughlines.utils.UiState
import com.example.laughlines.utils.extensions.hideKeyboard
import com.example.laughlines.viewmodel.ChatViewModel
import com.example.laughlines.viewmodel.ImageViewModel
import com.google.android.gms.location.*
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.zegocloud.uikit.prebuilt.call.ZegoUIKitPrebuiltCallService
import com.zegocloud.uikit.prebuilt.call.config.ZegoNotificationConfig
import com.zegocloud.uikit.prebuilt.call.invite.ZegoUIKitPrebuiltCallInvitationConfig
import com.zegocloud.uikit.service.defines.ZegoUIKitUser
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ChatFragment : BaseFragment<FragmentChatBinding>() {
    override val layoutId: Int = R.layout.fragment_chat

    private lateinit var chatId: String
    private lateinit var friendId: String
    private lateinit var myId: String
    private val arg: ChatFragmentArgs by navArgs()

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private val adapter by lazy {
        MessageAdapter {
            if (it.type == Constant.LOCATION) {
                val action = ChatFragmentDirections.actionChatFragmentToMapFragment(it.message)
                requireView().findNavController().navigate(action)
            }
        }
    }

    private val viewModel by viewModels<ChatViewModel>()
    private val imageViewModel by viewModels<ImageViewModel>()
    private lateinit var encryptMessages: EncryptMessages

    @Inject
    lateinit var sharedPref: SharedPreferencesManager

    override fun initView() {
        super.initView()
        myId = sharedPref.getString(Constant.Key.ID.name) ?: Constant.ID_DEFAULT
        adapter.setMyId(myId)
        adapter.createKey(chatId.substring(0, 16))
        binding.rcv.adapter = adapter
        binding.rcv.itemAnimator = null

        val application = requireActivity().application // Android's application context
        val appID: Long = 1386242158   // yourAppID
        val appSign = "ffb364052c3c90ef160dbae68eb65c89e2332dfb4756bb33e528698130309d70"  // yourAppSign
        val userID = myId // yourUserID, userID should only contain numbers, English characters, and '_'.
        val userName = "ccc"   // yourUserName
        val callInvitationConfig = ZegoUIKitPrebuiltCallInvitationConfig()
        val notificationConfig = ZegoNotificationConfig()
        notificationConfig.sound = "zego_uikit_sound_call"
        notificationConfig.channelID = "CallInvitation"
        notificationConfig.channelName = "CallInvitation"
        ZegoUIKitPrebuiltCallService.init(application, appID, appSign, userID, userName, callInvitationConfig);
    }

    override fun initData() {
        super.initData()
        chatId = arg.cid
        friendId = arg.fid

        encryptMessages = EncryptMessages()
        encryptMessages.createKey(chatId.substring(0, 16))

    }

    override fun onObserve() {
        super.onObserve()
        viewModel.getInformationOfFriend(friendId).observe(viewLifecycleOwner) {
            when (it) {
                is UiState.Loading -> {}
                is UiState.Failure -> {
                    Log.e("Dunno", it.message.toString())
                    notify(getString(R.string.error))
                }
                is UiState.Success -> {
                    if (it.data.avatar == "" || it.data.avatar == "null") {
                        Glide.with(requireView()).load(ContextCompat.getDrawable(requireContext(), R.drawable.logo_chat_app)).into(binding.imgAvatar)
                    } else {
                        Glide.with(requireView()).load(it.data.avatar).into(binding.imgAvatar)
                    }
                    binding.tvName.text = it.data.name
                }
            }
        }

        viewModel.getMessageList(chatId).observe(viewLifecycleOwner) {
            Log.w("Dunno", "aaaa")
            val arr = ArrayList<Messages>()
            arr.clear()
            for (i in it){
                if (i.type == Constant.IMAGE) {
                    arr.add(i)
                } else {
                    val text = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) encryptMessages.decode(i.message) else i.message
                    arr.add(Messages(text, i.sender, i.timestamp, i.type))
                }
            }
            adapter.submitList(arr)
            adapter.notifyItemInserted(it.size - 1)
            binding.rcv.post {
                binding.rcv.smoothScrollToPosition(it.size + 1)
            }
        }
    }

    override fun initAction() {
        super.initAction()
        binding.apply {
            layoutParent.setOnClickListener { it.hideKeyboard() }
            btnBack.setOnClickListener { requireView().findNavController().popBackStack() }
            btnSend.setOnClickListener { handleSendMessage() }
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
            btnMore.setOnClickListener { handleMore() }
            handleVideoCall()
        }
    }

    private fun handleVideoCall() {
        binding.btnCall.setIsVideoCall(true)
        binding.btnCall.resourceID = "zego_uikit_call"
        binding.btnCall.setInvitees(listOf(ZegoUIKitUser(friendId)))
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && data != null && requestCode == GALLERY_REQUEST_CODE) {
            Log.e("Dunno", "Uri: ${data.data.toString()}")
            Log.e("Dunno", "Uri: ${data.dataString}")
            val loadingDialog = LoadingDialog(requireContext())
            loadingDialog.show()
            imageViewModel.pushMessage(chatId, Messages(data.data.toString(), myId, System.currentTimeMillis(), Constant.IMAGE)).observe(viewLifecycleOwner) {
                when (it) {
                    is UiState.Loading -> {}
                    is UiState.Failure -> {
                        Log.e("Dunno", it.message.toString())
                    }
                    is UiState.Success -> {
                        loadingDialog.dismiss()
                    }
                }
            }
        }
    }

    private fun getLatLng() {
        val loadingDialog = LoadingDialog(requireContext())
        loadingDialog.show()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return
        }
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                val text = "${location.longitude} ${location.latitude}"
                val messages = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) encryptMessages.encode(text) else text
                viewModel.pushMessage(chatId, Messages(messages, myId, System.currentTimeMillis(), Constant.LOCATION)).observe(viewLifecycleOwner) {
                    when (it) {
                        is UiState.Loading -> {}
                        is UiState.Failure -> {
                            loadingDialog.dismiss()
                            notify(getString(R.string.error))
                            Log.e("Dunno", it.message.toString())
                        }
                        is UiState.Success -> {
                            loadingDialog.dismiss()
                        }
                    }
                }
            } else {
                loadingDialog.dismiss()
                notify(getString(R.string.error))
            }
        }.addOnFailureListener {
            loadingDialog.dismiss()
            notify(getString(R.string.error))
            Log.e("Dunno", it.message.toString())
        }
    }

    private fun handleMore() {
        val bottomSheetBinding = BottomSheetShareContentBinding.inflate(layoutInflater)
        val bottomSheet = BottomSheetDialog(requireContext())
        bottomSheet.setContentView(bottomSheetBinding.root)
        bottomSheetBinding.apply {
            btnCamera.setOnClickListener {
                val action = ChatFragmentDirections.actionChatFragmentToCameraFragment(chatId)
                requireView().findNavController().navigate(action)
                bottomSheet.dismiss()
            }
            btnGallery.setOnClickListener {
                bottomSheet.dismiss()
                val i = Intent()
                i.type = "image/*"
                i.action = Intent.ACTION_GET_CONTENT
                startActivityForResult(Intent.createChooser(i, "Select Picture"), GALLERY_REQUEST_CODE)
            }
            btnLocation.setOnClickListener {
                getLatLng()
                bottomSheet.dismiss()
            }
        }
        bottomSheet.show()
    }

    private fun handleSendMessage() {
        val text = binding.edtMessage.text.toString().trim()
        if (!TextUtils.isEmpty(text)) {

            val messages = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) encryptMessages.encode(text) else text

            binding.edtMessage.setText("")
            viewModel.pushMessage(chatId, Messages(messages, myId, System.currentTimeMillis(), Constant.MESSAGE)).observe(viewLifecycleOwner) {
                when (it) {
                    is UiState.Loading -> {}
                    is UiState.Failure -> {
                        Log.e("Dunno", it.message.toString())
                    }
                    is UiState.Success -> {
                        binding.edtMessage.setText("")
                    }
                }
            }
        }
    }
}