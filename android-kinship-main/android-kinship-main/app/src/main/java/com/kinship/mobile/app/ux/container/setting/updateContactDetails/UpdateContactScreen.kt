package com.kinship.mobile.app.ux.container.setting.updateContactDetails

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
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
import com.kinship.mobile.app.ui.compose.common.OutlineTextFieldWithLeadingAndTrailingComponent
import com.kinship.mobile.app.ui.compose.common.TopBarComponent
import com.kinship.mobile.app.ui.compose.countryCode.CountryCodePickerComponent
import com.kinship.mobile.app.ui.networkresult.NetworkResultHandler
import com.kinship.mobile.app.ui.theme.White
import es.dmoral.toasty.Toasty

@Preview
@Composable
fun UpdateContactScreen(
    navController: NavController = rememberNavController(),
    viewModel: UpdateContactViewModel = hiltViewModel()
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val uiState = viewModel.uiState
    AppScaffold(
        modifier = Modifier.clickable(indication = null,
            interactionSource = remember { MutableInteractionSource() })
        {
            keyboardController?.hide()
        },
        containerColor = White,
        topAppBar = {
            TopBarComponent(
                header = stringResource(id = R.string.update_contact_details),
                isLineVisible = true, isBackVisible = true, isTrailingIconVisible = false,
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
                        uiState.onClickOfUpdateButton()
                    },
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 20.dp)
                )
            }
        )
    ) {
        UpdateContactContent(uiState)
        val context = LocalContext.current.applicationContext
        val apiResultFlow by uiState.apiResultFlow.collectAsStateWithLifecycle()
        apiResultFlow?.let { it1 ->
            NetworkResultHandler(
                networkResult = it1,
                onSuccess = { apiResponse ->
                    Toasty.success(
                        context,
                        stringResource(id = R.string.update_success),
                        Toast.LENGTH_SHORT
                    ).show()
                    apiResponse.data?.let { it2 -> uiState.onApiSuccess(it2) }
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
fun UpdateContactContent(uiState: UpdateContactUiState, modifier: Modifier = Modifier) {
    val phoneNumber by uiState.phoneNumberFlow.collectAsStateWithLifecycle()
    val phoneNumberError by uiState.phoneNumberErrorFlow.collectAsStateWithLifecycle()
    val email by uiState.emailFlow.collectAsStateWithLifecycle()
    val emailError by uiState.emailErrorFlow.collectAsStateWithLifecycle()
    val keyboardController = LocalSoftwareKeyboardController.current
    Column(
        modifier = modifier
            .background(White)
            .padding(horizontal = 20.dp)
            .imePadding()
            .clickable(indication = null,
                interactionSource = remember { MutableInteractionSource() })
            {
                keyboardController?.hide()
            }
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
    ) {
        Spacer(modifier = Modifier.padding(8.dp))
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
        Spacer(modifier = Modifier.padding(15.dp))
        CountryCodePickerComponent(
            value = phoneNumber,
            onValueChange = { newValue ->
                val filteredValue = newValue.filter { it.isDigit() }
                uiState.onPhoneNumberValueChange(filteredValue)
            },
            isLeadingIconVisible = true,
            isHeaderVisible = true,
            errorMessage = phoneNumberError,
            title = stringResource(id = R.string.phone_number),
            isTrailingIconVisible = true,
            keyboardOptions = KeyboardOptions(
                autoCorrectEnabled = true,
                keyboardType = KeyboardType.Number
            ),
            header = stringResource(id = R.string.phone_number),
            leadingIcon = R.drawable.ic_call,
            setCountryCode = uiState.countryCode
        )
    }
}