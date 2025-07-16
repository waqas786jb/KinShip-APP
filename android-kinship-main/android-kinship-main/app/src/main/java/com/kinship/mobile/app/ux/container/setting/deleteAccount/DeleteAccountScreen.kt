package com.kinship.mobile.app.ux.container.setting.deleteAccount

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
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
import com.kinship.mobile.app.ui.compose.common.DialogWithInputAndButton
import com.kinship.mobile.app.ui.compose.common.TopBarComponent
import com.kinship.mobile.app.ui.networkresult.NetworkResultHandler
import com.kinship.mobile.app.ui.theme.Black
import com.kinship.mobile.app.ui.theme.OpenSans
import com.kinship.mobile.app.ui.theme.White
import es.dmoral.toasty.Toasty

@Preview
@Composable
fun DeleteAccountScreen(
    navController: NavController = rememberNavController(),
    viewModel: DeleteAccountViewModel = hiltViewModel()
) {
    val uiState = viewModel.uiState
    var showDialog by remember { mutableStateOf(false) }
    val reason by uiState.reasonFlow.collectAsStateWithLifecycle()
    AppScaffold(
        containerColor = White,
        topAppBar = {
            TopBarComponent(
                header = stringResource(id = R.string.delete_your_account),
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
                    text = stringResource(id = R.string.delete_account), onClick = {
                        showDialog = true
                    },
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 20.dp)
                )
                if (showDialog) {
                    DialogWithInputAndButton(
                        onDismissRequest = { showDialog = false },
                        title = stringResource(id = R.string.reason_delete_account),
                        negativeText = stringResource(id = R.string.cancel),
                        positiveText = stringResource(id = R.string.confirm),
                        value = reason,
                        onValueChange = { uiState.onReasonValueChange(it) },
                        onPositiveClick = {
                            uiState.onClickOfConfirmButton()
                            // uiState.onApiSuccess()

                        }
                    )
                }
            }
        )
    ) {
        DeleteAccountContent()
        val context = LocalContext.current.applicationContext
        val apiResultFlow by uiState.apiResultFlow.collectAsStateWithLifecycle()
        apiResultFlow?.let { it1 ->
            NetworkResultHandler(
                networkResult = it1,
                onSuccess = { apiResponse ->
                    apiResponse.data?.let { it2 ->
                        it2.message.let { it3 ->
                            Toasty.success(
                                context,
                                it3,
                                Toast.LENGTH_SHORT,
                                false
                            ).show()
                        }
                    }
                    showDialog = false
                    uiState.onApiSuccess()
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
fun DeleteAccountContent(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .background(White)
            .padding(horizontal = 20.dp)
            .imePadding()
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
    ) {
        Spacer(modifier = Modifier.padding(5.dp))
        Text(
            text = stringResource(R.string.are_you_sure_delete),
            fontSize = 14.sp,
            color = Black,
            fontFamily = OpenSans,
            textAlign = TextAlign.Start,
            fontWeight = FontWeight.W600,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
        Spacer(modifier = Modifier.padding(20.dp))
        Row {
            Image(painter = painterResource(id = R.drawable.ic_caution), contentDescription = null)
            Column(
                modifier = Modifier
                    .padding(start = 15.dp)
            ) {
                Text(
                    text = stringResource(R.string.delete_account_1),
                    fontSize = 14.sp,
                    color = Black,
                    fontFamily = OpenSans,
                    fontWeight = FontWeight.W600
                )
                Text(
                    text = stringResource(R.string.delete_account_2),
                    fontSize = 12.sp,
                    color = Black,
                    fontFamily = OpenSans,
                    fontWeight = FontWeight.W400,
                    modifier = Modifier.padding(top = 7.dp)
                )
            }
        }
        Row(modifier = Modifier.padding(top = 30.dp)) {
            Image(painter = painterResource(id = R.drawable.ic_timer), contentDescription = null)
            Column(
                modifier = Modifier
                    .padding(start = 15.dp)
            ) {
                Text(
                    text = stringResource(R.string.delete_account_3),
                    fontSize = 14.sp,
                    color = Black,
                    fontFamily = OpenSans,
                    fontWeight = FontWeight.W600
                )
                Text(
                    text = stringResource(R.string.delete_account_4),
                    fontSize = 12.sp,
                    color = Black,
                    fontFamily = OpenSans,
                    fontWeight = FontWeight.W400,
                    modifier = Modifier.padding(top = 7.dp)
                )
            }

        }
    }
}