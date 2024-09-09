package com.example.travelapp12.fragments.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize


@Parcelize
data class Sights(
    var name: String? = null,
    var address: String? = null,
    var id_activtype: String? = null,
    var activityType: ActivityType? = null,
    var image: String? = null,
    var image2: String? = null,
    var description: String? = null,
    var cost: String? = null,
    var worktime: String? = null,
    var id_sights: String? = null
) : Parcelable


