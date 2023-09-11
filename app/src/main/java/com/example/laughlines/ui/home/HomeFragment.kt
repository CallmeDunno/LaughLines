package com.example.laughlines.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import com.example.laughlines.databinding.FragmentHomeBinding
import com.example.laughlines.listener.IClickItem
import com.example.laughlines.model.Friend
import com.example.laughlines.ui.home.adapter.FriendAdapter
import com.example.laughlines.utils.SharedPreferencesManager
import com.example.laughlines.viewmodel.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val friendAdapter by lazy { FriendAdapter() }
    private val viewModel by viewModels<HomeViewModel>()

    @Inject lateinit var sharedPreManager: SharedPreferencesManager

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
        initAction()
    }

    private fun initAction() {
        friendAdapter.setOnClickUserItem(object : IClickItem {
            override fun setOnClickItemChat(friend: Friend) {
                val action = HomeFragmentDirections.actionHomeFragmentToChatFragment(
                    friend.cid, friend.fid)
                requireView().findNavController().navigate(action)
            }
        })
    }

    private fun initView() {
        binding.rcvChatsListHome.adapter = friendAdapter
        initViewModel()
    }

    private fun initViewModel() {
        val uid = sharedPreManager.getString("uid")
        uid?.let {
            viewModel.fetchFriendList(it).observe(viewLifecycleOwner) { friends ->
                if (friends.isEmpty()) {
                    binding.tvWarningHome.visibility = View.VISIBLE
                } else {
                    friendAdapter.submitList(friends)
                    binding.tvWarningHome.visibility = View.INVISIBLE
                }
            }
        }

    }

}