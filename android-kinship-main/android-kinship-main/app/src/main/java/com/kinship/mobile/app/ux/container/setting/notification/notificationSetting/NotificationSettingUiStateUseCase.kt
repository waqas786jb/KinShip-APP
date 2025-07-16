package com.kinship.mobile.app.ux.container.setting.notification.notificationSetting
import android.content.Context
import com.kinship.mobile.app.data.source.local.datastore.AppPreferenceDataStore
import com.kinship.mobile.app.data.source.remote.helper.NetworkResult
import com.kinship.mobile.app.data.source.remote.repository.ApiRepository
import com.kinship.mobile.app.model.domain.response.ApiResponse
import com.kinship.mobile.app.model.domain.response.notification.NotificationSettingResponse
import com.kinship.mobile.app.navigation.NavigationAction
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


class NotificationSettingUiStateUseCase
@Inject constructor(
    private val apiRepository: ApiRepository,
    private val appPreferenceDataStore: AppPreferenceDataStore
) {
    private val apiResultFlow = MutableStateFlow<NetworkResult<ApiResponse<NotificationSettingResponse>>?>(null)

    operator fun invoke(
        context: Context,
        @Suppress("UnusedPrivateProperty")
        coroutineScope: CoroutineScope,
        navigate: (NavigationAction) -> Unit,
    ): NotificationSettingUiState {
        return NotificationSettingUiState(
            clearAllApiResultFlow = { clearAllAPIResultFlow() },
            apiResultFlow = apiResultFlow,
            onApiSuccess = { storeSuccessDataAndNavigate(it, coroutineScope) },
            onBackClick = { navigate(NavigationAction.PopIntent) },
            onAPICall = { allNewPost, newEvents, directMessage ->
                callNotificationSettingAPI(coroutineScope, allNewPost, newEvents, directMessage)
            },
            allNewPost = getNotificationSettingsData(coroutineScope)?.allNewPosts == true || getNotificationSettingsData(coroutineScope)?.allNewPosts == null,
            newEvents =  getNotificationSettingsData(coroutineScope)?.newEvents == true || getNotificationSettingsData(coroutineScope)?.newEvents == null,
            directMessage = getNotificationSettingsData(coroutineScope)?.directMessage == true || getNotificationSettingsData(coroutineScope)?.directMessage == null
        )
    }

    private fun callNotificationSettingAPI(coroutineScope: CoroutineScope, allNewPost: Boolean, newEvents: Boolean, directMessage: Boolean) {
        val map: HashMap<String, String> = hashMapOf()
        map["allNewPosts"] = allNewPost.toString()
        map["newEvents"] = newEvents.toString()
        map["directMessage"] = directMessage.toString()

        coroutineScope.launch {
            apiRepository.notificationSetting(map).collect {
                apiResultFlow.value = it
            }
        }
    }

    private fun storeSuccessDataAndNavigate(data: NotificationSettingResponse, coroutineScope: CoroutineScope) {
        coroutineScope.launch {
            appPreferenceDataStore.saveNotificationSettingData(data)
        }
    }
    private fun getNotificationSettingsData(coroutineScope: CoroutineScope): NotificationSettingResponse? {
        var data: NotificationSettingResponse? = null
        coroutineScope.launch { data = appPreferenceDataStore.getNotificationSettingData() }
        return data
    }

    private fun clearAllAPIResultFlow() {
        apiResultFlow.value = null
    }
}