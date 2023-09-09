package com.example.laughlines.view

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.laughlines.R
import com.example.laughlines.databinding.ActivityMainBinding
import com.example.laughlines.utils.SharedPreferencesManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!
    private var _navController: NavController? = null
    private val navController get() = _navController!!

    @Inject lateinit var sharedPreManager: SharedPreferencesManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initView()
    }

    override fun onStart() {
        super.onStart()
        if (sharedPreManager.getString("uid").equals("")) {
            navController.popBackStack(R.id.home_navigation, true)
            navController.navigate(R.id.login_navigation)
        } else {
            navController.popBackStack(R.id.login_navigation, true)
            navController.navigate(R.id.home_navigation)
        }
    }

    private fun initView() {
        // Set up Nav Controller for BottomNavigationView
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragment_container_view) as NavHostFragment
        _navController = navHostFragment.navController
        binding.bottomNavigationView.setupWithNavController(navHostFragment.navController)
        // Handler event when change destination
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id){
                R.id.startFragment -> binding.bottomNavigationView.visibility = View.GONE
                R.id.homeFragment -> binding.bottomNavigationView.visibility = View.VISIBLE
                R.id.chatFragment -> binding.bottomNavigationView.visibility = View.GONE
                R.id.profileFragment -> binding.bottomNavigationView.visibility = View.GONE
                R.id.inforDetailFragment -> binding.bottomNavigationView.visibility = View.GONE
                R.id.changePasswordDetailFragment -> binding.bottomNavigationView.visibility = View.GONE
            }
        }
    }
}