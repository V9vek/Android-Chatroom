package com.project.googlemaps2020.models

import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.clustering.ClusterItem

data class ClusterMarker(
    val latLng: LatLng,
    val customTitle: String,
    val customSnippet: String,
    val user: User
) : ClusterItem {

    override fun getPosition() = latLng

    override fun getTitle() = customTitle

    override fun getSnippet() = customSnippet
}