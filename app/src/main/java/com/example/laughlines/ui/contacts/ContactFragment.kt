package com.example.laughlines.ui.contacts

import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.DisplayMetrics
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import com.example.laughlines.R
import com.example.laughlines.base.BaseFragment
import com.example.laughlines.databinding.FragmentContactBinding
import com.example.laughlines.databinding.PopupMoreBinding
import com.example.laughlines.dialog.DeleteFriendDialog
import com.example.laughlines.dialog.InformationDialog
import com.example.laughlines.dialog.LoadingDialog
import com.example.laughlines.model.Contact
import com.example.laughlines.utils.UiState
import com.example.laughlines.utils.extensions.hide
import com.example.laughlines.utils.extensions.hideKeyboard
import com.example.laughlines.utils.extensions.show
import com.example.laughlines.viewmodel.ContactViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ContactFragment : BaseFragment<FragmentContactBinding>() {
    override val layoutId: Int = R.layout.fragment_contact

    private var isSearching = false
    private lateinit var loadingDialog: LoadingDialog
    private val currentList = ArrayList<Contact>()

    private val viewModel by viewModels<ContactViewModel>()

    private val adapter by lazy {
        ContactAdapter({ handleItemClick(it) }, { contact, view -> handleMoreClick(contact, view) })
    }

    override fun initView() {
        super.initView()
        binding.apply {
            rcv.adapter = adapter
            rcv.itemAnimator = null
        }
        loadingDialog = LoadingDialog(requireContext())
    }

    override fun onObserve() {
        super.onObserve()
        viewModel.getAllFriend().observe(viewLifecycleOwner) {
            when (it) {
                is UiState.Loading -> loadingDialog.show()
                is UiState.Failure -> {
                    loadingDialog.dismiss()
                    Log.e("Dunno", it.message.toString())
                    notify(getString(R.string.error))
                }
                is UiState.Success -> {
                    loadingDialog.dismiss()
                    currentList.clear()
                    currentList.addAll(it.data)
                    adapter.submitList(currentList)
                    adapter.notifyDataSetChanged()
                    if (it.data.isNotEmpty()) {
                        binding.tvContactEmpty.hide()
                    } else {
                        binding.tvContactEmpty.show()
                    }
                }
            }
        }
    }

    override fun initAction() {
        super.initAction()
        binding.apply {
            btnSearch.setOnClickListener {
                if (currentList.isEmpty()) {
                    notify(getString(R.string.this_action_cannot_be_performed))
                } else {
                    if (isSearching) {
                        tvTitle.show()
                        edtSearch.hide()
                        btnAddFriend.show()
                        it.hideKeyboard()
                        btnSearch.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_search))
                        edtSearch.setText("")
                        adapter.submitList(currentList)
                        adapter.notifyDataSetChanged()
                        tvNotFound.hide()
                    } else {
                        btnSearch.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_close))
                        btnSearch.imageTintList = ColorStateList.valueOf(Color.WHITE)
                        tvTitle.hide()
                        btnAddFriend.hide()
                        edtSearch.show()
                    }
                    isSearching = !isSearching
                }

            }
            btnAddFriend.setOnClickListener {
                requireView().findNavController().navigate(R.id.action_contactFragment_to_qrCodeFragment)
            }
            edtSearch.addTextChangedListener(object : TextWatcher{
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    val query = p0.toString().trim()
                    if (!TextUtils.isEmpty(query)) {
                        val array = ArrayList<Contact>()
                        for (i in currentList) {
                            if (i.account.name.lowercase().contains(query)) {
                                array.add(i)
                            }
                        }
                        if (array.isEmpty()) {
                            tvNotFound.show()
                        } else {
                            tvNotFound.hide()
                        }
                        adapter.setFilterList(array)
                    }
                }

                override fun afterTextChanged(p0: Editable?) {
                    val str = p0.toString().trim()
                    if (str.isEmpty()) {
                        tvNotFound.hide()
                        adapter.submitList(currentList)
                        adapter.notifyDataSetChanged()
                        binding.rcv.post { binding.rcv.smoothScrollToPosition(0) }
                    }
                }
            })
        }
    }

    private fun handleMoreClick(contact: Contact, view: View) {
        val bindingPopup = PopupMoreBinding.inflate(LayoutInflater.from(requireContext()))
        val popupWindow = PopupWindow(bindingPopup.root, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)

        popupWindow.apply {
            contentView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            isFocusable = false
            isOutsideTouchable = true
        }

        bindingPopup.apply {
            btnDelete.setOnClickListener {
                popupWindow.dismiss()
                val deleteDialog = DeleteFriendDialog(requireContext()) {
                    viewModel.deleteFriend(contact.chatId, contact.friendId)
                    notify(getString(R.string.deleted_successful))

                    updateRecycleView(contact)
                }
                deleteDialog.show()
            }
        }

        val location = IntArray(2)
        view.getLocationOnScreen(location)
        val displayMetrics = DisplayMetrics()
        requireActivity().windowManager.defaultDisplay.getMetrics(displayMetrics)
        val height = displayMetrics.heightPixels
        val x = location[0] - popupWindow.contentView.measuredWidth + view.width
        val y = if (location[1] > height * 6 / 7) location[1] - popupWindow.contentView.measuredHeight else location[1] + view.height

        popupWindow.showAtLocation(view, Gravity.NO_GRAVITY, x, y)
    }

    private fun updateRecycleView(contact: Contact) {
        val arr = ArrayList<Contact>()
        arr.addAll(adapter.currentList)
        arr.remove(contact)
        currentList.remove(contact)
        if (arr.size == 0) {
            binding.tvContactEmpty.show()
        } else {
            binding.tvContactEmpty.hide()
        }
        adapter.submitList(arr)
        adapter.notifyDataSetChanged()
    }

    private fun handleItemClick(contact: Contact) {
        InformationDialog(requireContext(), contact.account) {
            val action = ContactFragmentDirections.actionContactsFragmentToChatFragment(contact.chatId, contact.friendId)
            requireView().findNavController().navigate(action)
        }.show()
    }
}