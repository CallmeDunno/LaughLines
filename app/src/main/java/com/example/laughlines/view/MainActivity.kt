package com.example.laughlines.view

import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.laughlines.R
import com.example.laughlines.databinding.ActivityMainBinding
import com.example.laughlines.log.Logger
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!
    private var _navController: NavController? = null
    private val navController get() = _navController!!
    private val fAuth: FirebaseAuth = FirebaseAuth.getInstance()

    companion object {
        lateinit var sharedPref : SharedPreferences
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initView()
    }

    override fun onStart() {
        super.onStart()
        if (getDataInSharePreference() == null && fAuth.currentUser == null) {
            navController.popBackStack(R.id.home_navigation, true)
            navController.navigate(R.id.login_navigation)
        } else {
            val u = fAuth.currentUser
            u?.let {
                Logger.d(it.uid)
            }
            navController.popBackStack(R.id.login_navigation, true)
            navController.navigate(R.id.home_navigation)
        }
    }

    private fun getDataInSharePreference() : String? {
        return sharedPref.getString("email", null)
    }

    private fun initView() {
        sharedPref = getSharedPreferences("data_user", MODE_PRIVATE)

        // Set up Nav Controller for BottomNavigationView
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragment_container_view) as NavHostFragment
        _navController = navHostFragment.navController
        binding.bottomNavigationView.setupWithNavController(navHostFragment.navController)
        // Handler event when change destination
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id){
                R.id.loginFragment -> binding.bottomNavigationView.visibility = View.GONE
                R.id.homeFragment -> binding.bottomNavigationView.visibility = View.VISIBLE
                R.id.chatFragment -> binding.bottomNavigationView.visibility = View.GONE
            }
        }
    }
}