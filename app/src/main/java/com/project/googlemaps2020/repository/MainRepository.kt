package com.project.googlemaps2020.repository

import android.net.Uri
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.storage.StorageReference
import com.project.googlemaps2020.models.ChatMessage
import com.project.googlemaps2020.models.Chatroom
import com.project.googlemaps2020.models.User
import kotlinx.coroutines.tasks.await

class MainRepository(
    private val auth: FirebaseAuth,
    private val storageRef: StorageReference,
    private val firestoreRef: FirebaseFirestore
) {

    fun getUid() = auth.uid

    suspend fun getCurrentlyLoggedInUser(): User {
        val uid = getUid()
        val currentUserRef = uid?.let { firestoreRef.collection("Users").document(it) }
        val currentUserSnapshot = currentUserRef?.get()?.await()
        val currentUser = currentUserSnapshot?.toObject<User>()
        return currentUser!!
    }

    suspend fun registerUser(email: String, password: String): AuthResult =
        auth.createUserWithEmailAndPassword(email, password).await()

    suspend fun loginUser(email: String, password: String): AuthResult =
        auth.signInWithEmailAndPassword(email, password).await()

    fun logout() {
        auth.signOut()
    }

    suspend fun uploadProfileImage(photoUri: Uri): Uri {
        val filename = getUid()!!
        val ref = storageRef.child("ProfileImage/$filename")
        ref.putFile(photoUri).await()

        return ref.downloadUrl.await()
    }

    suspend fun saveUserToFirestore(user: User) {
        val newUserRef = firestoreRef.collection("Users").document(getUid()!!)
        newUserRef.set(user).await()
    }

    suspend fun createChatroom(name: String, uri: Uri?): Chatroom {
        val newChatroomRef = firestoreRef.collection("Chatrooms").document()
        val chatroom = createChatroomObject(newChatroomRef.id, name, uri)
        newChatroomRef.set(chatroom).await()
        return chatroom
    }

    private suspend fun createChatroomObject(
        documentId: String,
        name: String,
        uri: Uri?
    ): Chatroom {
        val uploadedUri = uri?.let {
            uploadChatroomImage(it, documentId)
        }
        return Chatroom(documentId, name, uploadedUri.toString())
    }

    private suspend fun uploadChatroomImage(uri: Uri, documentId: String): Uri {
        val ref = storageRef.child("ChatroomImage/$documentId")
        ref.putFile(uri).await()
        return ref.downloadUrl.await()
    }

    suspend fun getCurrentUserProfileImage(): String {
        return storageRef.child("ProfileImage/${getUid()}").downloadUrl.await().toString()
    }

    fun getChatrooms(): CollectionReference {
        return firestoreRef.collection("Chatrooms")
    }

    suspend fun insertChatMessage(chatroomId: String, message: String) {

        // Chatrooms -> chatroom id -> message_collection -> message_document
        val newMessageDocumentRef = firestoreRef.collection("Chatrooms")
            .document(chatroomId).collection("Chat Messages").document()

        val user = getCurrentlyLoggedInUser()
        val chatMessage =
            ChatMessage(user, message, newMessageDocumentRef.id, System.currentTimeMillis())

        newMessageDocumentRef.set(chatMessage).await()
    }

    fun getChatMessages(chatroomId: String): CollectionReference {
        return firestoreRef.collection("Chatrooms")
            .document(chatroomId)
            .collection("Chat Messages")
    }

    suspend fun joinChatroom(chatroomId: String) {
        val joinChatroomRef = firestoreRef.collection("Chatrooms")
            .document(chatroomId).collection("User List").document(getUid()!!)

        val user = getCurrentlyLoggedInUser()
        joinChatroomRef.set(user)
    }

    suspend fun leaveChatroom(chatroomId: String) {
        val joinChatroomRef = firestoreRef.collection("Chatrooms")
            .document(chatroomId).collection("User List").document(getUid()!!)

        joinChatroomRef.delete().await()
    }

    fun getChatroomUsers(chatroomId: String): CollectionReference {
        return firestoreRef.collection("Chatrooms")
            .document(chatroomId).collection("User List")
    }
}















