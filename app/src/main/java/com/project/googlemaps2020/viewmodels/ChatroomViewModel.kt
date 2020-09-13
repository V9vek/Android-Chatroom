package com.project.googlemaps2020.viewmodels

import android.net.Uri
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.toObject
import com.project.googlemaps2020.models.ChatMessage
import com.project.googlemaps2020.models.Chatroom
import com.project.googlemaps2020.models.User
import com.project.googlemaps2020.repository.MainRepository
import com.project.googlemaps2020.utils.Resource
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch

class ChatroomViewModel
@ViewModelInject
constructor(
    private val repository: MainRepository
) : ViewModel() {

    var createChatroomState = MutableLiveData<Resource<Chatroom>>()
    var chatroomsState = MutableLiveData<Resource<List<Chatroom>>>()
    val newMessageState = MutableLiveData<Resource<Nothing>>()
    val chatMessagesState = MutableLiveData<Resource<List<ChatMessage>>>()
    val chatroomUsersState = MutableLiveData<Resource<List<User>>>()
    val profileState = MutableLiveData<Resource<User>>()

    var chatroomImageUri = MutableLiveData<Uri>()
    var profileImageUri = MutableLiveData<String>()

    init {
        getCurrentUserProfileImage()
        getChatrooms()
    }

    fun getCurrentlyLoggedInUser() = viewModelScope.launch {
        try {
            val user = repository.getCurrentlyLoggedInUser()
            profileState.postValue(Resource.Success(user, ""))
        } catch (e: Exception) {
            profileState.postValue(Resource.Error(e.message))
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
                            val chatMessage = document.toObject<User>()
                            users.add(chatMessage)
                        }
                    }
                    chatroomUsersState.postValue(Resource.Success(users, ""))
                }
        } catch (e: Exception) {
            chatroomUsersState.postValue(Resource.Error(e.message))
        }
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
}















