package com.kinship.mobile.app.data.source.remote.helper


sealed class NetworkResult<T>(val data: T? = null, val message: String? = null) {
    class Success<T>(data: T) : NetworkResult<T>(data)
    class Error<T>(message: String?, data: T? = null) : NetworkResult<T>(data, message)
    class Loading<T> : NetworkResult<T>()
    class UnAuthenticated<T>(message: String?) : NetworkResult<T>(message = message)
}