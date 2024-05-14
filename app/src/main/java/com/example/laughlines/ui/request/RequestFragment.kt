package com.example.laughlines.ui.request

import android.content.res.ColorStateList
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import com.bumptech.glide.Glide
import com.example.laughlines.R
import com.example.laughlines.base.BaseFragment
import com.example.laughlines.databinding.BottomSheetResultQrBinding
import com.example.laughlines.databinding.FragmentRequestBinding
import com.example.laughlines.dialog.LoadingDialog
import com.example.laughlines.model.RequestModel2
import com.example.laughlines.utils.Constant
import com.example.laughlines.utils.SharedPreferencesManager
import com.example.laughlines.utils.UiState
import com.example.laughlines.utils.extensions.hide
import com.example.laughlines.utils.extensions.show
import com.example.laughlines.viewmodel.RequestViewModel
import com.google.android.material.bottomsheet.BottomSheetDialog
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class RequestFragment : BaseFragment<FragmentRequestBinding>() {
    override val layoutId: Int = R.layout.fragment_request

    private val viewModel by viewModels<RequestViewModel>()
    private lateinit var loadingDialog: LoadingDialog

    private val adapter by lazy {
        RequestAdapter({ it1 -> handleItemClick(it1) }, { it2 -> handleAccept(it2) }, { it3 -> handleDelete(it3) })
    }

    @Inject
    lateinit var sharedPref: SharedPreferencesManager

    override fun initView() {
        super.initView()
        binding.toolbar.apply {
            tvToolbar.text = getString(R.string.request)
            btnSettings.hide()
        }
        binding.rcv.adapter = adapter
        loadingDialog = LoadingDialog(requireContext())
    }

    override fun onObserve() {
        super.onObserve()
        viewModel.getRequest().observeForever {
            when (it) {
                is UiState.Loading -> {
                    loadingDialog.show()
                }
                is UiState.Failure -> {
                    loadingDialog.dismiss()
                    Log.e("Dunno", it.message.toString())
                    notify(getString(R.string.error))
                }
                is UiState.Success -> {
                    loadingDialog.dismiss()
                    if (it.data.isNotEmpty()) {
                        adapter.submitList(it.data)
                        binding.tvEmpty.hide()
                    } else {
                        binding.tvEmpty.show()
                    }
                }
            }
        }
    }

    override fun initAction() {
        super.initAction()
        binding.apply {
            toolbar.btnBack.setOnClickListener { requireView().findNavController().popBackStack() }
        }
    }

    private fun handleItemClick(requestModel2: RequestModel2) {
        val bottomSheetBinding = BottomSheetResultQrBinding.inflate(layoutInflater)
        val bottomSheet = BottomSheetDialog(requireContext())
        bottomSheet.setContentView(bottomSheetBinding.root)

        bottomSheetBinding.btnAddFriend.hide()

        viewModel.getInformation(requestModel2.idRequest).observe(viewLifecycleOwner) {
            when (it) {
                is UiState.Loading -> {
                    bottomSheetBinding.apply {
                        vLoading.show()
                        btnAddFriend.isEnabled = false
                        btnAddFriend.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.davy_grey))
                    }
                }
                is UiState.Failure -> {
                    Log.e("Dunno", "Error: ${it.message}")
                    notify(getString(R.string.qr_error))
                }
                is UiState.Success -> {
                    bottomSheetBinding.apply {
                        vLoading.hide()
                        btnAddFriend.isEnabled = true
                        btnAddFriend.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.jungle_green))

                        val data = it.data
                        if (data.avatar == null || data.avatar == "null") {
                            Glide.with(requireView()).load(R.drawable.ic_person_green_24).into(imgAvatar)
                        } else {
                            Glide.with(requireView()).load(data.avatar).into(imgAvatar)
                        }
                        tvName.text = data.name
                        tvEmail.text = data.email
                        tvFriend.text = "${data.sumFriend} friends"
                    }
                }
            }
        }

        bottomSheet.show()

    }

    private fun handleDelete(id: String) {
        viewModel.deleteRequest(id).observe(viewLifecycleOwner) {
            when (it) {
                is UiState.Loading -> {
                    loadingDialog.show()
                }
                is UiState.Failure -> {
                    loadingDialog.dismiss()
                    Log.e("Dunno", it.message.toString())
                }
                is UiState.Success -> {
                    loadingDialog.dismiss()

                    val arr = ArrayList<RequestModel2>()
                    arr.addAll(adapter.currentList)

                    val index = arr.indexOfFirst { rm -> rm.id == id }
                    arr.removeAt(index)

                    if (arr.size == 0) {
                        binding.tvEmpty.show()
                    }

                    adapter.submitList(arr)
                    adapter.notifyDataSetChanged()

                    notify(getString(R.string.deleted_successful))
                }
            }
        }
    }

    private fun handleAccept(requestModel2: RequestModel2) {
        val myId = sharedPref.getString(Constant.Key.ID.name) ?: ""
        viewModel.acceptRequest(myId, requestModel2.idRequest)
        viewModel.deleteRequest(requestModel2.id).observe(viewLifecycleOwner) {
            when (it) {
                is UiState.Loading -> {
                    loadingDialog.show()
                }
                is UiState.Failure -> {
                    loadingDialog.dismiss()
                    Log.e("Dunno", it.message.toString())
                }
                is UiState.Success -> {
                    loadingDialog.dismiss()

                    val arr = ArrayList<RequestModel2>()
                    arr.addAll(adapter.currentList)

                    val index = arr.indexOfFirst { rm -> rm.id == requestModel2.id }
                    arr.removeAt(index)

                    if (arr.size == 0) {
                        binding.tvEmpty.show()
                    }

                    adapter.submitList(arr)
                    adapter.notifyDataSetChanged()

                    notify(getString(R.string.accept_successful))
                }
            }
        }
    }
}