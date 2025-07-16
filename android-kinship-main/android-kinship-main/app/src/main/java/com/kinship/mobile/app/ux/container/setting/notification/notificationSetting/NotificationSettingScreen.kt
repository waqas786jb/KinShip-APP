package com.kinship.mobile.app.ux.container.setting.notification.notificationSetting

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
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
import com.kinship.mobile.app.navigation.scaffold.AppScaffold
import com.kinship.mobile.app.ui.compose.common.CustomSwitch
import com.kinship.mobile.app.ui.compose.common.TopBarComponent
import com.kinship.mobile.app.ui.networkresult.NetworkResultHandler
import com.kinship.mobile.app.ui.theme.Black50
import com.kinship.mobile.app.ui.theme.Black80
import com.kinship.mobile.app.ui.theme.OpenSans
import com.kinship.mobile.app.ui.theme.White
import es.dmoral.toasty.Toasty

@Preview
@Composable
fun NotificationSettingScreen(
    navController: NavController = rememberNavController(),
    viewModel: NotificationSettingViewModel = hiltViewModel()
) {
    val uiState = viewModel.uiState
    AppScaffold(
        containerColor = White,
        topAppBar = {
            TopBarComponent(
                header = stringResource(id = R.string.notification_setting),
                isLineVisible = true, isBackVisible = true, isTrailingIconVisible = false,
                onClick = {
                    uiState.onBackClick()
                }
            )
        }
    ) {
        NotificationSettingContent(uiState = uiState)
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
fun NotificationSettingContent(modifier: Modifier = Modifier, uiState: NotificationSettingUiState) {
    var allNewPostSwitch by rememberSaveable { mutableStateOf(uiState.allNewPost) }
    var directMessageSwitch by rememberSaveable { mutableStateOf(uiState.directMessage) }
    var newEventSwitch by rememberSaveable { mutableStateOf(uiState.newEvents) }

    Column(
        modifier = modifier
            .background(White)
            .padding(horizontal = 20.dp)
            .imePadding()
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
    ) {
        Spacer(modifier = Modifier.padding(7.dp))
        Text(
            text = stringResource(R.string.push_notification),
            fontSize = 14.sp,
            color = Black50,
            fontFamily = OpenSans,
            textAlign = TextAlign.Start,
            fontWeight = FontWeight.W400,
            modifier = Modifier
                .align(Alignment.Start)
                .fillMaxWidth()
        )
        Spacer(modifier = Modifier.padding(8.dp))

        // All New Post Switch Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Text(
                text = stringResource(id = R.string.all_new_post),
                fontSize = 16.sp,
                color = Black80,
                fontFamily = OpenSans,
                textAlign = TextAlign.Start,
                fontWeight = FontWeight.W600,
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .weight(1f)
            )
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .align(Alignment.CenterVertically)
            ) {
                CustomSwitch(
                    onClick = {
                        allNewPostSwitch = !allNewPostSwitch

                        // Automatically turn on Direct Message and New Event when All New Posts is turned on
                        if (allNewPostSwitch) {
                            directMessageSwitch = true
                            newEventSwitch = true
                        }

                        // Call the API with updated switch values
                        uiState.onAPICall(allNewPostSwitch, newEventSwitch, directMessageSwitch)
                    },
                    switchState = allNewPostSwitch,
                )
            }
        }

        // Direct Message Switch Row (disabled if All New Posts is ON)
        Row(
            modifier = Modifier
                .padding(top = 25.dp)
                .fillMaxWidth()
        ) {
            Text(
                text = stringResource(id = R.string.direct_message),
                fontSize = 16.sp,
                color = Black80,
                fontFamily = OpenSans,
                textAlign = TextAlign.Start,
                fontWeight = FontWeight.W600,
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .weight(1f)
            )
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .align(Alignment.CenterVertically)
            ) {
                CustomSwitch(
                    onClick = {
                        directMessageSwitch = !directMessageSwitch
                        // Call the API with updated switch values
                        uiState.onAPICall(allNewPostSwitch, newEventSwitch, directMessageSwitch)
                    },
                    switchState = directMessageSwitch,
                    enabled = !allNewPostSwitch  // Disable if All New Posts is ON
                )
            }
        }

        // New Event Switch Row (disabled if All New Posts is ON)
        Row(
            modifier = Modifier
                .padding(top = 25.dp)
                .fillMaxWidth()
        ) {
            Text(
                text = stringResource(id = R.string.new_event),
                fontSize = 16.sp,
                color = Black80,
                fontFamily = OpenSans,
                textAlign = TextAlign.Start,
                fontWeight = FontWeight.W600,
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .weight(1f)
            )
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .align(Alignment.CenterVertically)
            ) {
                CustomSwitch(
                    onClick = {
                        newEventSwitch = !newEventSwitch
                        // Call the API with updated switch values
                        uiState.onAPICall(allNewPostSwitch, newEventSwitch, directMessageSwitch)
                    },
                    switchState = newEventSwitch,
                    enabled = !allNewPostSwitch  // Disable if All New Posts is ON
                )
            }
        }
    }
}

