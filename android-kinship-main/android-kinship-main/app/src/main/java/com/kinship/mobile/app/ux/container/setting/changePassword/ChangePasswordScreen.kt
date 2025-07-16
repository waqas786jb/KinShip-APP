package com.kinship.mobile.app.ux.container.setting.changePassword
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.kinship.mobile.app.R
import com.kinship.mobile.app.navigation.HandleNavigation
import com.kinship.mobile.app.navigation.scaffold.AppNavBarData
import com.kinship.mobile.app.navigation.scaffold.AppNavBarType
import com.kinship.mobile.app.navigation.scaffold.AppScaffold
import com.kinship.mobile.app.ui.compose.common.BottomButtonComponent
import com.kinship.mobile.app.ui.compose.common.CustomLoader
import com.kinship.mobile.app.ui.compose.common.OutlineTextFieldWithLeadingAndTrailingComponent
import com.kinship.mobile.app.ui.compose.common.TopBarComponent
import com.kinship.mobile.app.ui.networkresult.NetworkResultHandler
import com.kinship.mobile.app.ui.theme.Black50
import com.kinship.mobile.app.ui.theme.OpenSans
import com.kinship.mobile.app.ui.theme.AppThemeColor
import com.kinship.mobile.app.ui.theme.White
import es.dmoral.toasty.Toasty

@Composable
fun ChangePasswordScreen(
    navController: NavController = rememberNavController(),
    viewModel: ChangePasswordViewModel = hiltViewModel()
) {
    val uiState = viewModel.uiState
    val showLoader by uiState.showLoader.collectAsStateWithLifecycle()
    val context = LocalContext.current.applicationContext
    AppScaffold(
        containerColor = White,
        topAppBar = {
            TopBarComponent(
                header = stringResource(id = R.string.change_password),
                isLineVisible = true, isBackVisible = true,
                isTrailingIconVisible = false,
                onClick = {
                    uiState.onBackClick()
                }
            )
        },
        navBarData = AppNavBarData(
            appNavBarType = AppNavBarType.NAV_BAR,
            navBar = {
                BottomButtonComponent(
                    text = stringResource(id = R.string.update), onClick = {
                        uiState.onClickUpdateButton()
                    },
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 20.dp)
                )
            }
        )
    ) {
        if (showLoader) {
            CustomLoader()
        }
        ChangePasswordContent(uiState)
        val apiRefreshTokenResultFlow by uiState.apiRefreshTokenResultFlow.collectAsStateWithLifecycle()
        apiRefreshTokenResultFlow?.let { it1 ->
            NetworkResultHandler(
                networkResult = it1,
                onSuccess = { apiResponse ->
                    apiResponse.data?.let { it2 -> uiState.onRefreshTokenApiSuccess(it2) }
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
fun ChangePasswordContent(uiState: ChangePasswordUiState, modifier: Modifier = Modifier) {
    val currentPassword by uiState.currentPasswordFlow.collectAsStateWithLifecycle()
    val currentPasswordError by uiState.currentPasswordErrorFlow.collectAsStateWithLifecycle()
    val confirmPassword by uiState.confirmPasswordFlow.collectAsStateWithLifecycle()
    val confirmPasswordError by uiState.confirmPasswordErrorFlow.collectAsStateWithLifecycle()
    val newPassword by uiState.newPasswordFlow.collectAsStateWithLifecycle()
    val newPasswordError by uiState.newPasswordErrorFlow.collectAsStateWithLifecycle()
    var currentPasswordVisible by rememberSaveable { mutableStateOf(false) }
    var confirmPasswordVisible by rememberSaveable { mutableStateOf(false) }
    var newPasswordVisible by rememberSaveable { mutableStateOf(false) }
    val keyboardController = LocalSoftwareKeyboardController.current
    val isForgetPassword by uiState.isForgetPassword.collectAsStateWithLifecycle()

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .background(White)
            .padding(horizontal = 20.dp)
            .clickable(indication = null,
                interactionSource = remember { MutableInteractionSource() })
            {
                keyboardController?.hide()
            }
            .imePadding()
            .verticalScroll(rememberScrollState())
    ) {
        Spacer(modifier = Modifier.padding(7.dp))
        Text(
            text = stringResource(R.string.change_password_text),
            fontSize = 15.sp,
            color = Black50,
            fontFamily = OpenSans,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.W400
        )
        Log.d("TAG", "ChangePasswordContent: $newPassword")
        if (isForgetPassword.isEmpty()) {
            Spacer(modifier = Modifier.padding(20.dp))
            OutlineTextFieldWithLeadingAndTrailingComponent(
                isTitleVisible = true,
                isTrailingIconVisible = true,
                title = stringResource(R.string.current_password),
                isLeadingIconVisible = true,
                value = currentPassword,
                errorMessage = currentPasswordError,
                trailingIcon = if (currentPasswordVisible) R.drawable.ic_show_password_key else R.drawable.ic_hide_password_key,
                onValueChange = { uiState.onCurrentPasswordValueChange(it) },
                visualTransformation = if (currentPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                onTogglePasswordVisibility = { currentPasswordVisible = !currentPasswordVisible },
                leadingIcon = R.drawable.ic_password,
                header = stringResource(id = R.string.current_password),
                keyboardOptions = KeyboardOptions(
                    autoCorrectEnabled = true,
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Next
                )
            )
            Text(
                text = stringResource(R.string.forgot_password),
                fontSize = 12.sp,
                color = AppThemeColor,
                modifier = Modifier
                    .padding(
                        top = 20.dp,
                    )
                    .clickable {
                        uiState.forgetPasswordAPICall()
                    }
                    .align(Alignment.End)
            )
        }
        Spacer(modifier = Modifier.padding(20.dp))
        OutlineTextFieldWithLeadingAndTrailingComponent(
            isTitleVisible = true,
            isTrailingIconVisible = true,
            title = stringResource(R.string.new_password),
            isLeadingIconVisible = true,
            value = newPassword,
            errorMessage = newPasswordError,
            trailingIcon = if (newPasswordVisible) R.drawable.ic_show_password_key else R.drawable.ic_hide_password_key,
            onValueChange = { uiState.onNewPasswordValueChange(it) },
            visualTransformation = if (newPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            onTogglePasswordVisibility = { newPasswordVisible = !newPasswordVisible },
            leadingIcon = R.drawable.ic_password,
            header = stringResource(id = R.string.new_password),
            keyboardOptions = KeyboardOptions(
                autoCorrectEnabled = true,
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Next
            )
        )
        Spacer(modifier = Modifier.padding(15.dp))
        OutlineTextFieldWithLeadingAndTrailingComponent(
            isTitleVisible = true,
            isTrailingIconVisible = true,
            title = stringResource(R.string.confirm_password),
            isLeadingIconVisible = true,
            value = confirmPassword,
            errorMessage = confirmPasswordError,
            trailingIcon = if (confirmPasswordVisible) R.drawable.ic_show_password_key else R.drawable.ic_hide_password_key,
            onValueChange = { uiState.onConfirmPasswordValueChange(it) },
            visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            onTogglePasswordVisibility = { confirmPasswordVisible = !confirmPasswordVisible },
            leadingIcon = R.drawable.ic_password,
            header = stringResource(id = R.string.confirm_password),
            keyboardOptions = KeyboardOptions(
                autoCorrectEnabled = true,
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done
            )
        )
    }
}

@Preview
@Composable
fun ChangePasswordContentPreview(modifier: Modifier = Modifier) {
    val uiState = ChangePasswordUiState()
    ChangePasswordContent(uiState)
}