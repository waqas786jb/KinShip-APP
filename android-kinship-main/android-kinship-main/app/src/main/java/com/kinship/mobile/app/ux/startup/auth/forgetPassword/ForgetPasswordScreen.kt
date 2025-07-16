package com.kinship.mobile.app.ux.startup.auth.forgetPassword


import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.kinship.mobile.app.R
import com.kinship.mobile.app.navigation.HandleNavigation
import com.kinship.mobile.app.navigation.scaffold.AppNavBarData
import com.kinship.mobile.app.navigation.scaffold.AppNavBarType
import com.kinship.mobile.app.navigation.scaffold.AppScaffold
import com.kinship.mobile.app.ui.compose.common.BottomButtonComponent
import com.kinship.mobile.app.ui.compose.common.OutlineTextFieldWithLeadingAndTrailingComponent

import com.kinship.mobile.app.ui.compose.common.TopBarComponent
import com.kinship.mobile.app.ui.networkresult.NetworkResultHandler
import com.kinship.mobile.app.ui.theme.Black0530
import com.kinship.mobile.app.ui.theme.OpenSans
import com.kinship.mobile.app.ui.theme.White
import es.dmoral.toasty.Toasty

@Composable
fun ForgetPasswordScreen(
    navController: NavHostController = rememberNavController(),
    viewModel: ForgetPasswordViewModel = hiltViewModel(),
) {
    val uiState = viewModel.uiState
    AppScaffold(
        modifier = Modifier.safeDrawingPadding(),
        containerColor = White,
        topAppBar = {
            TopBarComponent(
                header = stringResource(id = R.string.forget_password),
                isBackVisible = true,
                onClick = {
                    uiState.onBackClick()
                },
                isLineVisible = true,
            )
        },
        navBarData = AppNavBarData(
            appNavBarType = AppNavBarType.NAV_BAR,
            navBar = {
                BottomButtonComponent(
                    text = stringResource(id = R.string.send_verification_link), onClick = {
                        uiState.onForgetPasswordCLick()
                    },
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 20.dp)
                )
            }
        ),
    ) {
        ForgetPasswordContent(uiState)
        val context = LocalContext.current.applicationContext
        val apiForgetPasswordResultFlow by uiState.apiForgetPasswordResultFlow.collectAsStateWithLifecycle()
        apiForgetPasswordResultFlow?.let { it1 ->
            NetworkResultHandler(
                networkResult = it1,
                onSuccess = { apiResponse ->
                    Toasty.success(context, apiResponse.message, Toast.LENGTH_SHORT, false).show()
                    uiState.onBackClick()
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

@Composable
fun ForgetPasswordContent(uiState: ForgetPasswordUiState, modifier: Modifier = Modifier) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val email by uiState.emailFlow.collectAsStateWithLifecycle()
    val emailError by uiState.emailErrorFlow.collectAsStateWithLifecycle()
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .background(White)
            .padding(horizontal = 20.dp)
            .imePadding()
            .clickable {
                keyboardController?.hide()
            }
            .verticalScroll(rememberScrollState())
    ) {
        Spacer(modifier = Modifier.padding(5.dp))
        Text(
            text = stringResource(R.string.enter_your_registered_email_to_reset_your_password),
            fontSize = 15.sp,
            color = Black0530,
            fontFamily = OpenSans,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.W400,
            modifier = Modifier.padding(horizontal = 30.dp),
        )
        Spacer(modifier = Modifier.padding(20.dp))
        OutlineTextFieldWithLeadingAndTrailingComponent(
            value = email, header = stringResource(id = R.string.email_address),
            leadingIcon = R.drawable.ic_email,
            errorMessage = emailError,
            onValueChange = { uiState.onEmailValueChange(it) },
            isLeadingIconVisible = true,
            isTitleVisible = true,
            keyboardOptions = KeyboardOptions(
                autoCorrectEnabled = true,
                keyboardType = KeyboardType.Password
            ),
            title = stringResource(id = R.string.email_address),
        )
    }
}

@Composable
fun Preview() {
    Surface {
        val uiState = ForgetPasswordUiState()
        ForgetPasswordContent(uiState = uiState)
    }
}









