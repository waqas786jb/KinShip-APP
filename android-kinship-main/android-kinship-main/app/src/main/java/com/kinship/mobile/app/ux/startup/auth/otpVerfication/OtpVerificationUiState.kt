package com.kinship.mobile.app.ux.startup.auth.otpVerfication

import com.kinship.mobile.app.data.source.remote.helper.NetworkResult
import com.kinship.mobile.app.model.domain.response.ApiResponse
import com.kinship.mobile.app.model.domain.response.message.MessageResponse
import com.kinship.mobile.app.model.domain.response.signUp.UserAuthResponseData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

data class OtpVerificationUiState(
    var otpFlow: StateFlow<String> = MutableStateFlow(""),
    val otpErrorFlow: StateFlow<String?> = MutableStateFlow(null),
    val onOtpValueChanges: (String) -> Unit = {},
    val onOtpVerificationApiCall: () -> Unit = {},
    val onOtpFindingKinshipClick:(UserAuthResponseData?) -> Unit = {},
    val onResendOtpClick: () -> Unit = {},
    val onBackClick: () -> Unit = {},
    val startCountDown: () -> Unit = {},
    val apiResultFlow: StateFlow<NetworkResult<ApiResponse<UserAuthResponseData>>?> = MutableStateFlow(null),
    val apiResendOtpResultFlow: StateFlow<NetworkResult<ApiResponse<MessageResponse>>?> = MutableStateFlow(null),
    val clearAllApiResultFlow: () -> Unit = {},

)
