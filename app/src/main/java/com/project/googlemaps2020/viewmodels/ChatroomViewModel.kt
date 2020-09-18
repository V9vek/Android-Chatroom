package com.project.googlemaps2020.viewmodels

import android.annotation.SuppressLint
import android.net.Uri
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.toObject
import com.project.googlemaps2020.models.ChatMessage
import com.project.googlemaps2020.models.Chatroom
import com.project.googlemaps2020.models.User
import com.project.googlemaps2020.models.UserLocation
import com.project.googlemaps2020.repository.MainRepository
import com.project.googlemaps2020.utils.Resource
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class ChatroomViewModel
@ViewModelInject
constructor(
    private val repository: MainRepository
) : ViewModel() {

    val createChatroomState = MutableLiveData<Resource<Chatroom>>()
    val chatroomsState = MutableLiveData<Resource<List<Chatroom>>>()
    val newMessageState = MutableLiveData<Resource<Nothing>>()
    val chatMessagesState = MutableLiveData<Resource<List<ChatMessage>>>()
    val chatroomUsersState = MutableLiveData<Resource<List<User>>>()
    val userLocationsState = MutableLiveData<MutableList<UserLocation>>()
    val currentUserState = MutableLiveData<Resource<User>>()

    val chatroomImageUri = MutableLiveData<Uri>()
    val profileImageUri = MutableLiveData<String>()

    init {
        getCurrentUserProfileImage()
        getChatrooms()
    }

    fun getCurrentlyLoggedInUser() = viewModelScope.launch {
        try {
            val user = repository.getCurrentlyLoggedInUser()
            currentUserState.postValue(Resource.Success(user, ""))
        } catch (e: Exception) {
            currentUserState.postValue(Resource.Error(e.message))
        }
    }

    fun createChatroom(chatroomName: String) = viewModelScope.launch(IO) {
        createChatroomState.postValue(Resource.Loading())
        try {
            val createdChatroom = repository.createChatroom(chatroomName, chatroomImageUri.value)
            createChatroomState.postValue(
                Resource.Success(
                    createdChatroom,
                    "Chatroom created and Joining it"
                )
            )
        } catch (e: Exception) {
            createChatroomState.postValue(Resource.Error(e.message))
        }
        unsetChatroomImage()
    }

    fun setChatroomImage(uri: Uri?) {
        uri?.let {
            chatroomImageUri.value = it
        }
    }

    private fun getCurrentUserProfileImage() = viewModelScope.launch(IO) {
        try {
            val image = repository.getCurrentUserProfileImage()
            profileImageUri.postValue(image)
        } catch (e: Exception) {
        }
    }

    private fun getChatrooms() = viewModelScope.launch(IO) {
        chatroomsState.postValue(Resource.Loading())
        try {
            repository.getChatrooms()
                .addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                    firebaseFirestoreException?.let {
                        throw it
                    }

                    val chatroomsList: MutableList<Chatroom> = mutableListOf()
                    querySnapshot?.let {
                        for (document in it) {
                            val chatroom = document.toObject<Chatroom>()
                            chatroomsList.add(chatroom)
                        }
                        chatroomsState.postValue(Resource.Success(chatroomsList, ""))
                    }
                }
        } catch (e: Exception) {
            chatroomsState.postValue(Resource.Error(e.message))
        }
    }

    fun insertNewMessage(chatroomId: String, message: String) = viewModelScope.launch(IO) {
        try {
            repository.insertChatMessage(chatroomId, message)
            newMessageState.postValue(Resource.Success(null, "Message Sent"))
        } catch (e: Exception) {
            newMessageState.postValue(Resource.Error("Something went wrong"))
        }
    }

    fun getChatMessages(chatroomId: String) = viewModelScope.launch(IO) {
        chatMessagesState.postValue(Resource.Loading())
        try {
            repository.getChatMessages(chatroomId)
                .orderBy("timestamp", Query.Direction.ASCENDING)
                .addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                    firebaseFirestoreException?.let {
                        throw it
                    }

                    val chatMessageList: MutableList<ChatMessage> = mutableListOf()
                    querySnapshot?.let {
                        for (document in it) {
                            val chatMessage = document.toObject<ChatMessage>()
                            chatMessageList.add(chatMessage)
                        }
                        chatMessagesState.postValue(Resource.Success(chatMessageList, ""))
                    }
                }
        } catch (e: Exception) {
            chatMessagesState.postValue(Resource.Error(e.message))
        }
    }

    fun joinChatroom(chatroomId: String) = viewModelScope.launch {
        repository.joinChatroom(chatroomId)
    }

    fun leaveChatroom(chatroomId: String) = viewModelScope.launch {
        repository.leaveChatroom(chatroomId)
    }

    fun getChatroomUsers(chatroomId: String) = viewModelScope.launch {
        try {
            repository.getChatroomUsers(chatroomId)
                .addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                    firebaseFirestoreException?.let {
                        throw it
                    }

                    val users: MutableList<User> = mutableListOf()
                    querySnapshot?.let {
                        for (document in it) {
                            val user = document.toObject<User>()
                            users.add(user)
                        }
                    }
                    chatroomUsersState.postValue(Resource.Success(users, ""))
                }
        } catch (e: Exception) {
            chatroomUsersState.postValue(Resource.Error(e.message))
        }
    }

    fun getUserLocation(userList: List<User>) = viewModelScope.launch {
        try {
            val userLocations: MutableList<UserLocation> = mutableListOf()
            for (user in userList) {
                val location = repository.getUserLocation(user.user_id)
                userLocations.add(location)
            }
            userLocationsState.postValue(userLocations)
        } catch (e: Exception) {
        }
    }

    @SuppressLint("MissingPermission")
    fun getLastLocation(fusedLocationProviderClient: FusedLocationProviderClient) =
        viewModelScope.launch {
            val location = fusedLocationProviderClient.lastLocation.await()
            val geoPoint = GeoPoint(location.latitude, location.longitude)

            repository.saveUserLocation(geoPoint)
        }

    fun logout() {
        repository.logout()
    }

    fun unsetChatroomImage() {
        chatroomImageUri.postValue(null)
    }

    fun unsetChatroomState() {
        createChatroomState.postValue(null)
    }

    fun unsetNewMessageState() {
        newMessageState.postValue(null)
    }

    fun unsetChatMessagesState() {
        chatMessagesState.postValue(null)
    }

    fun unsetChatroomUsers() {
        chatroomUsersState.postValue(null)
    }
}















