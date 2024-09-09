package com.example.travelapp12.fragments

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.travelapp12.R
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.mainHostFragment) as NavHostFragment
        navController = navHostFragment.navController

        val bottomNavView = findViewById<BottomNavigationView>(R.id.bottomNavigation)
        NavigationUI.setupWithNavController(bottomNavView, navController)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.profileFragment -> {
                    checkAuthAndNavigateProfile()
                }
                R.id.cartFragment -> {
                    checkAuthAndNavigateCart()
                }
            }
        }
    }

    private fun checkAuthAndNavigateProfile() {
        val sharedPreferences = getSharedPreferences("user_info", Context.MODE_PRIVATE)
        val userLogin = sharedPreferences.getString("user_login", null)

        if (userLogin != null) {
            // Открываем фрагмент профиля
        } else {
            // Если пользователь не авторизован, перенаправляем на фрагмент авторизации
            navController.navigate(R.id.loginFragment)
        }
    }
    private fun checkAuthAndNavigateCart() {
        val sharedPreferences = getSharedPreferences("user_info", Context.MODE_PRIVATE)
        val userLogin = sharedPreferences.getString("user_login", null)

        if (userLogin != null) {
        } else {
            navController.navigate(R.id.loggedOutCartFragment)
        }
    }

    override fun onResume() {
        super.onResume()
        checkAuthAndNavigateProfile()
    }
}
















