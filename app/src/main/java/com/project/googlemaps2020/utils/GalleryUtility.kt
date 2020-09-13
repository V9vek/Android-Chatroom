package com.project.googlemaps2020.utils

import android.Manifest
import android.content.Context
import pub.devrel.easypermissions.EasyPermissions

object GalleryUtility {

    fun hasStoragePermission(context: Context) =
        EasyPermissions.hasPermissions(
            context, Manifest.permission.READ_EXTERNAL_STORAGE
        )
}