package com.kinship.mobile.app.ux.container.setting
import com.kinship.mobile.app.data.source.remote.helper.NetworkResult
import com.kinship.mobile.app.model.domain.response.ApiResponse
import com.kinship.mobile.app.model.domain.response.message.MessageResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

data class SettingUiState(
    //data
    val profilePicFlow: StateFlow<String> = MutableStateFlow(""),
    val nameFlow: StateFlow<String> = MutableStateFlow(""),
    val emailFlow: StateFlow<String> = MutableStateFlow(""),
    //click
    val onClickEditProfileButton: () -> Unit = {},
    val onClickNotificationButton: () -> Unit = {},
    val onClickChangePasswordButton: () -> Unit = {},
    val onClickLeaveKinshipButton: () -> Unit = {},
    val onClickDeleteAccountButton: () -> Unit = {},
    val onClickUpdateContactDetailsButton: () -> Unit = {},
    val onClickEventDetailsButton: () -> Unit = {},
    val onClickNotificationSettingButton: () -> Unit = {},
    val onClickLogoutButton: () -> Unit = {},
    val onUserGetData: () -> Unit = {},
    val clearAllApiResultFlow: () -> Unit = {},
    val clearAllPrefData: () -> Unit = {},
    val onGetDataFromPref: () -> Unit = {},
    val onClickHelpButton: () -> Unit = {},
    val onTermConditionClick:()->Unit={},

    val showLoader: StateFlow<Boolean> = MutableStateFlow(false),


    //API flow
    val apiLogoutResultFlow: StateFlow<NetworkResult<ApiResponse<MessageResponse>>?> = MutableStateFlow(null)
)