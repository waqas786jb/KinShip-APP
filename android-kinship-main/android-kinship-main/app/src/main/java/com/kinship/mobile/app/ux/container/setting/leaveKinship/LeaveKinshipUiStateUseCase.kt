package com.kinship.mobile.app.ux.container.setting.leaveKinship

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.kinship.mobile.app.R
import com.kinship.mobile.app.data.source.Constants
import com.kinship.mobile.app.data.source.local.datastore.AppPreferenceDataStore
import com.kinship.mobile.app.data.source.remote.helper.NetworkResult
import com.kinship.mobile.app.data.source.remote.repository.ApiRepository
import com.kinship.mobile.app.model.domain.response.ApiResponse
import com.kinship.mobile.app.model.domain.response.group.CreateGroup
import com.kinship.mobile.app.model.domain.response.message.MessageResponse
import com.kinship.mobile.app.model.domain.response.signUp.UserAuthResponseData
import com.kinship.mobile.app.navigation.NavigationAction
import com.kinship.mobile.app.ux.startup.StartupActivity
import es.dmoral.toasty.Toasty
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


class LeaveKinshipUiStateUseCase
@Inject constructor(
    private val apiRepository: ApiRepository,
    private val appPreferenceDataStore: AppPreferenceDataStore

) {
    private val reasonFlow = MutableStateFlow("")
    private val kinshipName: StateFlow<String> get() = _kinshipName
    private val _kinshipName = MutableStateFlow("")
    private val apiLeaveKinshipResultFlow =
        MutableStateFlow<NetworkResult<ApiResponse<MessageResponse>>?>(null)

    operator fun invoke(
        context: Context,
        @Suppress("UnusedPrivateProperty")
        coroutineScope: CoroutineScope,
        navigate: (NavigationAction) -> Unit,
    ): LeaveKinshipUiState {
        _kinshipName.value=getCreateGroupData(coroutineScope)?.groupName?:""
        return LeaveKinshipUiState(
            reasonFlow = reasonFlow,
            onReasonValueChange = { reasonFlow.value = it },
            kinshipName = this.kinshipName,
            onClickOfConfirmButton = { callLeaveKinshipAPI(context, coroutineScope) },
            apiResultFlow = apiLeaveKinshipResultFlow,
            clearAllApiResultFlow = { clearAllAPIResultFlow() },
            onApiSuccess = {
                navigateToCommonQuestionScreen(coroutineScope, context, navigate)
            },
            onBackClick = { navigate(NavigationAction.PopIntent) }
        )
    }
    @SuppressLint("CheckResult")
    private fun callLeaveKinshipAPI(context: Context, coroutineScope: CoroutineScope) {
        if (reasonFlow.value.isBlank()) {
            Toasty.warning(
                context,
                context.getString(R.string.please_enter_valid_reason),
                Toast.LENGTH_SHORT,
                false
            ).show()

        } else {
            coroutineScope.launch {
                apiRepository.leaveKinship(reasonFlow.value).collect {
                    apiLeaveKinshipResultFlow.value = it
                }
            }
        }
    }
    private fun getCreateGroupData(coroutineScope: CoroutineScope): CreateGroup? {
        var data: CreateGroup? = null
        coroutineScope.launch { data = appPreferenceDataStore.getCreateGroupData() }
        return data
    }
    private fun navigateToCommonQuestionScreen(
        coroutineScope: CoroutineScope,
        context: Context,
        navigate: (NavigationAction) -> Unit
    ) {
        val isUserData = getUserData(coroutineScope)
        isUserData?.isProfileCompleted = false
        isUserData?.auth = getUserData(coroutineScope)?.auth
        isUserData?.profile = getUserData(coroutineScope)?.profile
        isUserData?.isVerify = getUserData(coroutineScope)?.isVerify
        isUserData?.email = getUserData(coroutineScope)?.email
        isUserData?._id = getUserData(coroutineScope)?._id
        coroutineScope.launch {
            isUserData?.let { appPreferenceDataStore.saveUserData(it) }
            coroutineScope.launch { appPreferenceDataStore.saveStartUpStartDestination(Constants.AppScreen.COMMON_QUESTION_SCREEN) }
            val intent = Intent(context, StartupActivity::class.java)
            navigate(NavigationAction.NavigateIntent(intent = intent, finishCurrentActivity = false))
        }
    }
    private fun clearAllAPIResultFlow() {
        apiLeaveKinshipResultFlow.value = null
    }
    private fun getUserData(coroutineScope: CoroutineScope): UserAuthResponseData? {
        var data: UserAuthResponseData? = null
        coroutineScope.launch { data = appPreferenceDataStore.getUserData() }
        return data
    }
}