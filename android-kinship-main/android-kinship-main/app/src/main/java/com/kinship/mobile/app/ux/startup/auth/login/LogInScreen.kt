package com.kinship.mobile.app.ux.startup.auth.login

import android.widget.Toast
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.kinship.mobile.app.R
import com.kinship.mobile.app.navigation.HandleNavigation
import com.kinship.mobile.app.navigation.scaffold.AppNavBarData
import com.kinship.mobile.app.navigation.scaffold.AppNavBarType
import com.kinship.mobile.app.navigation.scaffold.AppScaffold
import com.kinship.mobile.app.ui.compose.auth.LogInSignInNavText
import com.kinship.mobile.app.ui.compose.auth.WelComeText
import com.kinship.mobile.app.ui.compose.common.BottomButtonComponent
import com.kinship.mobile.app.ui.compose.common.OutlineTextFieldWithLeadingAndTrailingComponent
import com.kinship.mobile.app.ui.networkresult.NetworkResultHandler
import com.kinship.mobile.app.ui.theme.AppThemeColor
import com.kinship.mobile.app.ui.theme.White
import es.dmoral.toasty.Toasty

@Preview
@Composable
fun LogInScreen(
    navController: NavHostController = rememberNavController(),
    viewModel: LogInViewModel = hiltViewModel()

) {
    val uiState = viewModel.uiState
    val systemUiController = rememberSystemUiController()
    DisposableEffect(Unit) {
        systemUiController.setStatusBarColor(color = White)
        systemUiController.setNavigationBarColor(color = White)

        onDispose {
            systemUiController.setStatusBarColor(color = White)
            systemUiController.setNavigationBarColor(color = White)
        }

    }
    AppScaffold(
        containerColor = White,
        navBarData = AppNavBarData(
            appNavBarType = AppNavBarType.NAV_BAR,
            navBar = {
                Row(horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()) {
                    LogInSignInNavText(
                        description = stringResource(R.string.don_t_have_an_account),
                        destinationName = stringResource(id = R.string.register),
                        onClick = {
                             uiState.onRegisterClick()
                        },

                    )
                }

            },

        )
    ) {
        LogInScreenContent(uiState)
         val apiResultFlow by uiState.apiResultFlow.collectAsStateWithLifecycle()
         val context = LocalContext.current
         apiResultFlow?.let {it1->
             NetworkResultHandler(
                 networkResult = it1,
                 onSuccess = { apiResponse ->
                     uiState.onLoginInSuccessfully(apiResponse.data)
                     uiState.clearAllApiResultFlow()
                 }, onError = {
                     Toasty.error(context, it, Toast.LENGTH_SHORT, false).show()
                     uiState.clearAllApiResultFlow()
                 },
                 onUnAuthenticated = {
                     Toasty.warning(context, it, Toast.LENGTH_SHORT, false).show()
                 }
             )
         }

    }
       HandleNavigation(viewModelNav = viewModel, navController = navController)
}

@Composable
fun LogInScreenContent(uiState: LogInUiState) {
    val keyboardController = LocalSoftwareKeyboardController.current
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .background(White)
            .fillMaxSize()
            .padding(horizontal = 20.dp)
            .clickable(indication = null,
                interactionSource = remember { MutableInteractionSource() })
            {
                keyboardController?.hide()
            }
            .verticalScroll(rememberScrollState())
            .imePadding()
    ) {
        Spacer(modifier = Modifier.padding(30.dp))
        WelComeText(
            header = stringResource(id = R.string.kinship),
            title = stringResource(id = R.string.life_together)
        )
        OutLineTextFiled(uiState)
        Text(
            text = stringResource(R.string.forgot_password),
            fontSize = 12.sp,
            color = AppThemeColor,
            modifier = Modifier
                .padding(
                    top = 20.dp,
                )
                .clickable {
                    uiState.onForgetPasswordClick()
                }
                .align(Alignment.End)
        )
        Spacer(modifier = Modifier.padding(40.dp))
        BottomButtonComponent(
            text = stringResource(id = R.string.login),
            onClick = {
                uiState.onLoginClick()
            }
        )

    }
}

@Preview
@Composable
private fun Preview() {
    val uiState = LogInUiState()
    Surface {
        LogInScreenContent(uiState)
    }
}

@Composable
fun OutLineTextFiled(uiState: LogInUiState) {
    Column(
        verticalArrangement = Arrangement.spacedBy(30.dp)
    ) {
        val email by uiState.emailFlow.collectAsStateWithLifecycle()
        val emailError by uiState.emailErrorFlow.collectAsStateWithLifecycle()
        val password by uiState.passwordFlow.collectAsStateWithLifecycle()
        val passwordError by uiState.passwordErrorFlow.collectAsStateWithLifecycle()
        Spacer(modifier = Modifier.padding(40.dp))
        OutlineTextFieldWithLeadingAndTrailingComponent(
            value = email,
            onValueChange = { uiState.onEmailValueChange(it) },
            isTitleVisible = true,
            errorMessage = emailError,
            isLeadingIconVisible = true,
            keyboardOptions = KeyboardOptions(
                autoCorrectEnabled = true,
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Next
            ),
            title = stringResource(id = R.string.email_address),
            header = stringResource(id = R.string.email_address),
            leadingIcon = R.drawable.ic_email,
        )
        var passwordVisible by rememberSaveable { mutableStateOf(false) }
        OutlineTextFieldWithLeadingAndTrailingComponent(
            value = password,
            onValueChange = { uiState.onPasswordValueChange(it) },
            isTitleVisible = true,
            isLeadingIconVisible = true,
            errorMessage = passwordError,
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            onTogglePasswordVisibility = { passwordVisible = !passwordVisible },
            title = stringResource(id = R.string.password),
            isTrailingIconVisible = true,
            keyboardOptions = KeyboardOptions(
                autoCorrectEnabled = true,
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done
            ),
            header = stringResource(id = R.string.password),
            leadingIcon = R.drawable.ic_password,
            trailingIcon = if (passwordVisible) R.drawable.ic_show_password_key else R.drawable.ic_hide_password_key,
        )
    }
}

