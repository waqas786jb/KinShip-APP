package com.kinship.mobile.app.ux.container.setting.deleteAccount

import com.kinship.mobile.app.data.source.remote.helper.NetworkResult
import com.kinship.mobile.app.model.domain.response.ApiResponse
import com.kinship.mobile.app.model.domain.response.message.MessageResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class DeleteAccountUiState (
    val reasonFlow: StateFlow<String> = MutableStateFlow(""),
    val onReasonValueChange: (String) -> Unit = {},
    //event
    val onClickOfConfirmButton: () -> Unit = {},
    val clearAllApiResultFlow: () -> Unit = {},
    val onApiSuccess: () -> Unit = {},
    val onBackClick: () -> Unit = {},
    //Api response
    val apiResultFlow: StateFlow<NetworkResult<ApiResponse<MessageResponse>>?> = MutableStateFlow(null)
    )