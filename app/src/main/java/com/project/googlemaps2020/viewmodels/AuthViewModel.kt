package com.project.googlemaps2020.viewmodels

import android.net.Uri
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.googlemaps2020.models.User
import com.project.googlemaps2020.repository.MainRepository
import com.project.googlemaps2020.utils.Resource
import kotlinx.coroutines.launch

class AuthViewModel
@ViewModelInject
constructor(
    private val repository: MainRepository
) : ViewModel() {

    val registerState = MutableLiveData<Resource<Nothing>>()
    val loginState = MutableLiveData<Resource<Nothing>>()

    var profileImageUri = MutableLiveData<Uri>()

    fun registerUser(
        username: String,
        email: String,
        password: String,
        confirmPassword: String
    ) = viewModelScope.launch {

        registerState.postValue(Resource.Loading())

        if (username.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty()
            && confirmPassword.isNotEmpty() && profileImageUri.value != null
        ) {
            if (password == confirmPassword) {
                try {
                    repository.registerUser(email, password)
                    uploadImageToFirebaseStorage(username, email)
                } catch (e: Exception) {
                    registerState.postValue(Resource.Error(e.message))
                }
            } else {
                registerState.postValue(Resource.Error("Password and Confirm Password do not match"))
            }
        } else {
            registerState.postValue(Resource.Error("Please Fill The Details or Select Photo"))
        }
    }


    private fun uploadImageToFirebaseStorage(username: String, email: String) =
        viewModelScope.launch {
            try {
                profileImageUri.value?.let {
                    val uploadedUri = repository.uploadProfileImage(it)
                    saveUserToFirestoreDatabase(username, email, uploadedUri.toString())
                }
            } catch (e: Exception) {

            }
        }

    private fun saveUserToFirestoreDatabase(username: String, email: String, uploadedUri: String) =
        viewModelScope.launch {
            val uid = repository.getUid()!!
            val user = User(username, email, uid, uploadedUri)

            try {
                repository.saveUserToFirestore(user)
                registerState.postValue(Resource.Success(null, "Successfully Registered"))
            } catch (e: Exception) {
                registerState.postValue(Resource.Error(e.message))
            }
        }


    fun loginUser(email: String, password: String) = viewModelScope.launch {

        loginState.postValue(Resource.Loading())
        if (email.isNotEmpty() && password.isNotEmpty()) {
            try {
                repository.loginUser(email, password)
                loginState.postValue(Resource.Success(null, "Successfully Logged In"))
            } catch (e: Exception) {
                loginState.postValue(Resource.Error(e.message))
            }
        } else {
            loginState.postValue(Resource.Error("Please Fill The Details"))
        }
    }


    fun setProfileImage(uri: Uri) {
        profileImageUri.value = uri
    }
}
















