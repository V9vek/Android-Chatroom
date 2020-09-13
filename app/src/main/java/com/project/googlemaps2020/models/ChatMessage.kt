package com.project.googlemaps2020.models

data class ChatMessage(
    val user: User? = null,
    val message: String = "",
    val message_id: String = "",
    val timestamp: Long = 0L
)