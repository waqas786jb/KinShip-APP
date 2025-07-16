package com.kinship.mobile.app.ux.startup.auth.questionFlow.babyQuestionFlow

import com.kinship.mobile.app.data.source.remote.helper.NetworkResult
import com.kinship.mobile.app.model.domain.response.ApiResponse
import com.kinship.mobile.app.model.domain.response.signUp.UserAuthResponseData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

data class Baby1UiState(
    val onUserDetailsAPICall: () -> Unit = {},
    val onUserDetailsCLick: () -> Unit = {},
    val onBackClick: () -> Unit = {},
    val singleBabyBornDateErrorFlow: StateFlow<String?> = MutableStateFlow(null),
    val multipleFirstBabyBornDateErrorFlow: StateFlow<String?> = MutableStateFlow(null),
    val multipleSecondBabyBornDateErrorFlow: StateFlow<String?> = MutableStateFlow(null),
    val onSingleBabyBornDate: (String) -> Unit = {},
    val onMultipleFirstBabyBornDate: (String) -> Unit = {},
    val onMultipleSecondBabyBornDate: (String) -> Unit = {},
    val onSingleGenderList: (String) -> Unit = {},
    val onMultipleGenderList: (String) -> Unit = {},
    //custom tab value
    val onSingleClick: () -> Unit = {},
    val onMultipleClick: () -> Unit = {},
    var onSingleGirlValue: () -> Unit = {},
    var onSingleBoyValue: () -> Unit = {},
    var onMultipleGirlValue: () -> Unit = {},
    var onMultipleBoyValue: () -> Unit = {},
    var onMultipleBothValue: () -> Unit = {},
    val onChildYesClick: () -> Unit = {},
    val onChildNoClick: () -> Unit = {},
    val onFirstTimeMomYesClick: () -> Unit = {},
    val onFirstTimeMomNoClick: () -> Unit = {},
    val firstDobErrorFlow: StateFlow<String?> = MutableStateFlow(null),
    val secondDobErrorFlow: StateFlow<String?> = MutableStateFlow(null),
    val apiResultFlow: StateFlow<NetworkResult<ApiResponse<UserAuthResponseData>>?> = MutableStateFlow(null),
    val clearAllApiResultFlow: () -> Unit = {}

)
