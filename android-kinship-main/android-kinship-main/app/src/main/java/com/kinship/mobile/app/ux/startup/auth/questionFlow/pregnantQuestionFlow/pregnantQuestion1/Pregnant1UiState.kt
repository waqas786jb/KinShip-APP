package com.kinship.mobile.app.ux.startup.auth.questionFlow.pregnantQuestionFlow.pregnantQuestion1
import com.kinship.mobile.app.data.source.remote.helper.NetworkResult
import com.kinship.mobile.app.model.domain.response.ApiResponse
import com.kinship.mobile.app.model.domain.response.signUp.UserAuthResponseData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

data class Pregnant1UiState(
 val onUserProfileAPICall: () -> Unit = {},
 val onUserProfileClick: () -> Unit = {},
 val onBackClick: () -> Unit = {},
 val dobErrorFlow: StateFlow<String?> = MutableStateFlow(null),
 val questionErrorFlow: StateFlow<String?> = MutableStateFlow(null),
 val onSelectedDateChange: (String) -> Unit = {},
 var onGirlValue: () -> Unit = {},
 var onBoyValue: () -> Unit = {},
 var onItSurpriseValue: () -> Unit = {},
 val onSingleGenderList: (String) -> Unit = {},
 val onMultipleGenderList: (String) -> Unit = {},
 // tab value and click
 val onSingleClick: () -> Unit = {},
 val onMultipleClick: () -> Unit = {},
 val onYesClick: () -> Unit = {},
 val onNoClick: () -> Unit = {},
 // multiple Question value
 var onMultipleGirlValue: () -> Unit = {},
 var onMultipleBoyValue: () -> Unit = {},
 var onMultipleBothValue: () -> Unit = {},
 var onMultipleItSurpriseValue: () -> Unit = {},
 val clearAllApiResultFlow: () -> Unit = {},
 val apiResultFlow: StateFlow<NetworkResult<ApiResponse<UserAuthResponseData>>?> = MutableStateFlow(null),


 )
