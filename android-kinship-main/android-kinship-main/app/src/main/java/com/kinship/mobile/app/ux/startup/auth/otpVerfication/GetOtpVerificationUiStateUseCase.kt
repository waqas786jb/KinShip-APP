package com.kinship.mobile.app.ux.startup.auth.otpVerfication

import android.content.Context
import android.util.Log
import com.kinship.mobile.app.R
import com.kinship.mobile.app.data.source.local.datastore.AppPreferenceDataStore
import com.kinship.mobile.app.data.source.remote.helper.NetworkResult
import com.kinship.mobile.app.data.source.remote.repository.ApiRepository
import com.kinship.mobile.app.model.domain.request.otpVerfication.OtpVerificationRequest
import com.kinship.mobile.app.model.domain.request.resendOtp.ResendOtpRequest
import com.kinship.mobile.app.model.domain.response.ApiResponse
import com.kinship.mobile.app.model.domain.response.message.MessageResponse
import com.kinship.mobile.app.model.domain.response.signUp.UserAuthResponseData
import com.kinship.mobile.app.navigation.NavigationAction
import com.kinship.mobile.app.ux.startup.auth.questionFlow.CommonQuestionRoute
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class GetOtpVerificationUiStateUseCase @Inject constructor(
    private val apiRepository: ApiRepository,
    private val appPreferenceDataStore: AppPreferenceDataStore
) {
    private val otpFlow = MutableStateFlow("")
    private val otpErrorFlow = MutableStateFlow<String?>(null)
    private val apiResultFlow =
        MutableStateFlow<NetworkResult<ApiResponse<UserAuthResponseData>>?>(null)
    private val apiResendOtpResultFlow =
        MutableStateFlow<NetworkResult<ApiResponse<MessageResponse>>?>(null)

    operator fun invoke(
        context: Context,
        coroutine: CoroutineScope,
        navigate: (NavigationAction) -> Unit,
    ): OtpVerificationUiState {
        return OtpVerificationUiState(
            otpFlow = otpFlow,
            otpErrorFlow = otpErrorFlow,
            startCountDown = {},
            onOtpValueChanges = {
                otpFlow.value = it;otpErrorFlow.value = null
            },
            onResendOtpClick = {
                makeResendOtpReq(coroutine)
            },
            apiResultFlow = apiResultFlow,
            apiResendOtpResultFlow = apiResendOtpResultFlow,
            onOtpVerificationApiCall = {
                if (isSignInInfoValid(context)) {
                    makeOtpVerificationInReq(coroutine)
                }
            },
            onOtpFindingKinshipClick = {

                storeResponseToDataStore(
                    coroutineScope = coroutine,
                    navigate = navigate,
                    userAuthResponseData = it
                )
            },
            onBackClick = {
                navigate(NavigationAction.Pop())
            },
            clearAllApiResultFlow = {
                clearAllAPIResultFlow()
            },

            )
    }

    private fun storeResponseToDataStore(
        coroutineScope: CoroutineScope,
        navigate: (NavigationAction) -> Unit,
        userAuthResponseData: UserAuthResponseData?
    ) {
        coroutineScope.launch {
            userAuthResponseData?.let {
                appPreferenceDataStore.saveUserData(it)
                it.auth?.let { it1 -> appPreferenceDataStore.saveUserAuthData(it1) }

            }
            navigate(NavigationAction.Navigate(CommonQuestionRoute.createRoute()))

        }
    }

    private fun isSignInInfoValid(context: Context): Boolean {
        var validInfo = true
        if (otpFlow.value.isBlank()) {
            otpErrorFlow.value = context.getString(R.string.please_provide_otp_validation)
            validInfo = false
        } else if (otpFlow.value.length < 4) {
            otpErrorFlow.value = context.getString(R.string.requires_4_digits_validation)
            validInfo = false
        }
        return validInfo
    }

    private fun makeOtpVerificationInReq(coroutineScope: CoroutineScope) {
        coroutineScope.launch {
            val email = appPreferenceDataStore.getUserData()?.email ?: ""
            Log.d("TAG", "makeSignInReq: $email")
            val otpInRequest = OtpVerificationRequest(
                otp = otpFlow.value,
                type = 1,
                email = email
            )
            callOtpVerificationAPI(otpInRequest, coroutineScope)
        }
    }

    private fun callOtpVerificationAPI(
        otpVericationRequest: OtpVerificationRequest,
        coroutineScope: CoroutineScope
    ) {
        coroutineScope.launch {
            apiRepository.otpVerification(otpVericationRequest).collect {
                apiResultFlow.value = it
            }
        }
    }

    private fun makeResendOtpReq(coroutineScope: CoroutineScope) {
        coroutineScope.launch {
            val email = appPreferenceDataStore.getUserData()?.email ?: ""
            Log.d("TAG", "makeSignInReq: $email")
            val otpInRequest = ResendOtpRequest(
                email = email
            )
            callResendOtpAPI(otpInRequest, coroutineScope)
        }
    }

    private fun callResendOtpAPI(
        otpVericationRequest: ResendOtpRequest,
        coroutineScope: CoroutineScope
    ) {
        coroutineScope.launch {
            apiRepository.resendOtp(otpVericationRequest).collect {
                apiResendOtpResultFlow.value = it

            }
        }
    }

    //Clear All API Result flow
    private fun clearAllAPIResultFlow() {
        apiResultFlow.value = null
        apiResendOtpResultFlow.value = null
    }
}
