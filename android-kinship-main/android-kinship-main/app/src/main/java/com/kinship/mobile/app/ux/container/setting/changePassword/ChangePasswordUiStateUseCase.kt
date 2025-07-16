package com.kinship.mobile.app.ux.container.setting.changePassword
import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.kinship.mobile.app.R
import com.kinship.mobile.app.data.source.Constants
import com.kinship.mobile.app.data.source.remote.helper.NetworkResult
import com.kinship.mobile.app.data.source.remote.repository.ApiRepository
import com.kinship.mobile.app.model.domain.request.resendOtp.ChangePasswordRequest
import com.kinship.mobile.app.model.domain.request.userForgetPassword.NewPasswordRequest
import com.kinship.mobile.app.model.domain.request.userForgetPassword.UserForgetPasswordRequest
import com.kinship.mobile.app.model.domain.response.ApiResponse
import com.kinship.mobile.app.model.domain.response.message.MessageResponse
import com.kinship.mobile.app.model.domain.response.signUp.UserAuthResponseData
import com.kinship.mobile.app.navigation.NavigationAction
import com.kinship.mobile.app.ux.container.ContainerActivity
import com.kinship.mobile.app.ux.main.MainActivity
import es.dmoral.toasty.Toasty
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.regex.Pattern
import javax.inject.Inject


class ChangePasswordUiStateUseCase
@Inject constructor(
    private val apiRepository: ApiRepository,

) {
    private val currentPasswordFlow = MutableStateFlow("")
    private val confirmPasswordFlow = MutableStateFlow("")
    private val newPasswordFlow = MutableStateFlow("")
    private val newPasswordErrorFlow = MutableStateFlow<String?>(null)
    private val currentPasswordErrorFlow = MutableStateFlow<String?>(null)
    private val confirmPasswordErrorFlow = MutableStateFlow<String?>(null)
    private val showLoader = MutableStateFlow(false)
    private var _isForgetPassword = MutableStateFlow("")
    private val isForgetPassword: StateFlow<String> get() = _isForgetPassword
    private val apiChangePasswordResultFlow =
        MutableStateFlow<NetworkResult<ApiResponse<MessageResponse>>?>(null)
    private val apiRefreshTokenResultFlow =
        MutableStateFlow<NetworkResult<ApiResponse<UserAuthResponseData>>?>(null)

    operator fun invoke(
        context: Context,
        @Suppress("UnusedPrivateProperty")
        coroutineScope: CoroutineScope,
        isBoolean: String,
        navigate: (NavigationAction) -> Unit,
    ): ChangePasswordUiState {
        _isForgetPassword.value = isBoolean
        return ChangePasswordUiState(
            currentPasswordFlow = currentPasswordFlow,
            onCurrentPasswordValueChange = {
                currentPasswordFlow.value = it; currentPasswordErrorFlow.value = null
            },
            currentPasswordErrorFlow = currentPasswordErrorFlow,
            newPasswordFlow = newPasswordFlow,
            onNewPasswordValueChange = {
                newPasswordFlow.value = it; newPasswordErrorFlow.value = null
            },
            showLoader = showLoader,
            newPasswordErrorFlow = newPasswordErrorFlow,
            confirmPasswordFlow = confirmPasswordFlow,
            onConfirmPasswordValueChange = {
                confirmPasswordFlow.value = it; confirmPasswordErrorFlow.value = null
            },
            isForgetPassword = isForgetPassword,
            confirmPasswordErrorFlow = confirmPasswordErrorFlow,
            onClickUpdateButton = {
                if (isInfoValid(context)) {
                    if (_isForgetPassword.value.isEmpty()) {
                        makeChangePasswordReq(coroutineScope, context, navigate)
                        //Toast.makeText(context, "password", Toast.LENGTH_SHORT).show()
                    } else {
                        makeNewPasswordReq(coroutineScope, context, navigate)
                    }
                }
            },
            apiResultFlow = apiChangePasswordResultFlow,
            clearAllApiResultFlow = { clearAllAPIResultFlow() },
            onApiSuccess = {
                //callRefreshTokenAPI(coroutineScope)
                navigate(NavigationAction.PopIntent)
            },
            onBackClick = { navigate(NavigationAction.PopIntent) },
            apiRefreshTokenResultFlow = apiRefreshTokenResultFlow,
            onRefreshTokenApiSuccess = {
                //navigateToHomeScreen(context, navigate, it, coroutineScope)
            },
            forgetPasswordAPICall = {
                makeUserForgetPasswordReq(coroutineScope, context, navigate)

            }
        )
    }

    private fun makeChangePasswordReq(
        coroutineScope: CoroutineScope,
        context: Context,
        navigate: (NavigationAction) -> Unit
    ) {
        val changePasswordRequest = ChangePasswordRequest(
            oldPassword = currentPasswordFlow.value,
            password = newPasswordFlow.value,
            confirmPassword = confirmPasswordFlow.value
        )

        callChangePasswordAPI(changePasswordRequest, coroutineScope, context, navigate)
    }

    private fun callChangePasswordAPI(
        changePassword: ChangePasswordRequest,
        coroutineScope: CoroutineScope,
        context: Context,
        navigate: (NavigationAction) -> Unit
    ) {
        coroutineScope.launch {
            apiRepository.changePassword(changePassword).collect {
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
                        Toasty.success(
                            context,
                            it.data?.message.toString(),
                            Toast.LENGTH_SHORT,
                            false
                        )
                            .show()
                        navigate(NavigationAction.PopIntent)
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


    private fun makeNewPasswordReq(
        coroutineScope: CoroutineScope,
        context: Context,
        navigate: (NavigationAction) -> Unit
    ) {
        val newPasswordRequest = NewPasswordRequest(
            step = 2,
            password = newPasswordFlow.value,
            confirmPassword = confirmPasswordFlow.value
        )

        callNewPasswordAPI(newPasswordRequest, coroutineScope, context, navigate)
    }

    private fun callNewPasswordAPI(
        newPassword: NewPasswordRequest,
        coroutineScope: CoroutineScope,
        context: Context,
        navigate: (NavigationAction) -> Unit
    ) {
        coroutineScope.launch {
            apiRepository.newPassword(newPassword).collect {
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
                        Toasty.success(
                            context,
                            it.data?.message.toString(),
                            Toast.LENGTH_SHORT,
                            false
                        ).show()
                        //navigate(NavigationAction.PopIntent)
                        navigateToSettingScreen(
                            context,
                            navigate,
                            Constants.ContainerScreens.SETTING_SCREEN
                        )
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

    private fun makeUserForgetPasswordReq(
        coroutineScope: CoroutineScope,
        context: Context,
        navigate: (NavigationAction) -> Unit,
    ) {
        val userForgetRequest = UserForgetPasswordRequest(
            step = 1
        )
        callUserForgetPasswordAPI(userForgetRequest, coroutineScope, context, navigate)
    }

    private fun callUserForgetPasswordAPI(
        userForgetPasswordReq: UserForgetPasswordRequest,
        coroutineScope: CoroutineScope,
        context: Context,
        navigate: (NavigationAction) -> Unit,
    ) {
        coroutineScope.launch {
            apiRepository.userForgetPassword(userForgetPasswordReq).collect {
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
                        Toasty.success(
                            context,
                            it.data?.message.toString(),
                            Toast.LENGTH_SHORT,
                            false
                        ).show()
                        navigateToWithOutBottomNavigationScreen(
                            context,
                            navigate,
                            Constants.ContainerScreens.OTP_VERIFICATION
                        )
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

    private fun navigateToWithOutBottomNavigationScreen(
        context: Context,
        navigate: (NavigationAction) -> Unit,
        screenName: String
    ) {
        val intent = Intent(context, ContainerActivity::class.java)
        intent.putExtra(Constants.IntentData.SCREEN_NAME, screenName)
        navigate(NavigationAction.NavigateIntent(intent = intent, finishCurrentActivity = false))
    }

    private fun navigateToSettingScreen(
        context: Context,
        navigate: (NavigationAction) -> Unit,
        screenName: String
    ) {
        val intent = Intent(context, MainActivity::class.java)
        intent.putExtra(Constants.IntentData.SCREEN_NAME, screenName)
        navigate(NavigationAction.NavigateIntent(intent = intent, finishCurrentActivity = false))
    }

    private fun isInfoValid(context: Context): Boolean {
        val uppercase: Pattern = Pattern.compile("[A-Z]")
        val digit: Pattern = Pattern.compile("[0-9]")
        val specialChar: Pattern = Pattern.compile("[^a-zA-Z0-9]")
        var validInfo = true

        if (_isForgetPassword.value.isEmpty()) {
            if (currentPasswordFlow.value.isBlank()) {
                currentPasswordErrorFlow.value = context.getString(R.string.enter_current_password)
                validInfo = false
            } else if (currentPasswordFlow.value.length < 8) {
                currentPasswordErrorFlow.value =
                    context.getString(R.string.password_type_validation)
                validInfo = false
            } else if (!uppercase.matcher(currentPasswordFlow.value).find()) {
                currentPasswordErrorFlow.value =
                    context.getString(R.string.password_type_validation)
                validInfo = false
            } else if (!specialChar.matcher(currentPasswordFlow.value).find()) {
                currentPasswordErrorFlow.value =
                    context.getString(R.string.password_type_validation)
                validInfo = false
            } else if (!digit.matcher(currentPasswordFlow.value).find()) {
                currentPasswordErrorFlow.value =
                    context.getString(R.string.password_type_validation)
                validInfo = false
            }
        }

        if (newPasswordFlow.value.isBlank()) {
            newPasswordErrorFlow.value = context.getString(R.string.enter_new_password)
            validInfo = false
        } else if (newPasswordFlow.value.length < 8) {
            newPasswordErrorFlow.value =
                context.getString(R.string.password_type_validation)
            validInfo = false
        } else if (!uppercase.matcher(newPasswordFlow.value).find()) {
            newPasswordErrorFlow.value =
                context.getString(R.string.password_type_validation)
            validInfo = false
        } else if (!specialChar.matcher(newPasswordFlow.value).find()) {
            newPasswordErrorFlow.value =
                context.getString(R.string.password_type_validation)
            validInfo = false
        } else if (!digit.matcher(newPasswordFlow.value).find()) {
            newPasswordErrorFlow.value =
                context.getString(R.string.password_type_validation)
            validInfo = false
        }

        if (confirmPasswordFlow.value.isBlank()) {
            confirmPasswordErrorFlow.value = context.getString(R.string.enter_confirm_password)
            validInfo = false
        } else if (confirmPasswordFlow.value.length < 8) {
            confirmPasswordErrorFlow.value =
                context.getString(R.string.password_type_validation)
            validInfo = false
        } else if (!uppercase.matcher(confirmPasswordFlow.value).find()) {
            confirmPasswordErrorFlow.value =
                context.getString(R.string.password_type_validation)
            validInfo = false
        } else if (!specialChar.matcher(confirmPasswordFlow.value).find()) {
            confirmPasswordErrorFlow.value =
                context.getString(R.string.password_type_validation)
            validInfo = false
        } else if (!digit.matcher(confirmPasswordFlow.value).find()) {
            confirmPasswordErrorFlow.value =
                context.getString(R.string.password_type_validation)
            validInfo = false
        } else if (newPasswordFlow.value != confirmPasswordFlow.value) {
            confirmPasswordErrorFlow.value =
                context.getString(R.string.password_and_confirm_password_not_matched)
            validInfo = false
        }
        return validInfo

    }
    private fun clearAllAPIResultFlow() {
        apiChangePasswordResultFlow.value = null
        apiRefreshTokenResultFlow.value = null
    }

}