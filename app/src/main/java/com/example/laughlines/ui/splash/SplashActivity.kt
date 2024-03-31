package com.example.laughlines.ui.splash

import android.annotation.SuppressLint
import com.example.laughlines.R
import com.example.laughlines.base.BaseActivity
import com.example.laughlines.databinding.ActivitySplashBinding
import com.example.laughlines.ui.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*

@AndroidEntryPoint
@SuppressLint("CustomSplashScreen")
class SplashActivity : BaseActivity<ActivitySplashBinding>() {
    override val layoutID: Int = R.layout.activity_splash
    override fun initView() {
        super.initView()
        setLayoutNoLimit()
        CoroutineScope(Job() + Dispatchers.Default).launch {
            delay(2100)
            withContext(Dispatchers.Main) {
                goTo(this@SplashActivity, MainActivity::class.java)
                finish()
            }
        }
    }

}