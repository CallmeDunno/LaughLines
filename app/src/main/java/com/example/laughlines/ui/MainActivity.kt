package com.example.laughlines.ui

import android.view.View
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.laughlines.R
import com.example.laughlines.base.BaseActivity
import com.example.laughlines.databinding.ActivityMainBinding
import com.example.laughlines.utils.SharedPreferencesManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : BaseActivity<ActivityMainBinding>() {
    override val layoutID: Int = R.layout.activity_main

    private var _navController: NavController? = null
    private val navController get() = _navController!!

    @Inject
    lateinit var sharedPreManager: SharedPreferencesManager

    override fun onStart() {
        super.onStart()
        if (sharedPreManager.getString("uid").isNullOrEmpty()) {
            navController.popBackStack(R.id.home_navigation, true)
            navController.navigate(R.id.login_navigation)
        } else {
            navController.popBackStack(R.id.login_navigation, true)
            navController.navigate(R.id.home_navigation)
        }
    }

    override fun initView() {
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragment_container_view) as NavHostFragment
        _navController = navHostFragment.navController
        binding.bottomNavigationView.setupWithNavController(navHostFragment.navController)
    }

    override fun initAction() {
        super.initAction()
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.homeFragment, R.id.friendsListFragment -> binding.bottomNavigationView.visibility = View.VISIBLE
                else -> binding.bottomNavigationView.visibility = View.GONE
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}