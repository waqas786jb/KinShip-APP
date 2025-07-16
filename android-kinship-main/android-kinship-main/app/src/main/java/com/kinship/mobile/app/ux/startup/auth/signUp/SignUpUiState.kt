package com.kinship.mobile.app.ux.startup.auth.signUp

import com.kinship.mobile.app.data.source.remote.helper.NetworkResult
import com.kinship.mobile.app.model.domain.response.ApiResponse
import com.kinship.mobile.app.model.domain.response.signUp.UserAuthResponseData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

data class SignUpUiState(
    //Data
    val emailFlow: StateFlow<String> = MutableStateFlow(""),
    val onEmailValueChange: (String) -> Unit = {},
    val passwordFlow: StateFlow<String> = MutableStateFlow(""),
    val onPasswordValueChange: (String) -> Unit = {},
    val confirmPasswordFlow: StateFlow<String> = MutableStateFlow(""),
    val onConfirmPasswordValueChange: (String) -> Unit = {},
    val emailErrorFlow: StateFlow<String?> = MutableStateFlow(null),
    val passwordErrorFlow: StateFlow<String?> = MutableStateFlow(null),
    val confirmPasswordErrorFlow: StateFlow<String?> = MutableStateFlow(null),
    //Api response
    val apiResultFlow: StateFlow<NetworkResult<ApiResponse<UserAuthResponseData>>?> = MutableStateFlow(null),
    //Events
    val onBackClick: () -> Unit = {},
    val onOtpVerificationClick: () -> Unit = {},
    val onSignInClick: () -> Unit = {},
    val onSignInSuccessfully: (UserAuthResponseData?) -> Unit = {},
    val clearAllApiResultFlow: () -> Unit = {},
    val termsAndConditionErrorFlow: StateFlow<String?> = MutableStateFlow(null),
    val onTermsAndConditionErrorChecked: (Boolean) -> Unit = {},
    val termAndConditionClick:()->Unit={},
    val showLoader: StateFlow<Boolean> = MutableStateFlow(false),

)
