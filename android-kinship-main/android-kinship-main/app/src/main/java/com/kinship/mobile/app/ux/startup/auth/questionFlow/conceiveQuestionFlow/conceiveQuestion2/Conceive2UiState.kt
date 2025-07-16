package com.kinship.mobile.app.ux.startup.auth.questionFlow.conceiveQuestionFlow.conceiveQuestion2
import com.kinship.mobile.app.data.source.remote.helper.NetworkResult
import com.kinship.mobile.app.model.domain.response.ApiResponse
import com.kinship.mobile.app.model.domain.response.signUp.UserAuthResponseData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

data class Conceive2UiState(
    val onUserProfileApiCall: () -> Unit = {},
    val onUserProfileClick: (UserAuthResponseData) -> Unit = {},
    val onConceive2Save: () -> Unit = {},
    val onBackClick: () -> Unit = {},
    //Api response
    val apiResultFlow: StateFlow<NetworkResult<ApiResponse<UserAuthResponseData>>?> = MutableStateFlow(null),
    var howAreYouTryingFirst: () -> Unit = {},
    var howAreYouTryingSecond: () -> Unit = {},
    var howAreYouTryingThird: () -> Unit = {},
    val clearAllApiResultFlow: () -> Unit = {},




)
