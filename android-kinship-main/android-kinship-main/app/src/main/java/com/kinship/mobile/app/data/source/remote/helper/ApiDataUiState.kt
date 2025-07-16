package com.kinship.mobile.app.data.source.remote.helper

sealed interface ApiDataUiState {
    data object Loading : ApiDataUiState
    data class Success(val data: Any) : ApiDataUiState
    data class Error(val errorMsg: String) : ApiDataUiState
    data class UnAuthenticated(val authMsg: String) : ApiDataUiState
}