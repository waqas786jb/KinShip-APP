package com.kinship.mobile.app.ux.startup.auth.forgetPassword

import android.content.Context
import android.util.Log
import com.kinship.mobile.app.data.source.local.datastore.AppPreferenceDataStore
import com.kinship.mobile.app.data.source.remote.helper.NetworkResult
import com.kinship.mobile.app.data.source.remote.repository.ApiRepository
import com.kinship.mobile.app.model.domain.request.resendOtp.ResendOtpRequest
import com.kinship.mobile.app.model.domain.response.ApiResponse
import com.kinship.mobile.app.model.domain.response.message.MessageResponse
import com.kinship.mobile.app.navigation.NavigationAction
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class GetForgetPasswordUiStateUseCase @Inject constructor(
    private val apiRepository: ApiRepository,
    private val appPreferenceDataStore: AppPreferenceDataStore
) {
    private val emailFlow = MutableStateFlow("")
    private val emailErrorFlow = MutableStateFlow<String?>(null)
    private val apiForgetPasswordResultFlow =
        MutableStateFlow<NetworkResult<ApiResponse<MessageResponse>>?>(null)

    operator fun invoke(
        context: Context,
        coroutineScope: CoroutineScope,
        navigate: (NavigationAction) -> Unit,

        ): ForgetPasswordUiState {
        return ForgetPasswordUiState(
            emailFlow = emailFlow,
            onEmailValueChange = { emailFlow.value = it;emailErrorFlow.value = null },
            apiForgetPasswordResultFlow = apiForgetPasswordResultFlow,
            emailErrorFlow = emailErrorFlow,

            onForgetPasswordCLick = {
                if (isSignInInfoValid()) {
                    makeOtpVerificationInReq(coroutineScope)
                }
            },
            onBackClick = {
                navigate(NavigationAction.Pop())
            },
            clearAllApiResultFlow = {
                clearAllAPIResultFlow()
            }

        )
    }

    private fun isSignInInfoValid(): Boolean {
        var validInfo = true
        if (emailFlow.value.isBlank()) {
            emailErrorFlow.value = "Please enter email"
            validInfo = false
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(emailFlow.value).matches()) {
            emailErrorFlow.value = "Please enter valid email"
            validInfo = false
        }
        return validInfo
    }
    private fun makeOtpVerificationInReq(coroutineScope: CoroutineScope) {
        coroutineScope.launch {
            val email = appPreferenceDataStore.getUserData()?.email ?: ""
            Log.d("TAG", "makeSignInReq: $email")
            val otpInRequest = ResendOtpRequest(
                email = emailFlow.value
            )
            callOtpVerificationAPI(otpInRequest, coroutineScope)
        }
    }

    private fun callOtpVerificationAPI(otpVericationRequest: ResendOtpRequest, coroutineScope: CoroutineScope) {
        coroutineScope.launch {
            apiRepository.forgetPassword(otpVericationRequest).collect {
                apiForgetPasswordResultFlow.value = it
            }
        }
    }

    private fun clearAllAPIResultFlow() {
        apiForgetPasswordResultFlow.value = null
    }

}