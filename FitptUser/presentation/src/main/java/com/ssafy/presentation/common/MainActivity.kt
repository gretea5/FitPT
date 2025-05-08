package com.ssafy.presentation.common

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.navigation.NavigationBarView
import com.ssafy.presentation.R
import com.ssafy.presentation.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint

private const val TAG = "MainActivity"
@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initNavigationBar()
        initEvent()
    }

    fun initNavigationBar() {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.main_container) as NavHostFragment
        val navController = navHostFragment.navController
        val bottomNavigationView = binding.bottomNavigation
        bottomNavigationView.setupWithNavController(navController)
        bottomNavigationView.labelVisibilityMode = NavigationBarView.LABEL_VISIBILITY_LABELED

        bottomNavigationView.setOnItemSelectedListener { item ->
            val destinationMap = mapOf(
                R.id.home to R.id.home_fragment,
                R.id.private_records to R.id.measure_list_fragment,
                R.id.pt_report to R.id.report_list_fragment,
                R.id.mypage to R.id.mypage_fragment
            )

            navController.currentDestination?.id?.let { currentDestinationId ->
                if (destinationMap[item.itemId] == currentDestinationId) {
                    return@setOnItemSelectedListener false
                }

                val navigateOptions = NavOptions.Builder()
                    .setLaunchSingleTop(true)
                    .setPopUpTo(currentDestinationId, true)
                    .build()

                when (item.itemId) {
                    R.id.home -> navController.navigate(R.id.home_fragment, null, navigateOptions)
                    R.id.private_records -> navController.navigate(
                        R.id.measure_list_fragment,
                        null,
                        navigateOptions
                    )
                    R.id.pt_report-> navController.navigate(
                        R.id.report_list_fragment,
                        null,
                        navigateOptions
                    )
                    R.id.mypage-> navController.navigate(
                        R.id.mypage_fragment,
                        null,
                        navigateOptions
                    )
                    else -> false
                }
            }
            true
        }
        hideBottomNavigationView(navController)
    }

    private fun hideBottomNavigationView(navController: NavController) {
        navController.addOnDestinationChangedListener { _, destination, _ ->
            val visibleFragments = setOf(
                R.id.home_fragment,
                R.id.measure_list_fragment,
                R.id.measure_fragment,
                R.id.report_list_fragment,
                R.id.mypage_fragment
            )
            binding.bottomNavigation.visibility = if (destination.id in visibleFragments) {
                View.VISIBLE
            } else {
                View.GONE
            }
            binding.fabCenter.visibility = if (destination.id in visibleFragments) {
                View.VISIBLE
            } else {
                View.GONE
            }
        }
    }

    fun initEvent(){
        binding.fabCenter.setOnClickListener {
            val navController = findNavController(R.id.main_container)

            val currentDestinationId = navController.currentDestination?.id
            val navigateOptions = NavOptions.Builder()
                .setLaunchSingleTop(true)  // 이미 스택에 있는 프래그먼트를 재사용
                .setPopUpTo(currentDestinationId ?: -1, true)  // 현재 destination을 팝하여 새로운 프래그먼트만 남기기
                .build()
            navController.navigate(R.id.measure_fragment, null, navigateOptions)
        }
    }
}