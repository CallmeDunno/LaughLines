package com.example.laughlines.ui.chat

import android.annotation.SuppressLint
import com.example.laughlines.R
import com.example.laughlines.base.BaseFragment
import com.example.laughlines.databinding.FragmentChatBinding
import com.example.laughlines.utils.SharedPreferencesManager
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class ChatFragment : BaseFragment<FragmentChatBinding>() {
    override val layoutId: Int = R.layout.fragment_chat

    @Inject lateinit var sharedPref: SharedPreferencesManager

    @SuppressLint("SimpleDateFormat")
    private fun getCurrentTime(): String {
        val formatter = SimpleDateFormat("dd-MM-yyyy hh:mm")
        return formatter.format(Date())
    }

}