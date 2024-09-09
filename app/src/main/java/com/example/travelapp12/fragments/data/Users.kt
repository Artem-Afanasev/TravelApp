package com.example.travelapp12.fragments.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Users(
    var user_id : String? = null,
    var login : String? = null,
    var password : String? = null,
    var emailAddress : String? = null,
) : Parcelable
