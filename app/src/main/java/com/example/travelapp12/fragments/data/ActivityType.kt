package com.example.travelapp12.fragments.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ActivityType(var acttype: String? = null,
                        var id_activtype: String? = null) : Parcelable
