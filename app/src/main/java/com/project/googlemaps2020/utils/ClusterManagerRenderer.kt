package com.project.googlemaps2020.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.view.ViewGroup
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.maps.android.clustering.Cluster
import com.google.maps.android.clustering.ClusterManager
import com.google.maps.android.clustering.view.DefaultClusterRenderer
import com.google.maps.android.ui.IconGenerator
import com.project.googlemaps2020.R
import com.project.googlemaps2020.models.ClusterMarker
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

class ClusterManagerRenderer(
    context: Context,
    map: GoogleMap?,
    clusterManager: ClusterManager<ClusterMarker>
) : DefaultClusterRenderer<ClusterMarker>(context, map, clusterManager) {

    private val iconGenerator = IconGenerator(context)
    private val imageView = CircleImageView(context)
    private val markerWidth = context.resources.getDimension(R.dimen.custom_marker_image).toInt()
    private val markerHeight = context.resources.getDimension(R.dimen.custom_marker_image).toInt()
    private val padding = context.resources.getDimension(R.dimen.custom_marker_padding).toInt()


    override fun onBeforeClusterItemRendered(item: ClusterMarker, markerOptions: MarkerOptions) {
        Picasso
            .get()
            .load(item.user.profile_image)
            .into(object : Target {
                override fun onBitmapLoaded(bitmap: Bitmap?, from: Picasso.LoadedFrom?) {
                    println("AppDebug: onBitmapLoaded $bitmap")
                    imageView.setImageBitmap(bitmap)
                    imageView.layoutParams = ViewGroup.LayoutParams(markerWidth, markerHeight)
                    imageView.setPadding(padding, padding, padding, padding)
                    iconGenerator.setContentView(imageView)
                }

                override fun onBitmapFailed(e: Exception?, errorDrawable: Drawable?) {
                    println("AppDebug: onBitmapFailed $e")
                }

                override fun onPrepareLoad(placeHolderDrawable: Drawable?) {
                    println("AppDebug: onPrepareLoad")
                }
            })

        println("AppDebug: ")

        // imageView.setImageResource(R.drawable.ic_launcher_background)

//        Glide.with(context).asBitmap().load(item.user.profile_image)
//            .into(imageView)
//        val icon = iconGenerator.makeIcon()
//
//        markerOptions
//            .icon(BitmapDescriptorFactory.fromBitmap(icon))
//            .title(item.customTitle)
//            .snippet(item.customSnippet)

        val icon = iconGenerator.makeIcon()
        markerOptions
            .icon(BitmapDescriptorFactory.fromBitmap(icon))
            .title(item.customTitle)
            .snippet(item.customSnippet)
    }

    override fun shouldRenderAsCluster(cluster: Cluster<ClusterMarker>?): Boolean {
        return false
    }
}









