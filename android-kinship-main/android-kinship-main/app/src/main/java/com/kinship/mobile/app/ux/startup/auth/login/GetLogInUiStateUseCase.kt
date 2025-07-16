package com.kinship.mobile.app.ux.startup.auth.login

import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.kinship.mobile.app.R
import com.kinship.mobile.app.data.source.local.datastore.AppPreferenceDataStore
import com.kinship.mobile.app.data.source.remote.helper.NetworkResult
import com.kinship.mobile.app.data.source.remote.repository.ApiRepository
import com.kinship.mobile.app.model.domain.request.signUp.logIn.LogInRequest
import com.kinship.mobile.app.model.domain.response.ApiResponse
import com.kinship.mobile.app.model.domain.response.signUp.UserAuthResponseData
import com.kinship.mobile.app.navigation.NavigationAction
import com.kinship.mobile.app.ux.main.MainActivity
import com.kinship.mobile.app.ux.startup.auth.forgetPassword.ForgetPasswordRoute
import com.kinship.mobile.app.ux.startup.auth.otpVerfication.OtpVerificationRoute
import com.kinship.mobile.app.ux.startup.auth.questionFlow.CommonQuestionRoute
import com.kinship.mobile.app.ux.startup.auth.signUp.SignUpRoute
import es.dmoral.toasty.Toasty
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.util.regex.Pattern
import javax.inject.Inject

class GetLogInUiStateUseCase
@Inject constructor(
    private val apiRepository: ApiRepository,
    private val appPreferenceDataStore: AppPreferenceDataStore

) {
    private val emailFlow = MutableStateFlow("")
    private val emailErrorFlow = MutableStateFlow<String?>(null)
    private val passwordFlow = MutableStateFlow("")
    private val passwordErrorFlow = MutableStateFlow<String?>(null)
    private val apiResultFlow =
        MutableStateFlow<NetworkResult<ApiResponse<UserAuthResponseData>>?>(null)



    operator fun invoke(
        context: Context,
        coroutineScope: CoroutineScope,
        navigate: (NavigationAction) -> Unit,
    ): LogInUiState {
        return LogInUiState(
            emailFlow = emailFlow,
            onEmailValueChange = { emailFlow.value = it;emailErrorFlow.value = null },
            passwordFlow = passwordFlow,
            onPasswordValueChange = { passwordFlow.value = it;passwordErrorFlow.value = null },
            emailErrorFlow = emailErrorFlow,
            apiResultFlow = apiResultFlow,
            onRegisterClick = {
                navigate(NavigationAction.Navigate(SignUpRoute.createRoute()))
            },
            onLoginInSuccessfully = {
                storeResponseToDataStore(context = context, coroutineScope = coroutineScope, navigate = navigate, userAuthResponseData = it)
            },
            passwordErrorFlow = passwordErrorFlow,
            onLoginClick = {
                if (isSignInInfoValid(context)) {
                    makeLogInReq(coroutineScope)
                }
            },
            clearAllApiResultFlow = {
                clearAllAPIResultFlow()
            },
            onForgetPasswordClick = {
                navigate(NavigationAction.Navigate(ForgetPasswordRoute.createRoute()))
            }
        )
    }
    private fun makeLogInReq(coroutineScope: CoroutineScope) {
        val logInRequest = LogInRequest(
            email = emailFlow.value,
            password = passwordFlow.value,
        )
        callLogInAPI(logInRequest, coroutineScope)
    }
    private fun storeResponseToDataStore(context: Context, coroutineScope: CoroutineScope, navigate: (NavigationAction) -> Unit, userAuthResponseData: UserAuthResponseData?) {
        coroutineScope.launch {
            userAuthResponseData?.let {
                appPreferenceDataStore.saveUserData(it)
                it.auth?.let { it1 -> appPreferenceDataStore.saveUserAuthData(it1) }
                if (userAuthResponseData.isVerify == true){
                    if (userAuthResponseData.isProfileCompleted == true) {
                       navigateToHomeScreen(context,navigate)
                        Toasty.success(context, context.getString(R.string.login_successfully), Toast.LENGTH_SHORT, false).show()
                    } else {
                        navigate(NavigationAction.Navigate(CommonQuestionRoute.createRoute()))
                    }
                }else{
                    navigate(NavigationAction.Navigate(OtpVerificationRoute.createRoute()))
                    Toasty.success(context, context.getString(R.string.otp_sent_your_mail_validation), Toast.LENGTH_SHORT, false).show()
                }
            }

        }

    }
    private fun navigateToHomeScreen(context: Context, navigate: (NavigationAction) -> Unit) {
        val intent = Intent(context, MainActivity::class.java)
        navigate(NavigationAction.NavigateIntent(intent = intent, finishCurrentActivity = true))
    }

    private fun callLogInAPI(logInRequest: LogInRequest, coroutineScope: CoroutineScope) {
        coroutineScope.launch {
            apiRepository.logIn(logInRequest).collect {
                apiResultFlow.value = it
            }
        }
    }

    private fun isSignInInfoValid(context: Context): Boolean {
        val uppercase: Pattern = Pattern.compile("[A-Z]")
        val digit: Pattern = Pattern.compile("[0-9]")
        val specialChar: Pattern = Pattern.compile("[^a-zA-Z0-9]")

        var validInfo = true

        if (emailFlow.value.isBlank()) {
            emailErrorFlow.value = context.getString(R.string.enter_valid_email)
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
        return validInfo
    }
    private fun clearAllAPIResultFlow() {
        apiResultFlow.value = null

    }


}