package com.app.tripmanagementapp.utility

sealed class Result<T>(
    val data: T? = null,
    val message: String? = null,
    val error: Exception? = null
) {

    class Success<T>(data: T) : Result<T>(data)

    class Error<T>(error: Exception) : Result<T>(error = error)

    class NoInternet<T>(message: String?, data: T? = null) : Result<T>(data, message)

    class Loading<T> : Result<T>()

}