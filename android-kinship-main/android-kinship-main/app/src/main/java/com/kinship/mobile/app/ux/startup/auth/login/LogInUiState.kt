package com.kinship.mobile.app.ux.startup.auth.login

import com.kinship.mobile.app.data.source.remote.helper.NetworkResult
import com.kinship.mobile.app.model.domain.response.ApiResponse
import com.kinship.mobile.app.model.domain.response.signUp.UserAuthResponseData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

data class LogInUiState(
    //data
    val emailFlow: StateFlow<String> = MutableStateFlow(""),
    val onEmailValueChange: (String) -> Unit = {},
    val passwordFlow: StateFlow<String> = MutableStateFlow(""),
    val onPasswordValueChange: (String) -> Unit = {},
    val emailErrorFlow: StateFlow<String?> = MutableStateFlow(null),
    val passwordErrorFlow: StateFlow<String?> = MutableStateFlow(null),

    //click
    val onLoginClick: () -> Unit = {},
    val onForgetPasswordClick: () -> Unit = {},
    val onRegisterClick: () -> Unit = {},
    val onLoginInSuccessfully: (UserAuthResponseData?) -> Unit = {},
    val clearAllApiResultFlow: () -> Unit = {},

    //api response
    val apiResultFlow: StateFlow<NetworkResult<ApiResponse<UserAuthResponseData>>?> = MutableStateFlow(null),
)