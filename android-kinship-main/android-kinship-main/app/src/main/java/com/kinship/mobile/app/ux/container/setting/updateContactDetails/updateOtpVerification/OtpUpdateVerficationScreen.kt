package com.kinship.mobile.app.ux.container.setting.updateContactDetails.updateOtpVerification

import android.annotation.SuppressLint
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.kinship.mobile.app.R
import com.kinship.mobile.app.data.source.Constants
import com.kinship.mobile.app.navigation.HandleNavigation
import com.kinship.mobile.app.navigation.scaffold.AppNavBarData
import com.kinship.mobile.app.navigation.scaffold.AppNavBarType
import com.kinship.mobile.app.navigation.scaffold.AppScaffold
import com.kinship.mobile.app.ui.compose.common.BottomButtonComponent
import com.kinship.mobile.app.ui.compose.common.CustomLoader
import com.kinship.mobile.app.ui.compose.common.OtpTextField
import com.kinship.mobile.app.ui.compose.common.TopBarComponent
import com.kinship.mobile.app.ui.networkresult.NetworkResultHandler
import com.kinship.mobile.app.ui.theme.Black
import com.kinship.mobile.app.ui.theme.Black50
import com.kinship.mobile.app.ui.theme.OpenSans
import com.kinship.mobile.app.ui.theme.White
import es.dmoral.toasty.Toasty
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@Composable
fun OtpUpdateVerificationScreen(
    navController: NavHostController = rememberNavController(),
    viewModel: OtpUpdateVerificationViewModel = hiltViewModel(),
    screen: String,
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val uiState = viewModel.uiState
    Log.d("TAG", "OtpUpdateVerificationScreen: $screen")
    val showLoader by uiState.showLoader.collectAsStateWithLifecycle()
    AppScaffold(
        modifier = Modifier
            .safeDrawingPadding()
            .clickable(indication = null,
                interactionSource = remember { MutableInteractionSource() })
            {
                keyboardController?.hide()
            },
        containerColor = White,
        topAppBar = {
            TopBarComponent(
                header = stringResource(id = R.string.otp_verification),
                isLineVisible = true,
                isBackVisible = true,
                onClick = {
                    uiState.onBackClick()
                }
            )
        },
        navBarData = AppNavBarData(
            appNavBarType = AppNavBarType.NAV_BAR,
            navBar = {
                BottomButtonComponent(
                    text = stringResource(id = R.string.verify), onClick = {
                        uiState.onOtpVerificationApiCall()
                    },
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 20.dp)
                )
            }
        ),
    ) {
        if (showLoader) {
            CustomLoader()
        }
        uiState.onScreen(screen)
        OtpUpdateVerificationContent(modifier = Modifier, uiState, screen = screen)
        val context = LocalContext.current
        val apiResultFlow by uiState.apiResultFlow.collectAsStateWithLifecycle()
        apiResultFlow?.let { it1 ->
            NetworkResultHandler(
                networkResult = it1,
                onSuccess = { apiResponse ->
                    Toasty.success(context, apiResponse.message, Toast.LENGTH_SHORT, false).show()
                    uiState.onOtpFindingKinshipClick(apiResponse.data)
                    uiState.clearAllApiResultFlow()

                }, onError = {
                    Toasty.error(context, it, Toast.LENGTH_SHORT, false).show()
                    uiState.clearAllApiResultFlow()

                },
                onUnAuthenticated = {
                    Toasty.warning(context, it, Toast.LENGTH_SHORT, false).show()
                    uiState.clearAllApiResultFlow()
                }
            )
        }
        val apiResendOtpFlow by uiState.apiResendOtpResultFlow.collectAsStateWithLifecycle()
        apiResendOtpFlow?.let { it1 ->
            NetworkResultHandler(
                networkResult = it1,
                onSuccess = { apiResponse ->
                    Toasty.success(context, apiResponse.message, Toast.LENGTH_SHORT, false).show()
                    uiState.clearAllApiResultFlow()

                }, onError = {
                    Toasty.error(context, it, Toast.LENGTH_SHORT, false).show()
                    uiState.clearAllApiResultFlow()
                },
                onUnAuthenticated = {
                    Toasty.warning(context, it, Toast.LENGTH_SHORT, false).show()
                    uiState.clearAllApiResultFlow()
                }
            )
        }

    }
    HandleNavigation(viewModelNav = viewModel, navController = navController)
}

@SuppressLint("RememberReturnType")
@Composable
fun OtpUpdateVerificationContent(
    modifier: Modifier = Modifier,
    uiState: OtpUpdateVerificationUiState,
    startCount: Int = 60, // Initial countdown time in seconds
    onCountdownFinish: () -> Unit = {},
    screen: String
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    var countdown by remember { mutableIntStateOf(startCount) }
    var showResend by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    LaunchedEffect(Unit) {
        coroutineScope.launch {
            while (countdown > 0) {
                delay(1000) // Delay for 1 second
                countdown--
            }
            onCountdownFinish() // Notify when countdown finishes
            showResend = true
        }
    }
    val otpState by uiState.otpFlow.collectAsStateWithLifecycle()
    val otpError by uiState.otpErrorFlow.collectAsStateWithLifecycle()
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .padding(horizontal = 45.dp)
            .imePadding()
            .clickable(indication = null,
                interactionSource = remember { MutableInteractionSource() })
            {
                keyboardController?.hide()
            }
    ) {
        Spacer(modifier = Modifier.padding(10.dp))
        if (screen == Constants.ContainerScreens.OTP_VERIFICATION) {
            Text(
                text = stringResource(R.string.please_enter_the_code_that_send_to_your_email_address),
                fontSize = 14.sp,
                color = Black50,
                fontFamily = OpenSans,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.W400,
            )
        } else {
            Text(
                text = "We will send you an one time passcode via this ".plus(uiState.email)
                    .plus(" email address"),
                fontSize = 14.sp,
                color = Black50,
                fontFamily = OpenSans,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.W400,
            )
        }
        Spacer(modifier = Modifier.padding(27.dp))
        Column {
            Row {
                Text(
                    text = stringResource(R.string.enter_otp),
                    color = Black,
                    fontFamily = OpenSans,
                    fontWeight = FontWeight.W400,
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = formatTime(countdown),
                    fontWeight = FontWeight.W400,
                    fontFamily = OpenSans,
                    color = Black,
                    fontSize = 14.sp,
                )
            }
            Spacer(modifier = Modifier.padding(1.dp))
            OtpTextField(
                otpText = otpState,
                onOtpTextChange = { string, boolean ->
                    uiState.onOtpValueChanges(string)
                },
                errorMessage = otpError,
            )
        }
        Spacer(modifier = Modifier.padding(20.dp))
        if (showResend) {
            Text(
                text = stringResource(id = R.string.resend_otp),
                color = Black50,
                fontFamily = OpenSans,
                fontSize = 14.sp,
                fontWeight = FontWeight.W400,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .clickable {
                        uiState.onResendOtpClick()
                        countdown = startCount // Reset the countdown
                        showResend = false // Hide the Resend OTP view
                        coroutineScope.launch {
                            // Start the countdown again
                            while (countdown > 0) {
                                delay(1000) // Delay for 1 second
                                countdown--
                            }
                            onCountdownFinish() // Notify when countdown finishes
                            showResend = true // Show the Resend OTP view
                        }
                    }
            )
        }
    }
}

@SuppressLint("DefaultLocale")
private fun formatTime(seconds: Int): String {
    return String.format("%02dsec", seconds)
}
/*
@Preview
@Composable
fun Preview() {
    val uiState = OtpVerificationUiState()
    OtpVerificationContent(modifier = Modifier, uiState = uiState)

}*/
