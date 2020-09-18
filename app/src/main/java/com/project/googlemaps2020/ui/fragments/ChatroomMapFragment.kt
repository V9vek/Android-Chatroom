package com.project.googlemaps2020.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.firebase.auth.FirebaseAuth
import com.google.maps.android.clustering.ClusterManager
import com.project.googlemaps2020.R
import com.project.googlemaps2020.models.ClusterMarker
import com.project.googlemaps2020.models.UserLocation
import com.project.googlemaps2020.utils.ClusterManagerRenderer
import com.project.googlemaps2020.viewmodels.ChatroomViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_chatroom_map.*
import javax.inject.Inject

@AndroidEntryPoint
class ChatroomMapFragment : Fragment(R.layout.fragment_chatroom_map) {

    @Inject
    lateinit var auth: FirebaseAuth
    private val viewModel: ChatroomViewModel by activityViewModels()

    private var map: GoogleMap? = null

    private var userLocations = mutableListOf<UserLocation>()
    private var currentUserLocation: UserLocation? = null


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mapView.onCreate(savedInstanceState)

        mapView.getMapAsync {
            map = it
            setupObservers()
            addMapMarkers(map)
        }

        setupListeners()
    }

    private fun addMapMarkers(map: GoogleMap?) {
        val clusterManager = ClusterManager<ClusterMarker>(requireContext(), map)
        clusterManager.renderer = ClusterManagerRenderer(requireContext(), map, clusterManager)

        for (userLocation in userLocations) {
            val snippet = if (userLocation.user?.user_id == auth.uid) {
                "This is you"
            } else {
                "Determine route to ${userLocation.user?.username} ?"
            }

            val clusterMarker = ClusterMarker(
                LatLng(userLocation.geo_point?.latitude!!, userLocation.geo_point?.longitude!!),
                userLocation.user?.username!!,
                snippet,
                userLocation.user
            )
            clusterManager.addItem(clusterMarker)
        }
        clusterManager.cluster()
    }

    private fun setupListeners() {
        ivBack.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun setupObservers() {
        viewModel.userLocationsState.observe(viewLifecycleOwner, {
            userLocations = it
            getCurrentUserLocation()
            moveCameraToUser()
        })
    }

    private fun getCurrentUserLocation() {
        println("AppDebug: $userLocations")
        for (userLocation in userLocations) {
            if (userLocation.user?.user_id == auth.uid) {
                currentUserLocation = userLocation
                break
            }
        }
    }

    private fun moveCameraToUser() {
        val bottomBoundary = currentUserLocation?.geo_point?.latitude?.minus(.1)
        val leftBoundary = currentUserLocation?.geo_point?.longitude?.minus(.1)
        val topBoundary = currentUserLocation?.geo_point?.latitude?.plus(.1)
        val rightBoundary = currentUserLocation?.geo_point?.longitude?.plus(.1)
        val bounds = LatLngBounds(
            LatLng(bottomBoundary!!, leftBoundary!!),
            LatLng(topBoundary!!, rightBoundary!!)
        )
        println("AppDebug: $bounds, ${map.toString()}")
        map?.moveCamera(
            CameraUpdateFactory.newLatLngBounds(bounds, 0)
        )
    }

    override fun onResume() {
        super.onResume()
        mapView?.onResume()
    }

    override fun onStart() {
        super.onStart()
        mapView?.onStart()
    }

    override fun onStop() {
        super.onStop()
        mapView?.onStop()
    }

    override fun onPause() {
        super.onPause()
        mapView?.onPause()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView?.onLowMemory()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView?.onDestroy()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapView.onSaveInstanceState(outState)
    }
}