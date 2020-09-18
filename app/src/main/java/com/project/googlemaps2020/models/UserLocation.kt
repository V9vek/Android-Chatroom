package com.project.googlemaps2020.models

import com.google.firebase.firestore.GeoPoint

data class UserLocation(
    var geo_point: GeoPoint? = null,
    val timestamp: Long = 0L,
    val user: User? = null
)