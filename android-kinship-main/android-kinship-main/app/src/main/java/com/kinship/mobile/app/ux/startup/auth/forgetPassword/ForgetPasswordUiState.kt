package com.kinship.mobile.app.ux.startup.auth.forgetPassword

import com.kinship.mobile.app.data.source.remote.helper.NetworkResult
import com.kinship.mobile.app.model.domain.response.ApiResponse
import com.kinship.mobile.app.model.domain.response.message.MessageResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

data class ForgetPasswordUiState (
    val emailFlow:StateFlow<String> = MutableStateFlow(""),
    val onEmailValueChange:(String) ->Unit={},
    val onForgetPasswordCLick:() -> Unit = {},
    val onBackClick: () -> Unit = {},
    val emailErrorFlow: StateFlow<String?> = MutableStateFlow(null),
    val clearAllApiResultFlow: () -> Unit = {},
    val apiForgetPasswordResultFlow: StateFlow<NetworkResult<ApiResponse<MessageResponse>>?> = MutableStateFlow(null),
)

