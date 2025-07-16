package com.kinship.mobile.app.ux.startup.auth.signUp

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusModifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.kinship.mobile.app.R
import com.kinship.mobile.app.navigation.HandleNavigation
import com.kinship.mobile.app.navigation.scaffold.AppScaffold
import com.kinship.mobile.app.ui.compose.auth.LogInSignInNavText
import com.kinship.mobile.app.ui.compose.auth.TermsAndConditionsText
import com.kinship.mobile.app.ui.compose.auth.WelComeText
import com.kinship.mobile.app.ui.compose.common.BottomButtonComponent
import com.kinship.mobile.app.ui.compose.common.CustomLoader
import com.kinship.mobile.app.ui.compose.common.OutlineTextFieldWithLeadingAndTrailingComponent
import com.kinship.mobile.app.ui.networkresult.NetworkResultHandler
import com.kinship.mobile.app.ui.theme.OpenSans
import com.kinship.mobile.app.ui.theme.AppThemeColor
import com.kinship.mobile.app.ui.theme.White
import es.dmoral.toasty.Toasty

@Composable
fun SignInScreen(
    navController: NavController,
    viewModel: SignUpViewModel = hiltViewModel()
) {
    val uiState = viewModel.uiState
    AppScaffold(
        containerColor = White,
    ) {
        RegisterScreenContent(uiState)

        val context = LocalContext.current
        val apiResultFlow by uiState.apiResultFlow.collectAsStateWithLifecycle()
        apiResultFlow?.let { it1 ->
            NetworkResultHandler(
                networkResult = it1,
                onSuccess = { apiResponse ->
                    Toasty.success(
                        context,
                        stringResource(R.string.otp_sent_your_mail_validation),
                        Toast.LENGTH_SHORT,
                        false
                    ).show()
                    uiState.onSignInSuccessfully(apiResponse.data)
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
    val showLoader by uiState.showLoader.collectAsStateWithLifecycle()
    if (showLoader) {
        CustomLoader()
    }
    HandleNavigation(viewModelNav = viewModel, navController = navController)
}

@Composable
fun RegisterScreenContent(uiState: SignUpUiState) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val termsError by uiState.termsAndConditionErrorFlow.collectAsStateWithLifecycle()
    val context = LocalContext.current
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .background(White)
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
        Spacer(modifier = Modifier.padding(25.dp))
        Text(
            text = stringResource(R.string.create_account),
            color = AppThemeColor,
            fontFamily = OpenSans,
            fontWeight = FontWeight.W400,
            fontSize = 20.sp
        )
        Spacer(modifier = Modifier.padding(15.dp))
        OutlineTextFiled(uiState)
        Spacer(Modifier.padding(10.dp))
        TermsAndConditionsText(
            onTermsAndConditionsClick = {
                uiState.termAndConditionClick()
            },
            errorMessage = termsError,
            onCheckChange = { checked ->
                uiState.onTermsAndConditionErrorChecked(checked)
            })
        Spacer(modifier = Modifier.padding(20.dp))
        BottomButtonComponent(text = stringResource(R.string.register), onClick = {
            uiState.onSignInClick()
        })
        Spacer(modifier = Modifier.padding(40.dp))
        Spacer(modifier = Modifier.weight(1f))
        LogInSignInNavText(
            description = stringResource(R.string.already_have_an_account),
            destinationName = stringResource(id = R.string.login),
            onClick = {
                uiState.onBackClick()
            }
        )
    }
}

@Preview
@Composable
private fun Preview() {
    val uiState = SignUpUiState()
    Surface {
        RegisterScreenContent(uiState)
    }
}

@Composable
fun OutlineTextFiled(uiState: SignUpUiState) {
    Column(
        verticalArrangement = Arrangement.spacedBy(30.dp)
    ) {
        val email by uiState.emailFlow.collectAsStateWithLifecycle()
        val emailError by uiState.emailErrorFlow.collectAsStateWithLifecycle()
        val password by uiState.passwordFlow.collectAsStateWithLifecycle()
        val passwordError by uiState.passwordErrorFlow.collectAsStateWithLifecycle()
        val confirmPassword by uiState.confirmPasswordFlow.collectAsStateWithLifecycle()
        val confirmPasswordError by uiState.confirmPasswordErrorFlow.collectAsStateWithLifecycle()
        var passwordVisible by rememberSaveable { mutableStateOf(false) }
        var confirmPasswordVisible by rememberSaveable { mutableStateOf(false) }
        OutlineTextFieldWithLeadingAndTrailingComponent(
            isTitleVisible = true,
            isLeadingIconVisible = true,
            title = stringResource(id = R.string.email_address),
            value = email,
            errorMessage = emailError,
            keyboardOptions = KeyboardOptions(
                autoCorrectEnabled = true, keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Next
            ),
            modifier = Modifier.fillMaxWidth(),
            onValueChange = { uiState.onEmailValueChange(it) },
            leadingIcon = R.drawable.ic_email,
            header = stringResource(R.string.email_address),
        )
        OutlineTextFieldWithLeadingAndTrailingComponent(
            isTitleVisible = true,
            isTrailingIconVisible = true,
            title = stringResource(R.string.password),
            isLeadingIconVisible = true,
            value = password,
            errorMessage = passwordError,
            keyboardOptions = KeyboardOptions(
                autoCorrectEnabled = true,
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Next
            ),
            trailingIcon = if (passwordVisible) R.drawable.ic_show_password_key else R.drawable.ic_hide_password_key,
            onValueChange = { uiState.onPasswordValueChange(it) },
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            onTogglePasswordVisibility = { passwordVisible = !passwordVisible },
            leadingIcon = R.drawable.ic_password,
            header = stringResource(id = R.string.password),
        )
        OutlineTextFieldWithLeadingAndTrailingComponent(
            isTitleVisible = true,
            isLeadingIconVisible = true,
            isTrailingIconVisible = true,
            errorMessage = confirmPasswordError,
            title = stringResource(id = R.string.confirm_password),
            value = confirmPassword,
            keyboardOptions = KeyboardOptions(
                autoCorrectEnabled = true,
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done
            ),
            onValueChange = {
                uiState.onConfirmPasswordValueChange(it)
            },
            trailingIcon = if (confirmPasswordVisible) R.drawable.ic_show_password_key else R.drawable.ic_hide_password_key,
            visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            onTogglePasswordVisibility = { confirmPasswordVisible = !confirmPasswordVisible },
            leadingIcon = R.drawable.ic_password,
            header = stringResource(R.string.confirm_password),
        )
    }
}













