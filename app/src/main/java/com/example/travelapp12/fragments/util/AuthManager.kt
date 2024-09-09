package com.example.travelapp12.fragments.util

object AuthManager {
    private var isLoggedIn = false

    fun isUserLoggedIn(): Boolean {
        return isLoggedIn
    }

    fun setUserLoggedIn(value: Boolean) {
        isLoggedIn = value
    }
}
