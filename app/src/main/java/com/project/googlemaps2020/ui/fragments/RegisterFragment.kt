package com.project.googlemaps2020.ui.fragments

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.project.googlemaps2020.R
import com.project.googlemaps2020.databinding.FragmentRegisterBinding
import com.project.googlemaps2020.utils.Constants.IMAGE_PICK_CODE
import com.project.googlemaps2020.utils.Constants.REQUEST_CODE_STORAGE_PERMISSION
import com.project.googlemaps2020.utils.GalleryUtility
import com.project.googlemaps2020.utils.Resource
import com.project.googlemaps2020.viewmodels.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions

@AndroidEntryPoint
class RegisterFragment : Fragment(R.layout.fragment_register), EasyPermissions.PermissionCallbacks {

    private val viewModel: AuthViewModel by viewModels()

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentRegisterBinding.bind(view)

        setupOnClickListeners()
        setupObservers()
    }

    private fun setupObservers() {
        viewModel.profileImageUri.observe(viewLifecycleOwner, {
            Glide.with(requireContext()).load(it).into(binding.ivSignupProfile)
            binding.btnSignupProfile.alpha = 0f
        })

        viewModel.registerState.observe(viewLifecycleOwner, {
            when (it) {
                is Resource.Success -> {
                    progressBar(false)
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                    findNavController().navigate(
                        RegisterFragmentDirections.actionRegisterFragmentToChatsFragment()
                    )
                }

                is Resource.Error -> {
                    progressBar(false)
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                }

                is Resource.Loading -> {
                    progressBar(true)
                }
            }
        })
    }

    private fun progressBar(visible: Boolean) {
        binding.apply {
            signupProgressBar.isVisible = visible
            btnSignup.isVisible = !visible
        }
    }

    private fun setupOnClickListeners() {
        binding.apply {
            btnSignupProfile.setOnClickListener {
                requestPermissions()
            }

            btnSignup.setOnClickListener {
                val username = etSignupUsername.text.toString()
                val email = etSignupEmail.text.toString()
                val password = etSignupPassword.text.toString()
                val confirmPassword = etSignupConfirmPassword.text.toString()
                viewModel.registerUser(username, email, password, confirmPassword)
            }

            tvLogin.setOnClickListener {
                findNavController().navigate(
                    RegisterFragmentDirections.actionRegisterFragmentToLoginFragment()
                )
            }
        }
    }

    private fun requestPermissions() {
        if (GalleryUtility.hasStoragePermission(requireContext())) {
            pickImageFromGallery()
        } else {
            EasyPermissions.requestPermissions(
                this,
                "You need to accept storage permission",
                REQUEST_CODE_STORAGE_PERMISSION,
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
        }
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
        pickImageFromGallery()
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            AppSettingsDialog.Builder(this).build().show()
        } else {
            requestPermissions()
        }
    }

    private fun pickImageFromGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, IMAGE_PICK_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == IMAGE_PICK_CODE) {
            data?.data.let { uri ->
                uri?.let {
                    viewModel.setProfileImage(it)
                }
            }
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}