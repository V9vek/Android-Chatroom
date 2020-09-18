package com.project.googlemaps2020.utils

import android.content.Context
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.MarkerOptions
import com.google.maps.android.clustering.Cluster
import com.google.maps.android.clustering.ClusterManager
import com.google.maps.android.clustering.view.DefaultClusterRenderer
import com.google.maps.android.ui.IconGenerator
import com.project.googlemaps2020.R
import com.project.googlemaps2020.models.ClusterMarker
import de.hdodenhof.circleimageview.CircleImageView

class ClusterManagerRenderer(
    private val context: Context,
    map: GoogleMap?,
    clusterManager: ClusterManager<ClusterMarker>
) : DefaultClusterRenderer<ClusterMarker>(context, map, clusterManager) {

    private val iconGenerator = IconGenerator(context)
    private val imageView = CircleImageView(context)
    private val markerWidth = context.resources.getDimension(R.dimen.custom_marker_image).toInt()
    private val markerHeight = context.resources.getDimension(R.dimen.custom_marker_image).toInt()
    private val padding = context.resources.getDimension(R.dimen.custom_marker_padding).toInt()


    override fun onBeforeClusterItemRendered(item: ClusterMarker, markerOptions: MarkerOptions) {
        imageView.layoutParams = ViewGroup.LayoutParams(markerWidth, markerHeight)
        imageView.setPadding(padding, padding, padding, padding)
        iconGenerator.setContentView(imageView)

        // imageView.setImageResource(R.drawable.ic_launcher_background)

        Glide.with(context).asBitmap().load(item.user.profile_image)
            .into(imageView)
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









