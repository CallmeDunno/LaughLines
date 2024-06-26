package com.example.laughlines.ui

import android.util.Log
import android.view.View
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.laughlines.R
import com.example.laughlines.base.BaseActivity
import com.example.laughlines.databinding.ActivityMainBinding
import com.example.laughlines.ui.no_internet.NoInternetActivity
import com.example.laughlines.utils.Constant
import com.example.laughlines.utils.NetworkHelper
import com.example.laughlines.utils.SharedPreferencesManager
import com.zegocloud.uikit.prebuilt.call.invite.ZegoUIKitPrebuiltCallInvitationService
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : BaseActivity<ActivityMainBinding>() {
    override val layoutID: Int = R.layout.activity_main

    private var _navController: NavController? = null
    private val navController get() = _navController!!

    @Inject
    lateinit var sharedPreManager: SharedPreferencesManager

    override fun initView() {
        super.initView()

        NetworkHelper(context = this).observe(this, Observer { isConnected ->
            if (!isConnected) {
                goTo(this, NoInternetActivity::class.java)
            }
            return@Observer
        })


        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragment_container_view) as NavHostFragment
        _navController = navHostFragment.navController
        binding.bottomNavigationView.setupWithNavController(navHostFragment.navController)

        Log.e("Dunno", sharedPreManager.getString(Constant.Key.ID.name)  ?: "hehe")
        if (sharedPreManager.getString(Constant.Key.ID.name).isNullOrEmpty()) {
            navController.popBackStack(R.id.home_navigation, true)
            navController.navigate(R.id.login_navigation)
        } else {
            navController.popBackStack(R.id.login_navigation, true)
            navController.navigate(R.id.home_navigation)
        }
    }

    override fun initAction() {
        super.initAction()
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.homeFragment, R.id.contactFragment -> binding.bottomNavigationView.visibility = View.VISIBLE
                else -> binding.bottomNavigationView.visibility = View.GONE
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        ZegoUIKitPrebuiltCallInvitationService.unInit()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

}