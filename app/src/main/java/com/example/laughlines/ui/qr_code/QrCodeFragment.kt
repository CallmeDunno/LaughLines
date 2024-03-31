package com.example.laughlines.ui.qr_code

import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.example.laughlines.R
import com.example.laughlines.base.BaseFragment
import com.example.laughlines.databinding.FragmentQrCodeBinding
import com.example.laughlines.utils.extensions.hide
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class QrCodeFragment : BaseFragment<FragmentQrCodeBinding>() {
    override val layoutId: Int = R.layout.fragment_qr_code

    private val fragments = listOf(QrCodeScannerFragment(), QrCodeGeneratorFragment())

    private val adapter by lazy {
        QrCodeAdapter(childFragmentManager, lifecycle, fragments)
    }

    override fun initView() {
        super.initView()
        binding.apply {
            binding.toolbar.apply {
                tvToolbar.text = getText(R.string.qr_code)
                btnSettings.hide()
            }

            viewPager.adapter = adapter
            TabLayoutMediator(tabLayout, viewPager) { tab, position ->
                when (position) {
                    0 -> {
                        tab.text = getString(R.string.qr_code_scanner)
                    }
                    1 -> {
                        tab.text = getString(R.string.qr_code_generator)
                    }
                }
            }.attach()
            viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    gotoPage(position)
                    super.onPageSelected(position)
                }
            })
        }
    }

    override fun initAction() {
        super.initAction()
        binding.apply {
            tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab) {
                    when (tab.position) {
                        0 -> {
                            tabLayout.setSelectedTabIndicatorColor(ContextCompat.getColor(requireContext(), R.color.jungle_green))
                            tabLayout.setTabTextColors(ContextCompat.getColor(requireContext(), R.color.davy_grey), ContextCompat.getColor(requireContext(), R.color.jungle_green))
                        }
                        1 -> {
                            tabLayout.setSelectedTabIndicatorColor(ContextCompat.getColor(requireContext(), R.color.jungle_green))
                            tabLayout.setTabTextColors(ContextCompat.getColor(requireContext(), R.color.davy_grey), ContextCompat.getColor(requireContext(), R.color.jungle_green))
                        }
                    }
                }

                override fun onTabUnselected(tab: TabLayout.Tab) {}
                override fun onTabReselected(tab: TabLayout.Tab) {}
            })
            binding.toolbar.btnBack.setOnClickListener { requireView().findNavController().popBackStack() }
        }
    }

    private fun gotoPage(currentItem: Int) {
        binding.viewPager.currentItem = currentItem
    }
}