package com.kinship.mobile.app.ux.container.setting.notification.notificationListing
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kinship.mobile.app.navigation.ViewModelNav
import com.kinship.mobile.app.navigation.ViewModelNavImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

@HiltViewModel
class NotificationListingViewModel
@Inject constructor(
    @ApplicationContext context: Context,
    getNotificationListingUiStateUseCase: NotificationListingUiStateUseCase
) : ViewModel(), ViewModelNav by ViewModelNavImpl() {
    val uiState: NotificationListingUiState = getNotificationListingUiStateUseCase(context = context, coroutineScope = viewModelScope) { navigate(it) }
}