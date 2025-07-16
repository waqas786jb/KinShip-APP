package com.kinship.mobile.app.ux.container.setting.updateContactDetails

import com.kinship.mobile.app.data.source.remote.helper.NetworkResult
import com.kinship.mobile.app.model.domain.response.ApiResponse
import com.kinship.mobile.app.model.domain.response.signUp.ProfileData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class UpdateContactUiState(
    val phoneNumberFlow: StateFlow<String> = MutableStateFlow(""),
    val countryCode: String? = null,
    val onPhoneNumberValueChange: (String) -> Unit = {},
    val emailFlow :StateFlow<String> = MutableStateFlow(""),
    val onEmailValueChange: (String) -> Unit = {},
    val emailErrorFlow:StateFlow<String?> = MutableStateFlow(null),
    val phoneNumberErrorFlow: StateFlow<String?> = MutableStateFlow(null),
    //event
    val onClickOfUpdateButton: () -> Unit = {},
    val clearAllApiResultFlow: () -> Unit = {},
    val onApiSuccess: (ProfileData) -> Unit = {},
    val onBackClick: () -> Unit = {},
    //Api response
    val apiResultFlow: StateFlow<NetworkResult<ApiResponse<ProfileData>>?> = MutableStateFlow(null),
)