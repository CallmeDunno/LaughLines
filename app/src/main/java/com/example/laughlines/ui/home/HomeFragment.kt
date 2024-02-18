package com.example.laughlines.ui.home

import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import com.example.laughlines.R
import com.example.laughlines.base.BaseFragment
import com.example.laughlines.databinding.FragmentHomeBinding
import com.example.laughlines.listener.IClickItem
import com.example.laughlines.model.Friend
import com.example.laughlines.ui.home.adapter.FriendAdapter
import com.example.laughlines.utils.SharedPreferencesManager
import com.example.laughlines.viewmodel.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class HomeFragment : BaseFragment<FragmentHomeBinding>() {
    override val layoutId: Int = R.layout.fragment_home

    private val friendAdapter by lazy { FriendAdapter() }
    private val viewModel by viewModels<HomeViewModel>()

    @Inject
    lateinit var sharedPreManager: SharedPreferencesManager

    override fun initAction() {
        friendAdapter.setOnClickUserItem(object : IClickItem {
            override fun setOnClickItemChat(friend: Friend) {
                val action = HomeFragmentDirections.actionHomeFragmentToChatFragment(friend.cid, friend.fid)
                requireView().findNavController().navigate(action)
            }
        })
    }

    override fun initView() {
        requireActivity().window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
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