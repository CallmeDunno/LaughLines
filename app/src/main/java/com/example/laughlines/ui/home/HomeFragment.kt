package com.example.laughlines.ui.home

import android.util.Log
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import com.bumptech.glide.Glide
import com.example.laughlines.R
import com.example.laughlines.base.BaseFragment
import com.example.laughlines.databinding.FragmentHomeBinding
import com.example.laughlines.dialog.LoadingDialog
import com.example.laughlines.dialog.StatusDialog
import com.example.laughlines.model.Account
import com.example.laughlines.utils.SharedPreferencesManager
import com.example.laughlines.utils.UiState
import com.example.laughlines.viewmodel.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class HomeFragment : BaseFragment<FragmentHomeBinding>() {
    override val layoutId: Int = R.layout.fragment_home

    private val friendIdList = ArrayList<String>()
    private lateinit var myAccount: Account

    private val viewModel by viewModels<HomeViewModel>()
    private lateinit var loadingDialog: LoadingDialog

    @Inject
    lateinit var sharedPref: SharedPreferencesManager

    override fun initView() {
        super.initView()
        friendIdList.clear()
        loadingDialog = LoadingDialog(requireContext())
    }

    override fun onObserve() {
        super.onObserve()
        viewModel.getMyAccount().observe(viewLifecycleOwner) {
            myAccount = it
            Log.d("Dunno", myAccount.toString())
            if (myAccount.avatar == null || myAccount.avatar == "" || myAccount.avatar == "null") {
                Glide.with(requireView()).load(ContextCompat.getDrawable(requireContext(), R.drawable.logo_chat_app)).into(binding.imgAvatar)
            } else {
                Glide.with(requireView()).load(myAccount.avatar).into(binding.imgAvatar)
            }
        }

        viewModel.getRequest().observe(viewLifecycleOwner) {
            when (it) {
                is UiState.Loading -> {}
                is UiState.Failure -> { Log.e("Dunno", it.message.toString()) }
                is UiState.Success -> { binding.nb.setNumber(it.data) }
            }
        }

        viewModel.getFriendIdList().observe(viewLifecycleOwner) {
            when (it) {
                is UiState.Loading -> { loadingDialog.show() }
                is UiState.Failure -> {
                    loadingDialog.dismiss()
                    Log.e("Dunno", it.message.toString())
                    notify(getString(R.string.error))
                }
                is UiState.Success -> {
                    loadingDialog.dismiss()
                    friendIdList.clear()
                    friendIdList.addAll(it.data)
                }
            }
        }
    }

    override fun initAction() {
        super.initAction()
        binding.apply {
            btnNotification.setOnClickListener { requireView().findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToRequestFragment()) }
            btnMyStatus.setOnClickListener {
                val dialog = StatusDialog(requireContext(), myAccount) {

                }
                dialog.show()
            }
        }
    }

}