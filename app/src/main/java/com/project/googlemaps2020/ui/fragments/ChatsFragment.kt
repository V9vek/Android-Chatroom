package com.project.googlemaps2020.ui.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.android.gms.location.FusedLocationProviderClient
import com.project.googlemaps2020.R
import com.project.googlemaps2020.adapters.ChatroomsAdapter
import com.project.googlemaps2020.databinding.FragmentChatsBinding
import com.project.googlemaps2020.models.Chatroom
import com.project.googlemaps2020.utils.Constants
import com.project.googlemaps2020.utils.Resource
import com.project.googlemaps2020.utils.TrackingUtility
import com.project.googlemaps2020.viewmodels.ChatroomViewModel
import dagger.hilt.android.AndroidEntryPoint
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions
import javax.inject.Inject

private const val CREATE_CHATROOM_DIALOG_TAG = "CreateChatroom"

@AndroidEntryPoint
class ChatsFragment : Fragment(R.layout.fragment_chats), EasyPermissions.PermissionCallbacks {

    private val viewModel: ChatroomViewModel by activityViewModels()

    private var _binding: FragmentChatsBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    private lateinit var chatroomsAdapter: ChatroomsAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentChatsBinding.bind(view)

        // On rotation resetting createListener of dialog, which is called when create btn is clicked
        if (savedInstanceState != null) {
            val createChatroomDialog = parentFragmentManager.findFragmentByTag(
                CREATE_CHATROOM_DIALOG_TAG
            ) as CreateChatroomDialog?

            viewModel.chatroomImageUri.observe(viewLifecycleOwner, { uri ->
                uri?.let {
                    createChatroomDialog?.setChatroomImage(it)
                }
            })
        }

        requestPermissions()
        setupOnClickListeners()
        setupObservers()
        setUpRecyclerView()
    }

    private fun requestPermissions() {
        if (TrackingUtility.hasLocationPermission(requireContext())) {
            return
        }

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            EasyPermissions.requestPermissions(
                this,
                "You need to accept location permissions",
                Constants.REQUEST_CODE_LOCATION_PERMISSION,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        } else {
            EasyPermissions.requestPermissions(
                this,
                "You need to accept location permissions",
                Constants.REQUEST_CODE_LOCATION_PERMISSION,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
            )
        }
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
        getLastLocation()
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            AppSettingsDialog.Builder(this).build().show()
        } else {
            requestPermissions()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    @SuppressLint("MissingPermission")
    private fun getLastLocation() {
        if (TrackingUtility.hasLocationPermission(requireContext())) {
            println("AppDebug: getLastLocation")
            viewModel.getLastLocation(fusedLocationProviderClient)
        }
    }

    private fun setupOnClickListeners() {
        binding.apply {
            layoutNewChatroom.setOnClickListener {
                showCreateChatroomDialog()
            }

            ivProfileImage.setOnClickListener {
                findNavController().navigate(R.id.action_chatsFragment_to_profileFragment)
            }
        }
    }

    private fun showCreateChatroomDialog() {
        CreateChatroomDialog().show(parentFragmentManager, CREATE_CHATROOM_DIALOG_TAG)
    }

    private fun setupObservers() {
        viewModel.profileImageUri.observe(viewLifecycleOwner, {
            Glide.with(requireContext()).load(it).placeholder(binding.ivProfileImage.drawable)
                .into(binding.ivProfileImage)
        })

        viewModel.createChatroomState.observe(viewLifecycleOwner, {
            when (it) {
                is Resource.Success -> {
                    progressBarTillCreate(false)
                    val chatroom = it.data
                    chatroom?.let { room ->
                        navigateToChatroomFragment(room)
                    }
                }

                is Resource.Error -> {
                    progressBarTillCreate(false)
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                }

                is Resource.Loading -> {
                    progressBarTillCreate(true)
                }
            }

            viewModel.unsetChatroomState()              // chatroom state do not get observed again ,so chatroom don't get created again
        })

        viewModel.chatroomsState.observe(viewLifecycleOwner, {
            when (it) {
                is Resource.Success -> {
                    progressBarTillFetch(false)
                    it.data?.let { chatroomsList ->
                        chatroomsAdapter.submitList(chatroomsList)
                    }
                }

                is Resource.Error -> {
                    progressBarTillFetch(false)
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                }

                is Resource.Loading -> {
                    progressBarTillFetch(true)
                }
            }
        })
    }

    private fun setUpRecyclerView() {
        binding.rvChatrooms.apply {
            chatroomsAdapter = ChatroomsAdapter()
            adapter = chatroomsAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
        chatroomsAdapter.setOnChatroomItemClickListener {
            navigateToChatroomFragment(it)
        }
    }

    private fun navigateToChatroomFragment(chatroom: Chatroom) {
        findNavController().navigate(
            ChatsFragmentDirections.actionChatsFragmentToChatroomFragment(chatroom)
        )
    }


    private fun progressBarTillCreate(visible: Boolean) {
        binding.apply {
            progressBarEntering.isVisible = visible
            tvEntering.isVisible = visible
        }
    }

    private fun progressBarTillFetch(visible: Boolean) {
        binding.progressBarFetching.isVisible = visible
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}