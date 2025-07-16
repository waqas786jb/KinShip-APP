package com.kinship.mobile.app.ux.container.setting.notification.notificationSetting

import com.kinship.mobile.app.data.source.remote.helper.NetworkResult
import com.kinship.mobile.app.model.domain.response.ApiResponse
import com.kinship.mobile.app.model.domain.response.notification.NotificationSettingResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class NotificationSettingUiState(
    val allNewPost: Boolean = true,
    val newEvents: Boolean = true,
    val directMessage: Boolean = true,
    //event
    val onAPICall: (allNewPost: Boolean, newEvents: Boolean, directMessage: Boolean) -> Unit = { _: Boolean, _: Boolean, _: Boolean -> },
    val clearAllApiResultFlow: () -> Unit = {},


    val onApiSuccess: (NotificationSettingResponse) -> Unit = {},
    val onBackClick: () -> Unit = {},
    //Api response
    val apiResultFlow: StateFlow<NetworkResult<ApiResponse<NotificationSettingResponse>>?> = MutableStateFlow(null),
)