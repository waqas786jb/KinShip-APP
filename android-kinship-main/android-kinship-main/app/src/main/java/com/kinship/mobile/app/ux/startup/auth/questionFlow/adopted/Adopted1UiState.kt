package com.kinship.mobile.app.ux.startup.auth.questionFlow.adopted
import com.kinship.mobile.app.data.source.remote.helper.NetworkResult
import com.kinship.mobile.app.model.domain.response.ApiResponse
import com.kinship.mobile.app.model.domain.response.signUp.UserAuthResponseData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

data class Adopted1UiState(
    val onUserDetailsAPICall: () -> Unit = {},
    val onUserDetailsAPICLick: () -> Unit = {},
    val onBackClick: () -> Unit = {},
    val dobErrorFlow: StateFlow<String?> = MutableStateFlow(null),
    val onSelectedDate: (String) -> Unit = {},
    var onGirlValue: () -> Unit = {},
    var onBoyValue: () -> Unit = {},
    val onSingleGenderList: (String) -> Unit = {},
    val onChildYesClick: () -> Unit = {},
    val onChildNoClick: () -> Unit = {},
    val apiResultFlow: StateFlow<NetworkResult<ApiResponse<UserAuthResponseData>>?> = MutableStateFlow(null),
    val clearAllApiResultFlow: () -> Unit = {}
    )
