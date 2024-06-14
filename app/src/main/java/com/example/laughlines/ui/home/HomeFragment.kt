package com.example.laughlines.ui.home

import android.annotation.SuppressLint
import android.util.Log
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import com.bumptech.glide.Glide
import com.example.laughlines.R
import com.example.laughlines.base.BaseFragment
import com.example.laughlines.databinding.FragmentHomeBinding
import com.example.laughlines.dialog.LoadingDialog
import com.example.laughlines.dialog.StatusDialog
import com.example.laughlines.model.Account
import com.example.laughlines.ui.home.adapter.ChatAdapter
import com.example.laughlines.ui.home.adapter.StatusAdapter
import com.example.laughlines.utils.UiState
import com.example.laughlines.utils.extensions.hide
import com.example.laughlines.viewmodel.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class HomeFragment : BaseFragment<FragmentHomeBinding>() {
    override val layoutId: Int = R.layout.fragment_home

    private lateinit var myAccount: Account

    private val viewModel by viewModels<HomeViewModel>()
    private lateinit var loadingDialog: LoadingDialog

    private val statusAdapter by lazy {
        StatusAdapter {
            val dialog = StatusDialog(requireContext(), it, false) {}
            dialog.show()
        }
    }

    private val chatAdapter by lazy {
        ChatAdapter {
            Log.e("Dunno", it.friendId)
            val action = HomeFragmentDirections.actionHomeFragmentToChatFragment(it.chatId, it.friendId)
            requireView().findNavController().navigate(action)
        }
    }

    override fun initView() {
        super.initView()
        loadingDialog = LoadingDialog(requireContext())
        binding.apply {
            rcvStatus.adapter = statusAdapter
            rcvChat.adapter = chatAdapter
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onObserve() {
        super.onObserve()
        viewModel.getMyAccount().observe(viewLifecycleOwner) {
            myAccount = it
            if (myAccount.avatar == "" || myAccount.avatar == "null") {
                binding.imgAvatar.setImageResource(R.drawable.logo_chat_app)
            } else {
                Glide.with(requireContext()).load(myAccount.avatar).into(binding.imgAvatar)
            }
        }

        viewModel.getRequest().observe(viewLifecycleOwner) {
            when (it) {
                is UiState.Loading -> {}
                is UiState.Failure -> { Log.e("Dunno", it.message.toString()) }
                is UiState.Success -> { binding.nb.setNumber(it.data) }
                else -> {}
            }
        }

        viewModel.getAllFriend().observe(viewLifecycleOwner) {
            val statusArr = ArrayList<Account>()
            statusArr.clear()
            if (it.isNotEmpty()) {
                binding.tvWarningHome.hide()
            } else {
                binding.tvWarningHome.visibility = View.VISIBLE
            }
            for (c in it){
                if (c.account.status != "" && c.account.status != "null") {
                    statusArr.add(c.account)
                }
            }
            statusAdapter.submitList(statusArr)
            statusAdapter.notifyDataSetChanged()
            binding.rcvStatus.post { binding.rcvStatus.smoothScrollToPosition(0) }
            chatAdapter.submitList(it)
            chatAdapter.notifyDataSetChanged()
        }
    }

    override fun initAction() {
        super.initAction()
        binding.apply {
            btnNotification.setOnClickListener { requireView().findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToRequestFragment()) }
            btnMyStatus.setOnClickListener {
                viewModel.getMyAccount().observe(viewLifecycleOwner){
                    myAccount = it
                }
                val dialog = StatusDialog(requireContext(), myAccount) { status ->
                    viewModel.updateStatus(status).observe(viewLifecycleOwner) {
                        when (it) {
                            is UiState.Loading -> {}
                            is UiState.Failure -> {
                                Log.e("Dunno", it.message.toString())
                                notify(getString(R.string.error))
                            }
                            is UiState.Success -> {
                                notify(getString(R.string.update_successful))
                            }
                            else -> {}
                        }
                    }
                }
                dialog.show()
            }
        }
    }

}