package com.example.travelapp12.fragments.util

import android.view.View
import androidx.fragment.app.Fragment
import com.example.travelapp12.R
import com.example.travelapp12.fragments.MainActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

fun Fragment.hideBottomNavigation(){
    val bottomNavigationView=(activity as MainActivity).findViewById<BottomNavigationView>(
        R.id.bottomNavigation
    )
    bottomNavigationView.visibility = View.GONE
}

fun Fragment.showBottomNavigation(){
    val bottomNavigationView=(activity as MainActivity).findViewById<BottomNavigationView>(
        R.id.bottomNavigation
    )
    bottomNavigationView.visibility = View.VISIBLE
}