package com.project.googlemaps2020.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Chatroom(
    val chatroom_id: String = "",
    val title: String = "",
    val image: String = ""
) : Parcelable