package com.example.laughlines.ui.home

import android.util.Log
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import com.example.laughlines.R
import com.example.laughlines.base.BaseFragment
import com.example.laughlines.databinding.FragmentHomeBinding
import com.example.laughlines.dialog.LoadingDialog
import com.example.laughlines.utils.SharedPreferencesManager
import com.example.laughlines.utils.UiState
import com.example.laughlines.viewmodel.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class HomeFragment : BaseFragment<FragmentHomeBinding>() {
    override val layoutId: Int = R.layout.fragment_home

    private val viewModel by viewModels<HomeViewModel>()
    private lateinit var loadingDialog: LoadingDialog

    @Inject
    lateinit var sharedPref: SharedPreferencesManager

    override fun initView() {
        super.initView()
        loadingDialog = LoadingDialog(requireContext())
    }

    override fun onObserve() {
        super.onObserve()
        viewModel.getRequest().observe(viewLifecycleOwner) {
            when (it) {
                is UiState.Loading -> {}
                is UiState.Failure -> { Log.e("Dunno", it.message.toString()) }
                is UiState.Success -> {
                    binding.nb.setNumber(it.data)
                }
            }
        }
    }

    override fun initAction() {
        super.initAction()
        binding.apply {
            btnNotification.setOnClickListener { requireView().findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToRequestFragment()) }
        }
    }

}