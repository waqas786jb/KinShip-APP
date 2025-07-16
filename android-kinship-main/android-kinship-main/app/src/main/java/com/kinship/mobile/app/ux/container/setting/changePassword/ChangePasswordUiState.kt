package com.kinship.mobile.app.ux.container.setting.changePassword

import com.kinship.mobile.app.data.source.remote.helper.NetworkResult
import com.kinship.mobile.app.model.domain.response.ApiResponse
import com.kinship.mobile.app.model.domain.response.message.MessageResponse
import com.kinship.mobile.app.model.domain.response.signUp.UserAuthResponseData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class ChangePasswordUiState(
    val currentPasswordFlow: StateFlow<String> = MutableStateFlow(""),
    val onCurrentPasswordValueChange: (String) -> Unit = {},
    val confirmPasswordFlow: StateFlow<String> = MutableStateFlow(""),
    val onConfirmPasswordValueChange: (String) -> Unit = {},
    val newPasswordFlow: StateFlow<String> = MutableStateFlow(""),
    val onNewPasswordValueChange: (String) -> Unit = {},
    val newPasswordErrorFlow: StateFlow<String?> = MutableStateFlow(null),
    val currentPasswordErrorFlow: StateFlow<String?> = MutableStateFlow(null),
    val confirmPasswordErrorFlow: StateFlow<String?> = MutableStateFlow(null),
    val showLoader: StateFlow<Boolean> = MutableStateFlow(false),
    val isForgetPassword: StateFlow<String> = MutableStateFlow(""),
    //event
    val onClickUpdateButton: () -> Unit = {},
    val clearAllApiResultFlow: () -> Unit = {},
    val onApiSuccess: () -> Unit = {},
    val onRefreshTokenApiSuccess: (UserAuthResponseData) -> Unit = {},
    val onBackClick: () -> Unit = {},
    var forgetPasswordAPICall:()->Unit={},
    //Api response
    val apiResultFlow: StateFlow<NetworkResult<ApiResponse<MessageResponse>>?> = MutableStateFlow(null),
    val apiRefreshTokenResultFlow: StateFlow<NetworkResult<ApiResponse<UserAuthResponseData>>?> = MutableStateFlow(null)
)