package com.kinship.mobile.app.ux.container.setting.updateContactDetails.updateOtpVerification
import android.content.Context
import android.util.Log
import android.widget.Toast
import com.kinship.mobile.app.R
import com.kinship.mobile.app.data.source.Constants
import com.kinship.mobile.app.data.source.local.datastore.AppPreferenceDataStore
import com.kinship.mobile.app.data.source.remote.helper.NetworkResult
import com.kinship.mobile.app.data.source.remote.repository.ApiRepository
import com.kinship.mobile.app.model.domain.request.otpVerfication.OtpUpdateVerificationRequest
import com.kinship.mobile.app.model.domain.request.resendOtp.ResendOtpRequest
import com.kinship.mobile.app.model.domain.request.userForgetPassword.NewPasswordOtpVerificationReq
import com.kinship.mobile.app.model.domain.response.ApiResponse
import com.kinship.mobile.app.model.domain.response.message.MessageResponse
import com.kinship.mobile.app.model.domain.response.signUp.UserAuthResponseData
import com.kinship.mobile.app.navigation.NavigationAction
import com.kinship.mobile.app.ux.container.setting.changePassword.ChangePasswordRoute
import es.dmoral.toasty.Toasty
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class GetUpdateOtpVerificationUiStateUseCase @Inject constructor(
    private val apiRepository: ApiRepository,
    private val appPreferenceDataStore: AppPreferenceDataStore
) {
    private val otpFlow = MutableStateFlow("")
    private val screen = MutableStateFlow("")
    private val otpErrorFlow = MutableStateFlow<String?>(null)
    private val showLoader = MutableStateFlow(false)
    private val apiResultFlow =
        MutableStateFlow<NetworkResult<ApiResponse<UserAuthResponseData>>?>(null)
    private val apiResendOtpResultFlow =
        MutableStateFlow<NetworkResult<ApiResponse<MessageResponse>>?>(null)

    operator fun invoke(
        context: Context,
        coroutine: CoroutineScope,
        email: String,
        navigate: (NavigationAction) -> Unit,
    ): OtpUpdateVerificationUiState {
        return OtpUpdateVerificationUiState(
            otpFlow = otpFlow,
            otpErrorFlow = otpErrorFlow,
            startCountDown = {},
            onOtpValueChanges = {
                otpFlow.value = it;otpErrorFlow.value = null
            },
            onResendOtpClick = {
                makeResendOtpReq(coroutine)
            },
            onScreen = {
                screen.value = it
            },
            apiResultFlow = apiResultFlow,
            apiResendOtpResultFlow = apiResendOtpResultFlow,
            onOtpVerificationApiCall = {
                if (isSignInInfoValid(context)) {
                    if (screen.value == Constants.ContainerScreens.OTP_VERIFICATION) {
                        makeNewPasswordOtpVerificationInReq(coroutine, context, navigate)
                    } else {
                        makeOtpVerificationInReq(coroutine)
                    }

                }
            },
            email = email,
            onOtpFindingKinshipClick = {
                if (screen.value == Constants.ContainerScreens.OTP_VERIFICATION) {
                    navigate(NavigationAction.Navigate(ChangePasswordRoute.createRoute(boolean = "newPassword")))
                } else {
                    storeResponseToDataStore(
                        coroutineScope = coroutine,
                        navigate = navigate,
                        userAuthResponseData = it
                    )
                }
            },
            showLoader = showLoader,
            onBackClick = {
                if (screen.value == Constants.ContainerScreens.OTP_VERIFICATION) {
                    navigate(NavigationAction.PopIntent)
                } else {
                    navigate(NavigationAction.Pop())
                }

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
        val userData = UserAuthResponseData()
        userData.auth = getUserData(coroutineScope)?.auth
        userData._id = getUserData(coroutineScope)?._id
        userData.isProfileCompleted = getUserData(coroutineScope)?.isProfileCompleted
        userData.isVerify = getUserData(coroutineScope)?.isVerify
        userData.profile = getUserData(coroutineScope)?.profile
        userData.email = userAuthResponseData?.email ?: ""
        coroutineScope.launch {
            appPreferenceDataStore.saveUserData(userData)
            navigate(NavigationAction.PopIntent)
            appPreferenceDataStore.setIsProfilePicUpdated(true)
        }
    }

    private fun getUserData(coroutineScope: CoroutineScope): UserAuthResponseData? {
        var data: UserAuthResponseData? = null
        coroutineScope.launch { data = appPreferenceDataStore.getUserData() }
        return data
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
            val oldEmail = appPreferenceDataStore.getUserData()?.email ?: ""
            val newEmail = appPreferenceDataStore.getUserData()?.profile?.newEmail ?: ""
            val otpInRequest = OtpUpdateVerificationRequest(
                email = newEmail,
                otp = otpFlow.value,
                type = 2,
                oldEmail = oldEmail
            )
            callOtpVerificationAPI(otpInRequest, coroutineScope)
        }
    }

    private fun callOtpVerificationAPI(
        otpVericationRequest: OtpUpdateVerificationRequest,
        coroutineScope: CoroutineScope
    ) {
        coroutineScope.launch {
            apiRepository.otpUpdateContactVerification(otpVericationRequest).collect {
                apiResultFlow.value = it
            }
        }
    }

    private fun makeNewPasswordOtpVerificationInReq(
        coroutineScope: CoroutineScope,
        context: Context,
        navigate: (NavigationAction) -> Unit,

        ) {
        coroutineScope.launch {
            val oldEmail = appPreferenceDataStore.getUserData()?.email ?: ""
            val newPasswordOtpVerificationReq = NewPasswordOtpVerificationReq(
                email = oldEmail,
                otp = otpFlow.value,
                type = 3
            )
            callNewPasswordOtpVerificationAPI(
                newPasswordOtpVerificationReq,
                coroutineScope,
                context,
                navigate
            )
        }
    }

    private fun callNewPasswordOtpVerificationAPI(
        newPasswordOtpVerification: NewPasswordOtpVerificationReq,
        coroutineScope: CoroutineScope,
        context: Context,
        navigate: (NavigationAction) -> Unit
    ) {
        coroutineScope.launch {
            apiRepository.otpNewPasswordVerification(newPasswordOtpVerification).collect {
                when (it) {
                    is NetworkResult.Error -> {
                        showLoader.value = false
                        Toasty.error(context, it.message.toString(), Toast.LENGTH_SHORT, false)
                            .show()
                    }

                    is NetworkResult.Loading -> {
                        showLoader.value = true
                    }

                    is NetworkResult.Success -> {
                        showLoader.value = false
                        navigate(NavigationAction.Navigate(ChangePasswordRoute.createRoute(boolean = "newPassword")))
                    }

                    is NetworkResult.UnAuthenticated -> {
                        showLoader.value = false
                        Toasty.warning(context, it.message.toString(), Toast.LENGTH_SHORT, false)
                            .show()
                    }
                }
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
