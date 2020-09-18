package com.project.googlemaps2020.utils

import android.Manifest
import android.content.Context
import android.os.Build
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.firestore.GeoPoint
import pub.devrel.easypermissions.EasyPermissions

object TrackingUtility {

    fun hasLocationPermission(context: Context) =
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            EasyPermissions.hasPermissions(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        } else {
            EasyPermissions.hasPermissions(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
            )
        }

    fun getLatLng(geoPoint: GeoPoint?): LatLng {
        val lat = geoPoint?.latitude!!
        val lng = geoPoint.longitude
        return LatLng(lat, lng)
    }
}








