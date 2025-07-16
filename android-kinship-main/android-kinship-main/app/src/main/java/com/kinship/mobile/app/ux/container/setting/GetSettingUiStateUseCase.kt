package com.kinship.mobile.app.ux.container.setting
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.widget.Toast
import com.kinship.mobile.app.R
import com.kinship.mobile.app.data.source.Constants
import com.kinship.mobile.app.data.source.local.datastore.AppPreferenceDataStore
import com.kinship.mobile.app.data.source.remote.helper.NetworkResult
import com.kinship.mobile.app.data.source.remote.repository.ApiRepository
import com.kinship.mobile.app.model.domain.request.resendOtp.LogOutRequest
import com.kinship.mobile.app.model.domain.response.ApiResponse
import com.kinship.mobile.app.model.domain.response.group.CreateGroup
import com.kinship.mobile.app.model.domain.response.message.MessageResponse
import com.kinship.mobile.app.navigation.NavigationAction
import com.kinship.mobile.app.utils.AppUtils
import com.kinship.mobile.app.ux.container.ContainerActivity
import com.kinship.mobile.app.ux.startup.StartupActivity
import es.dmoral.toasty.Toasty
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class GetSettingUiStateUseCase
@Inject constructor(
    private val apiRepository: ApiRepository,
    private val appPreferenceDataStore: AppPreferenceDataStore
) {
    private val apiLogoutResultFlow =
        MutableStateFlow<NetworkResult<ApiResponse<MessageResponse>>?>(null)
    private val profileImgFlow = MutableStateFlow("")
    private val nameFlow = MutableStateFlow("")
    private val emailFlow = MutableStateFlow("")
    private val showLoader = MutableStateFlow(false)
    operator fun invoke(
        context: Context,
        @Suppress("UnusedPrivateProperty")
        coroutineScope: CoroutineScope,
        navigate: (NavigationAction) -> Unit,
    ): SettingUiState {
        getDataFromPrefAndShowIt(coroutineScope)
        return SettingUiState(
            onClickEditProfileButton = {
                navigateToSettingScreens(
                    context,
                    navigate,
                    Constants.ContainerScreens.EDIT_PROFILE_SCREEN
                )
            },
            onClickNotificationSettingButton = {
                navigateToSettingScreens(
                    context,
                    navigate,
                    Constants.ContainerScreens.NOTIFICATION_SETTING_SCREEN
                )
            },
            onClickChangePasswordButton = {
                navigateToSettingScreens(
                    context,
                    navigate,
                    Constants.ContainerScreens.CHANGE_PASSWORD_SCREEN
                )
            },
            onClickUpdateContactDetailsButton = {
                navigateToSettingScreens(
                    context,
                    navigate,
                    Constants.ContainerScreens.UPDATE_CONTACT_DETAILS
                )
            },
            onClickEventDetailsButton = {
                navigateToSettingScreens(
                    context,
                    navigate,
                    Constants.ContainerScreens.EVENT_DETAILS
                )
            },
            onClickLeaveKinshipButton = {
                if (getCreateGroupData(coroutineScope)?.isGroupCreated == true) {
                    navigateToSettingScreens(
                        context,
                        navigate,
                        Constants.ContainerScreens.LEAVE_KINSHIP_SCREEN
                    )
                } else {
                    Toasty.warning(
                        context,
                        context.getString(R.string.you_cannot_leave_the_kinship_as_you_do_not_belong_to_any_kinship_group),
                        Toast.LENGTH_SHORT,
                        false
                    ).show()
                }
            },
            onClickDeleteAccountButton = {
                navigateToSettingScreens(
                    context,
                    navigate,
                    Constants.ContainerScreens.DELETE_ACCOUNT_SCREEN
                )
            },
            onClickLogoutButton = { makeLogoutReq(coroutineScope, context) },
            profilePicFlow = profileImgFlow,
            nameFlow = nameFlow,
            emailFlow = emailFlow,
            apiLogoutResultFlow = apiLogoutResultFlow,
            clearAllApiResultFlow = { clearAllAPIResultFlow() },
            clearAllPrefData = { clearAllPrefData(coroutineScope, context, navigate) },
            onGetDataFromPref = {
                coroutineScope.launch {
                    if (appPreferenceDataStore.isProfilePicUpdated()) {
                        appPreferenceDataStore.setIsProfilePicUpdated(false)
                        getDataFromPrefAndShowIt(this)
                    }
                }
            },
            onClickHelpButton = {
                navigateToSettingScreens(
                    context,
                    navigate,
                    Constants.ContainerScreens.HELP_SCREEN
                )
            },
            onTermConditionClick = {
                callTermAndConditionAPI(coroutineScope = coroutineScope, context = context)
            },
            showLoader = showLoader

        )
    }
    private fun navigateToSettingScreens(
        context: Context,
        navigate: (NavigationAction) -> Unit,
        screenName: String
    ) {
        val intent = Intent(context, ContainerActivity::class.java)
        intent.putExtra(Constants.IntentData.SCREEN_NAME, screenName)
        navigate(NavigationAction.NavigateIntent(intent = intent, finishCurrentActivity = false))
    }
    private fun makeLogoutReq(coroutineScope: CoroutineScope, context: Context) {
        coroutineScope.launch {
            val deviceId = AppUtils.getDeviceId(context)
            val logoutReq = LogOutRequest(
                deviceId = deviceId
            )
            Log.d("TAG", "makeLogoutReq: $deviceId")
            callLogoutAPI(logoutReq, coroutineScope)
        }
    }
    private fun callLogoutAPI(logOutRequest: LogOutRequest, coroutineScope: CoroutineScope) {
        coroutineScope.launch {
            apiRepository.logout(logOutRequest).collect {
                apiLogoutResultFlow.value = it
            }
        }
    }
    private fun getCreateGroupData(coroutineScope: CoroutineScope): CreateGroup? {
        var data: CreateGroup? = null
        coroutineScope.launch { data = appPreferenceDataStore.getCreateGroupData() }
        return data
    }

    private fun getDataFromPrefAndShowIt(coroutineScope: CoroutineScope) {
        coroutineScope.launch {
            val authUserData = appPreferenceDataStore.getUserData()
            authUserData?.let {
                //getUserData(coroutineScope)?.profile?.firstName.plus(" ").plus(getUserData(coroutineScope)?.profile?.lastName)
                nameFlow.value = it.profile?.firstName.plus(" ").plus(it.profile?.lastName)
                emailFlow.value = it.email.toString()
                profileImgFlow.value = it.profile?.profileImage.toString()
            }
        }
    }
    private fun callTermAndConditionAPI(
        coroutineScope: CoroutineScope,
        context: Context
    ) {
        coroutineScope.launch {
            apiRepository.termAndCondition().collect {
                when (it) {
                    is NetworkResult.Error -> {
                        showLoader.value=false
                        showErrorMessage(context,it.data?.message?:"")
                    }
                    is NetworkResult.Loading -> {
                        showLoader.value=true
                    }
                    is NetworkResult.Success -> {
                        showLoader.value=false
                        val url = it.data?.data?.url
                        if (!url.isNullOrEmpty()) {
                            openTermsAndConditionsInBrowser(context, url)
                        } else {
                            showErrorMessage(context, "Invalid URL")
                        }
                    }
                    is NetworkResult.UnAuthenticated -> {
                        showLoader.value=false
                        showWarningMessage(context,it.data?.message?:"")
                    }
                }
            }
        }
    }

    private fun showErrorMessage(context: Context, errorMsg: String) {
        Toasty.error(context, errorMsg, Toast.LENGTH_SHORT, false).show()
    }

    private fun showWarningMessage(context: Context, errorMsg: String) {
        Toasty.warning(context, errorMsg, Toast.LENGTH_SHORT, false).show()
    }

    private fun openTermsAndConditionsInBrowser(context: Context, url: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK // Add this flag
        }
        context.startActivity(intent)
    }

    private fun clearAllAPIResultFlow() {
        apiLogoutResultFlow.value = null
    }
    private fun clearAllPrefData(
        coroutineScope: CoroutineScope,
        context: Context,
        navigate: (NavigationAction) -> Unit
    ) {
        coroutineScope.launch { appPreferenceDataStore.clearAll() }
        coroutineScope.launch { appPreferenceDataStore.saveStartUpStartDestination(Constants.AppScreen.LOG_IN) }
        val intent = Intent(context, StartupActivity::class.java)
        navigate(NavigationAction.NavigateIntent(intent = intent, finishCurrentActivity = true))
    }
}