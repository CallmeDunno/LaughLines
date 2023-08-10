package com.example.laughlines.view.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.laughlines.databinding.FragmentHomeBinding
import com.example.laughlines.log.Logger
import com.example.laughlines.view.home.adapter.ChatAdapter
import com.example.laughlines.view.home.data.Chat

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    private fun initView() {
        val list : List<Chat> = listOf(Chat(1,"https://lh3.googleusercontent.com/a/AAcHTteWaWnNamX2PqjWcq2robLtpQXoJcvfo6DPYjjPtDNkOLs=s96-c", "Dungx", "Dungx"),
            Chat(2,"https://lh3.googleusercontent.com/a/AAcHTteWaWnNamX2PqjWcq2robLtpQXoJcvfo6DPYjjPtDNkOLs=s96-c", "Dungx", "Dungx")
            , Chat(3,"https://lh3.googleusercontent.com/a/AAcHTteWaWnNamX2PqjWcq2robLtpQXoJcvfo6DPYjjPtDNkOLs=s96-c", "Dungx", "Dungx")
        , Chat(4,"https://lh3.googleusercontent.com/a/AAcHTteWaWnNamX2PqjWcq2robLtpQXoJcvfo6DPYjjPtDNkOLs=s96-c", "Dungx", "Dungx")
            , Chat(5,"https://lh3.googleusercontent.com/a/AAcHTteWaWnNamX2PqjWcq2robLtpQXoJcvfo6DPYjjPtDNkOLs=s96-c", "Dungx", "Dungx"))
        val chatAdapter = ChatAdapter(Chat.ChatDiffUtil)
        chatAdapter.submitList(list)
        binding.rcvListChatHome.adapter = chatAdapter
        chatAdapter.setOnClickChatItem(object : ChatAdapter.IChatIemClick {
            override fun onClickChatItem(chat: Chat) {
                Logger.d("${chat.id}")
            }

        })
    }

}