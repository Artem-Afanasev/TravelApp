package com.example.travelapp12.fragments.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Cart (
    var count: String? = null,
    var sights_id: String? = null,
    var cost: String? = null,
    var users_id: String? = null

) : Parcelable