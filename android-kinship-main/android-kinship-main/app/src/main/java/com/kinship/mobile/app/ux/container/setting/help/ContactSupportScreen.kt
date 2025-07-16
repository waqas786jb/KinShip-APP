package com.kinship.mobile.app.ux.container.setting.help

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
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
import com.kinship.mobile.app.ui.compose.common.ContactSupportStateDialog
import com.kinship.mobile.app.ui.compose.common.CustomLoader
import com.kinship.mobile.app.ui.compose.common.OutlineTextFieldComponent
import com.kinship.mobile.app.ui.compose.common.OutlineTextFieldMultipleLine
import com.kinship.mobile.app.ui.compose.common.TopBarComponent
import com.kinship.mobile.app.ui.theme.White

@Preview
@Composable
fun ContactSupportScreen(
    navController: NavController = rememberNavController(),
    viewModel: ContactSupportViewModel = hiltViewModel(),

    ) {
    val uiState = viewModel.uiState
    val showDialog by uiState.showDialog.collectAsStateWithLifecycle()
    AppScaffold(
        containerColor = White,
        topAppBar = {
            TopBarComponent(
                header = stringResource(R.string.contact_support),
                isLineVisible = true, isBackVisible = true,
                onClick = {
                    uiState.onBackClick()
                }
            )
        },
        navBarData = AppNavBarData(
            appNavBarType = AppNavBarType.NAV_BAR,
            navBar = {
                BottomButtonComponent(
                    text = stringResource(R.string.send), onClick = {
                        uiState.onContactReportSendClick()
                    },
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 20.dp)
                )
            }
        )
    ) {
        ContactSupportScreenContent(uiState)
        val showLoader by uiState.showLoader.collectAsStateWithLifecycle()
        if (showLoader) {
            CustomLoader()
        }
    }
    if (showDialog) {
        ContactSupportStateDialog(
            onDismissRequest = { uiState.onShowDialog(false) },
            description = stringResource(R.string.the_contact_support_request_has_been_send_to_the_support),
            title = stringResource(id = R.string.contact_support),
            onPositiveClick = {
                uiState.onShowDialog(false)
                uiState.onBackClick()
            }
        )
    }
    HandleNavigation(viewModelNav = viewModel, navController = navController)
}

@Composable
fun ContactSupportScreenContent(uiState: ContactSupportUiState) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val reason by uiState.reasonFlow.collectAsStateWithLifecycle()
    val reasonError by uiState.reasonErrorFlow.collectAsStateWithLifecycle()

    val description by uiState.descriptionFlow.collectAsStateWithLifecycle()
    val descriptionError by uiState.descriptionErrorFlow.collectAsStateWithLifecycle()
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(indication = null,
                interactionSource = remember { MutableInteractionSource() })
            {
                keyboardController?.hide()
            }
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp),

        ) {
        Spacer(modifier = Modifier.padding(10.dp))
        OutlineTextFieldComponent(
            value = reason,
            onValueChange = {
                uiState.onReason(it)
            },
            errorMessage = reasonError,
            keyboardOptions = KeyboardOptions(
                autoCorrectEnabled = true,
                keyboardType = KeyboardType.Password
            ),
            isTitleVisible = true,
            title = stringResource(R.string.reason_for_support),
            header = stringResource(R.string.reason_for_support),

            )
        Spacer(modifier = Modifier.padding(15.dp))
        OutlineTextFieldMultipleLine(
            value = description,
            onValueChange = { uiState.ontDescription(it) },
            isHeaderVisible = true,
            errorMessage = descriptionError,
            header = stringResource(id = R.string.type_here),
            title = "Add Comment"
        )

    }
}

