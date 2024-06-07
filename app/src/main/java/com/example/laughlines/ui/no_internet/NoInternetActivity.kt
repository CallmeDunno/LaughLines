package com.example.laughlines.ui.no_internet

import androidx.lifecycle.Observer
import com.example.laughlines.R
import com.example.laughlines.base.BaseActivity
import com.example.laughlines.databinding.ActivityNoInternetBinding
import com.example.laughlines.utils.NetworkHelper
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NoInternetActivity : BaseActivity<ActivityNoInternetBinding>() {
    override val layoutID: Int = R.layout.activity_no_internet

    override fun initView() {
        super.initView()
        NetworkHelper(context = this).observe(this, Observer { isConnected ->
            if (isConnected) {
                finish()
            }
            return@Observer
        })
    }

    override fun onBackPressed() {}

}