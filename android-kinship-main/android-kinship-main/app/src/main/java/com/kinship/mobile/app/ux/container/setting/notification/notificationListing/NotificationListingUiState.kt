package com.kinship.mobile.app.ux.container.setting.notification.notificationListing
import android.content.Context
import androidx.paging.PagingData
import com.kinship.mobile.app.model.domain.response.notification.NotificationListResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class NotificationListingUiState (
    val clearAllApiResultFlow: () -> Unit = {},
    val onBackClick: () -> Unit = {},
    val showLoader: StateFlow<Boolean> = MutableStateFlow(false),
    val notificationList: StateFlow<PagingData<NotificationListResponse>> = MutableStateFlow(
        PagingData.empty()
    ),
    val noDataFound: StateFlow<Boolean> = MutableStateFlow(false),
    val navigateToNotificationItem:(NotificationListResponse)->Unit={},
    val isLoading: StateFlow<Boolean> = MutableStateFlow(false),
    val notificationListingAPICall:()->Unit={},
    val isAllEventsRefreshing: StateFlow<Boolean> = MutableStateFlow(false),

    val onLoadNextPage:()->Unit={},
    val initSocketListener: (Context) -> Unit = {},
    )