package com.example.laughlines

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.laughlines.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!
    private  var _navController: NavController? = null
    private val navController get() = _navController!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initView()
        
//        val i = 0
//        if (i == 0){
//            navController.popBackStack(R.id.home_navigation, true) //Xóa toàn bộ các fragment nằm trong Nested Graph home_navigation trong Back Stack.
//            navController.navigate(R.id.login_navigation)
//        }

    }

    private fun initView() {
        // Set up Nav Controller for BottomNavigationView
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragment_container_view) as NavHostFragment
        _navController = navHostFragment.navController
        binding.bottomNavigationView.setupWithNavController(navHostFragment.navController)
        // Handler event when change destination
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id){
                R.id.loginFragment -> binding.bottomNavigationView.visibility = View.GONE
                R.id.homeFragment -> binding.bottomNavigationView.visibility = View.VISIBLE
            }
        }
    }
}