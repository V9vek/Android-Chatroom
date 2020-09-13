package com.project.googlemaps2020.utils

sealed class Resource<T>(
    val data: T? = null,
    val message: String? = null
) {
    class Success<T>(data: T?, message: String?) : Resource<T>(data, message)
    class Loading<T> : Resource<T>()
    class Error<T>(message: String?, data: T? = null) : Resource<T>(data, message)
}