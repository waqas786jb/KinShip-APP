package com.kinship.mobile.app.ux.container.setting.deleteAccount

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import com.kinship.mobile.app.R
import com.kinship.mobile.app.data.source.Constants
import com.kinship.mobile.app.data.source.local.datastore.AppPreferenceDataStore
import com.kinship.mobile.app.data.source.remote.helper.NetworkResult
import com.kinship.mobile.app.data.source.remote.repository.ApiRepository
import com.kinship.mobile.app.model.domain.response.ApiResponse
import com.kinship.mobile.app.model.domain.response.message.MessageResponse
import com.kinship.mobile.app.navigation.NavigationAction
import com.kinship.mobile.app.ux.startup.StartupActivity
import es.dmoral.toasty.Toasty
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


class DeleteAccountUiStateUseCase
@Inject constructor(
    private val apiRepository: ApiRepository,
    private val appPreferenceDataStore: AppPreferenceDataStore
) {
    private val reasonFlow = MutableStateFlow("")
    private val apiDeleteKinshipResultFlow =
        MutableStateFlow<NetworkResult<ApiResponse<MessageResponse>>?>(null)

    operator fun invoke(
        context: Context,
        @Suppress("UnusedPrivateProperty")
        coroutineScope: CoroutineScope,
        navigate: (NavigationAction) -> Unit,
    ): DeleteAccountUiState {
        return DeleteAccountUiState(
            reasonFlow = reasonFlow,
            onReasonValueChange = { reasonFlow.value = it },
            onClickOfConfirmButton = {
                callDeleteKinshipAPI(context, coroutineScope)
            },
            apiResultFlow = apiDeleteKinshipResultFlow,
            clearAllApiResultFlow = { clearAllAPIResultFlow() },
            onApiSuccess = { navigateToRegisterScreen(context, navigate, coroutineScope) },
            onBackClick = { navigate(NavigationAction.PopIntent) }
        )
    }

    @SuppressLint("CheckResult")
    private fun callDeleteKinshipAPI(context: Context, coroutineScope: CoroutineScope) {
        if (reasonFlow.value.isBlank()) {
            Toasty.warning(
                context,
                context.getString(R.string.please_enter_valid_reason),
                Toast.LENGTH_SHORT,
                false
            ).show()
        } else {
            coroutineScope.launch {
                apiRepository.deleteAccount(reasonFlow.value).collect {
                    apiDeleteKinshipResultFlow.value = it
                }
            }
        }
        Log.d("TAG", "callDeleteKinshipAPI: ${reasonFlow.value}")
    }

    private fun clearAllAPIResultFlow() {
        apiDeleteKinshipResultFlow.value = null
    }

    private fun navigateToRegisterScreen(
        context: Context,
        navigate: (NavigationAction) -> Unit,
        coroutineScope: CoroutineScope
    ) {
        coroutineScope.launch { appPreferenceDataStore.clearAll() }
        coroutineScope.launch {
            appPreferenceDataStore.saveStartUpStartDestination(Constants.AppScreen.SIGN_UP)
            val intent = Intent(context, StartupActivity::class.java)
            navigate(NavigationAction.NavigateIntent(intent = intent, finishCurrentActivity = true))

        }
    }
}