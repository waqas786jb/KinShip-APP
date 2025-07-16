package com.kinship.mobile.app.ux.startup.auth.signUp

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.widget.Toast
import com.kinship.mobile.app.R
import com.kinship.mobile.app.data.source.local.datastore.AppPreferenceDataStore
import com.kinship.mobile.app.data.source.remote.helper.NetworkResult
import com.kinship.mobile.app.data.source.remote.repository.ApiRepository
import com.kinship.mobile.app.model.domain.request.otpVerfication.OtpVerificationRequest
import com.kinship.mobile.app.model.domain.request.signUp.SignInRequest
import com.kinship.mobile.app.model.domain.response.ApiResponse
import com.kinship.mobile.app.model.domain.response.signUp.UserAuthResponseData
import com.kinship.mobile.app.navigation.NavigationAction
import com.kinship.mobile.app.ux.startup.auth.login.LogInRoute
import com.kinship.mobile.app.ux.startup.auth.otpVerfication.OtpVerificationRoute
import es.dmoral.toasty.Toasty
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.util.regex.Pattern
import javax.inject.Inject

class GetSignUpUiStateUseCase
@Inject constructor(
    private val apiRepository: ApiRepository,
    private val appPreferenceDataStore: AppPreferenceDataStore
) {
    private val emailFlow = MutableStateFlow("")
    private val emailErrorFlow = MutableStateFlow<String?>(null)
    private val passwordFlow = MutableStateFlow("")
    private val passwordErrorFlow = MutableStateFlow<String?>(null)
    private val confirmPasswordFlow = MutableStateFlow("")
    private val confirmPasswordErrorFlow = MutableStateFlow<String?>(null)
    private val termsErrorFlow = MutableStateFlow<String?>(null)
    private val showLoader = MutableStateFlow(false)
    private val termAndConditionCheck = MutableStateFlow(false)


    private val apiResultFlow =
        MutableStateFlow<NetworkResult<ApiResponse<UserAuthResponseData>>?>(null)

    operator fun invoke(
        context: Context,
        coroutineScope: CoroutineScope,
        navigate: (NavigationAction) -> Unit,
    ): SignUpUiState {
        return SignUpUiState(
            emailFlow = emailFlow,
            emailErrorFlow = emailErrorFlow,
            onEmailValueChange = { emailFlow.value = it; emailErrorFlow.value = null },
            passwordFlow = passwordFlow,
            confirmPasswordErrorFlow = confirmPasswordErrorFlow,
            passwordErrorFlow = passwordErrorFlow,
            onPasswordValueChange = { passwordFlow.value = it; passwordErrorFlow.value = null },
            confirmPasswordFlow = confirmPasswordFlow,
            onConfirmPasswordValueChange = {
                confirmPasswordFlow.value = it;confirmPasswordErrorFlow.value = null
            },
            apiResultFlow = apiResultFlow,
            onOtpVerificationClick = { navigate(NavigationAction.Navigate(OtpVerificationRoute.createRoute())) },
            onBackClick = { navigate(NavigationAction.PopAndNavigate(LogInRoute.createRoute())) },
            onSignInClick = {
                if (isSignInInfoValid(context)) {
                    makeSignInReq(coroutineScope)
                }
            },
            clearAllApiResultFlow = { clearAllAPIResultFlow() },
            onSignInSuccessfully = {
                storeResponseToDataStore(
                    coroutineScope = coroutineScope,
                    navigate = navigate,
                    userAuthResponseData = it
                )
            },
            termsAndConditionErrorFlow = termsErrorFlow,
            onTermsAndConditionErrorChecked = {
                termAndConditionCheck.value = it;termsErrorFlow.value = null
            },
            termAndConditionClick = {
                callTermAndConditionAPI(coroutineScope = coroutineScope, context = context)
            },
            showLoader = showLoader
        )

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

    private fun openTermsAndConditionsInBrowser(context: Context, url: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK // Add this flag
        }
        context.startActivity(intent)
    }

    private fun showErrorMessage(context: Context, errorMsg: String) {
        Toasty.error(context, errorMsg, Toast.LENGTH_SHORT, false).show()
    }

    private fun showWarningMessage(context: Context, errorMsg: String) {
        Toasty.warning(context, errorMsg, Toast.LENGTH_SHORT, false).show()
    }

    private fun isSignInInfoValid(context: Context): Boolean {
        val uppercase: Pattern = Pattern.compile("[A-Z]")
        val digit: Pattern = Pattern.compile("[0-9]")
        val specialChar: Pattern = Pattern.compile("[^a-zA-Z0-9]")
        var validInfo = true
        if (emailFlow.value.isBlank()) {
            emailErrorFlow.value = context.getString(R.string.enter_email_validation)
            validInfo = false
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(emailFlow.value).matches()) {
            emailErrorFlow.value = context.getString(R.string.enter_valid_email_validation)
            validInfo = false
        }
        if (passwordFlow.value.isBlank()) {
            passwordErrorFlow.value = context.getString(R.string.enter_password_validation)
            validInfo = false
        } else if (passwordFlow.value.length < 8) {
            passwordErrorFlow.value = context.getString(R.string.password_type_validation)
            validInfo = false
        } else if (!uppercase.matcher(passwordFlow.value).find()) {
            passwordErrorFlow.value = context.getString(R.string.password_type_validation)
            validInfo = false
        } else if (!specialChar.matcher(passwordFlow.value).find()) {
            passwordErrorFlow.value = context.getString(R.string.password_type_validation)
            validInfo = false
        } else if (!digit.matcher(passwordFlow.value).find()) {
            passwordErrorFlow.value = context.getString(R.string.password_type_validation)
            validInfo = false
        }
        if (confirmPasswordFlow.value.isBlank()) {
            confirmPasswordErrorFlow.value =
                context.getString(R.string.enter_confirm_password_validation)
            validInfo = false
        } else if (confirmPasswordFlow.value.length < 8) {
            confirmPasswordErrorFlow.value = context.getString(R.string.password_type_validation)
            validInfo = false
        } else if (!uppercase.matcher(confirmPasswordFlow.value).find()) {
            confirmPasswordErrorFlow.value = context.getString(R.string.password_type_validation)
            validInfo = false
        } else if (!specialChar.matcher(confirmPasswordFlow.value).find()) {
            confirmPasswordErrorFlow.value = context.getString(R.string.password_type_validation)
            validInfo = false
        } else if (!digit.matcher(confirmPasswordFlow.value).find()) {
            confirmPasswordErrorFlow.value = context.getString(R.string.password_type_validation)
            validInfo = false
        } else if (passwordFlow.value != confirmPasswordFlow.value) {
            confirmPasswordErrorFlow.value =
                context.getString(R.string.password_and_confirm_password_not_matched_validation)
            validInfo = false
        }
        if (!termAndConditionCheck.value) {
            termsErrorFlow.value = context.getString(R.string.please_check_terms_conditions)
            validInfo = false

        }
        return validInfo
    }

    private fun makeSignInReq(coroutineScope: CoroutineScope) {
        val signInRequest = SignInRequest(
            email = emailFlow.value,
            password = passwordFlow.value,
            confirmPassword = confirmPasswordFlow.value
        )
        callSignInAPI(signInRequest, coroutineScope)
    }
    private fun callSignInAPI(signInRequest: SignInRequest, coroutineScope: CoroutineScope) {
        coroutineScope.launch {
            apiRepository.register(signInRequest).collect {
                apiResultFlow.value = it
            }
        }
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
                navigate(NavigationAction.PopAndNavigate(OtpVerificationRoute.createRoute()))
            }
        }
    }

    private fun clearAllAPIResultFlow() {
        apiResultFlow.value = null
    }
}