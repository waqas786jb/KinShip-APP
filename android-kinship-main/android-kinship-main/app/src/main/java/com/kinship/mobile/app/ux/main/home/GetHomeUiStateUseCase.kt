package com.kinship.mobile.app.ux.main.home

import android.content.Context
import android.content.Intent
import android.util.Log
import com.kinship.mobile.app.data.source.Constants
import com.kinship.mobile.app.data.source.local.datastore.AppPreferenceDataStore
import com.kinship.mobile.app.data.source.remote.helper.NetworkResult
import com.kinship.mobile.app.data.source.remote.repository.ApiRepository
import com.kinship.mobile.app.model.domain.response.ApiResponse
import com.kinship.mobile.app.model.domain.response.group.CreateGroup
import com.kinship.mobile.app.model.domain.response.signUp.UserAuthResponseData
import com.kinship.mobile.app.navigation.NavigationAction
import com.kinship.mobile.app.ux.container.ContainerActivity
import com.kinship.mobile.app.ux.startup.StartupActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class GetHomeUiStateUseCase
@Inject constructor(
    private val apiRepository: ApiRepository,
    private val appPreferenceDataStore: AppPreferenceDataStore
) {
    private val apiResultFlow =
        MutableStateFlow<NetworkResult<ApiResponse<CreateGroup>>?>(null)
    operator fun invoke(
        context: Context,
        @Suppress("UnusedPrivateProperty")
        coroutineScope: CoroutineScope,
        navigate: (NavigationAction) -> Unit,
    ): HomeUiState {
       // callGetGroupInAPI(coroutineScope)
        Log.d("TAG", "kinshipReason: ${getUserData(coroutineScope)?.profile?.kinshipReason}")
        Log.d("TAG", "getUserData: ${getUserData(coroutineScope)}")
        return HomeUiState(
            onChatClick = {
                navigateToChatScreen(context, navigate, Constants.ContainerScreens.CHAT_SCREEN)
            },
            onNotificationClick = {
                navigateToNotificationScreen(
                    context,
                    navigate,
                    Constants.ContainerScreens.NOTIFICATION_LISTING_SCREEN
                )
            },
            clearBackEntry = {
               navigate(NavigationAction.Pop())
            },
            onBack = {
                navigate(NavigationAction.PopIntent)
            },
            kinshipReason = getUserData(coroutineScope)?.profile?.kinshipReason ?: 0,
            apiResultFlow = apiResultFlow,
            onSaveGroupData = {
                storeResponseToDataStore(coroutineScope, it)
            },
            groupAPICall = {
                callGetGroupInAPI(coroutineScope)
            },
            userEmail = getUserData(coroutineScope)?.email.toString(),
            clearAllApiResultFlow = {
                clearAllAPIResultFlow()
            },
            userName = getUserData(coroutineScope)?.profile?.firstName.plus(" ")
                .plus(getUserData(coroutineScope)?.profile?.lastName),
            appRestart = {
                navigateToStartUpActivity(context = context, navigate = navigate)
            },
            navigateToMyCommunitiesScreen = {
                navigateToNotificationScreen(
                    context,
                    navigate,
                    Constants.ContainerScreens.MY_COMMUNITIES_SCREEN
                )
            }
        )
    }

    private fun navigateToNotificationScreen(
        context: Context,
        navigate: (NavigationAction) -> Unit,
        screenName: String
    ) {
        val intent = Intent(context, ContainerActivity::class.java)
        intent.putExtra(Constants.IntentData.SCREEN_NAME, screenName)
        navigate(NavigationAction.NavigateIntent(intent = intent, finishCurrentActivity = false))
    }

    private fun navigateToStartUpActivity(context: Context,navigate: (NavigationAction) -> Unit){
        val intent = Intent(context, StartupActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        intent.putExtra("reset", true)
        navigate(NavigationAction.NavigateIntent(intent = intent, finishCurrentActivity = false))
    }

    private fun navigateToChatScreen(
        context: Context,
        navigate: (NavigationAction) -> Unit,
        screenName: String
    ) {
        val intent = Intent(context, ContainerActivity::class.java)
        intent.putExtra(Constants.IntentData.SCREEN_NAME, screenName)
        navigate(NavigationAction.NavigateIntent(intent = intent, finishCurrentActivity = false))
    }
    private fun callGetGroupInAPI(coroutineScope: CoroutineScope) {
        coroutineScope.launch {
            apiRepository.getCreateGroup().collect {
                apiResultFlow.value = it
            }
        }
    }
    private fun storeResponseToDataStore(
        coroutineScope: CoroutineScope,
        createGroup: CreateGroup?
    ) {
        coroutineScope.launch {
            createGroup?.let {
                appPreferenceDataStore.saveCreateGroupData(it)
            }
            Log.d("TAG", "GroupDataMember: ${appPreferenceDataStore.getCreateGroupData()}")
        }
    }
    private fun getUserData(coroutineScope: CoroutineScope): UserAuthResponseData? {
        var data: UserAuthResponseData? = null
        coroutineScope.launch { data = appPreferenceDataStore.getUserData() }
        return data
    }
    private fun clearAllAPIResultFlow() {
        apiResultFlow.value = null
    }
}