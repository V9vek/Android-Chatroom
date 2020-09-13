package com.project.googlemaps2020.ui.fragments

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.project.googlemaps2020.R
import com.project.googlemaps2020.models.User
import com.project.googlemaps2020.utils.Resource
import com.project.googlemaps2020.viewmodels.ChatroomViewModel
import kotlinx.android.synthetic.main.fragment_profile.*

class ProfileFragment : Fragment(R.layout.fragment_profile) {

    private val viewModel: ChatroomViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initUI()
        setupListeners()
        setupObservers()
    }

    private fun initUI() {
        viewModel.getCurrentlyLoggedInUser()
    }

    private fun setupListeners() {
        tvLogout.setOnClickListener {
            confirmLogout()
        }

        ivBack.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun confirmLogout() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Logout")
            .setMessage("Are you sure you want to Logout")
            .setPositiveButton("Logout") { dialog, _ ->
                viewModel.logout()
                findNavController().navigate(R.id.action_profileFragment_to_loginFragment)
                dialog.cancel()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.cancel()
            }
            .show()
    }

    private fun setupObservers() {
        viewModel.profileState.observe(viewLifecycleOwner, {
            when (it) {
                is Resource.Success -> {
                    val user = it.data
                    setUserDetails(user)
                }
                is Resource.Error -> {
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    private fun setUserDetails(user: User?) {
        Glide.with(requireContext()).load(user?.profile_image).into(ivUserImage)
        tvUserEmail.text = user?.email
        tvUsername.text = user?.username
    }
}