package com.example.laughlines.ui.request

import android.util.Log
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import com.example.laughlines.R
import com.example.laughlines.base.BaseFragment
import com.example.laughlines.databinding.FragmentRequestBinding
import com.example.laughlines.dialog.LoadingDialog
import com.example.laughlines.model.RequestModel2
import com.example.laughlines.utils.UiState
import com.example.laughlines.utils.extensions.hide
import com.example.laughlines.utils.extensions.show
import com.example.laughlines.viewmodel.RequestViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RequestFragment : BaseFragment<FragmentRequestBinding>() {
    override val layoutId: Int = R.layout.fragment_request

    private val viewModel by viewModels<RequestViewModel>()
    private lateinit var loadingDialog: LoadingDialog

    private val adapter by lazy {
        RequestAdapter({ it1 -> handleItemClick(it1) }, { it2 -> handleAccept(it2) }, { it3 -> handleDelete(it3) })
    }

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
            Log.e("Dunno", "a")
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

    }
}