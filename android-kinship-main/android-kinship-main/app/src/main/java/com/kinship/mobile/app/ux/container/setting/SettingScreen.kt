package com.kinship.mobile.app.ux.container.setting

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.kinship.mobile.app.R
import com.kinship.mobile.app.data.source.local.TempData
import com.kinship.mobile.app.navigation.HandleNavigation
import com.kinship.mobile.app.navigation.scaffold.AppScaffold
import com.kinship.mobile.app.ui.compose.common.CustomLoader
import com.kinship.mobile.app.ui.compose.common.DialogLogout
import com.kinship.mobile.app.ui.compose.common.TopBarComponent
import com.kinship.mobile.app.ui.networkresult.NetworkResultHandler
import com.kinship.mobile.app.ui.theme.Black80
import com.kinship.mobile.app.ui.theme.OpenSans
import com.kinship.mobile.app.ui.theme.AppThemeColor
import com.kinship.mobile.app.ui.theme.White
import es.dmoral.toasty.Toasty

@Preview
@Composable
fun SettingScreen(
    navController: NavController = rememberNavController(),
    viewModel: SettingViewModel = hiltViewModel()
) {
    val uiState = viewModel.uiState
    AppScaffold(
        containerColor = White,
        topAppBar = {
            TopBarComponent(
                header = stringResource(id = R.string.settings),
                isLineVisible = true, isBackVisible = false
            )
        },
        navBarData = null
    ) {
        val showLoader by uiState.showLoader.collectAsStateWithLifecycle()
        if (showLoader) {
            CustomLoader()
        }
        SettingScreenContent(uiState = uiState)
        val context = LocalContext.current
        val apiLogoutResultFlow by uiState.apiLogoutResultFlow.collectAsStateWithLifecycle()
        apiLogoutResultFlow?.let {
            NetworkResultHandler(
                networkResult = it,
                onSuccess = { apiResponse ->
                    Toasty.success(context, apiResponse.message, Toast.LENGTH_SHORT, false).show()
                    uiState.clearAllPrefData()
                    uiState.clearAllApiResultFlow()

                }, onError = { it1 ->
                    Toasty.error(context, it1, Toast.LENGTH_SHORT, false).show()
                    uiState.clearAllApiResultFlow()
                             },
                onUnAuthenticated = { it1 ->
                    Toasty.warning(context, it1, Toast.LENGTH_SHORT, false).show()
                    uiState.clearAllApiResultFlow()
                }
            )
        }
        val lifecycleOwner = LocalLifecycleOwner.current
        val lifecycleState by lifecycleOwner.lifecycle.currentStateFlow.collectAsState()
        LaunchedEffect(lifecycleState) {
            // Do something with your state
            // You may want to use DisposableEffect or other alternatives
            // instead of LaunchedEffect
            when (lifecycleState) {
                Lifecycle.State.RESUMED -> {
                    uiState.onGetDataFromPref()
                }
                else -> {}
            }
        }

    }
    HandleNavigation(viewModelNav = viewModel, navController = navController)
}

@Composable
private fun SettingScreenContent(uiState: SettingUiState) {
    Box(
        modifier = Modifier.fillMaxSize(),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 10.dp)
        ) {
            Spacer(modifier = Modifier.padding(20.dp))
            ProfileHeader(uiState)
            Spacer(modifier = Modifier.padding(top = 25.dp))
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .weight(weight = 1f, fill = false)
            ) {
                OptionsContent(items = TempData.settingScreenListingData, uiState)
                // SettingScreenListItem(list = TempData.settingScreenListingData, uiState)
            }
            Spacer(
                modifier = Modifier
                    .padding(top = 25.dp)
            )
        }
    }


}

@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun ProfileHeader(uiState: SettingUiState) {
    val pickedImg by uiState.profilePicFlow.collectAsStateWithLifecycle()
    val firstLastName by uiState.nameFlow.collectAsStateWithLifecycle()
    val email by uiState.emailFlow.collectAsStateWithLifecycle()
    Surface(
        shape = RoundedCornerShape(40.dp),
        color = AppThemeColor,
        modifier = Modifier
            .height(80.dp)
            .fillMaxWidth()
            .padding(horizontal = 10.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(10.dp)
        ) {
            AsyncImage(
                //replace with image from server
                model = pickedImg, contentDescription = null,
                placeholder = painterResource(id = R.drawable.ic_placeholder),
                error = painterResource(id = R.drawable.ic_placeholder),
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .clip(CircleShape)
                    .size(60.dp)
                    .align(Alignment.CenterVertically),
            )
            Column(
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .padding(start = 25.dp)
                    .weight(1f)
            ) {
                Text(
                    text = firstLastName, color = White, fontFamily = OpenSans, fontSize = 18.sp,
                    fontWeight = FontWeight.W700, overflow = TextOverflow.Ellipsis, maxLines = 1
                )
                Text(
                    text = email, color = White, fontFamily = OpenSans, fontSize = 12.sp,
                    fontWeight = FontWeight.W400, modifier = Modifier.padding(top = 1.dp)
                )
            }
        }
    }
}

@Composable
private fun OptionsContent(
    items: List<TempData.SettingData>,
    uiState: SettingUiState,
    modifier: Modifier = Modifier
) {
    var showDialog by remember {
        mutableStateOf(false)
    }
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.Start,
        modifier = modifier
    ) {
        items.forEach { index ->
            SettingItemRow(settingOptions = index, onClick = {
                when (index.title) {
                    R.string.edit_profile -> {
                        uiState.onClickEditProfileButton()

                    }

                    R.string.notification_setting -> {
                        uiState.onClickNotificationSettingButton()
                    }

                    R.string.change_password -> {
                        uiState.onClickChangePasswordButton()
                    }

                    R.string.update_contact_details -> {
                        uiState.onClickUpdateContactDetailsButton()

                    }

                    R.string.events_details -> {
                        uiState.onClickEventDetailsButton()

                    }

                    R.string.help -> {
                        uiState.onClickHelpButton()

                    }

                    R.string.leave_your_kinship -> {
                        uiState.onClickLeaveKinshipButton()

                    }

                    R.string.delete_account -> {
                        uiState.onClickDeleteAccountButton()

                    }

                    R.string.terms_conditions -> {
                        uiState.onTermConditionClick()

                    }
                    R.string.logout -> {
                        showDialog = true

                    }
                }

            })
        }
    }
    if (showDialog) {
        DialogLogout(
            onDismissRequest = { showDialog = false },
            title = stringResource(id = R.string.kinship),
            description = stringResource(R.string.are_you_sure_you_want_to_logout),
            negativeText = stringResource(R.string.cancel),
            positiveText = stringResource(id = R.string.logout),
            onPositiveClick = {
                uiState.onClickLogoutButton()
                showDialog = false
            },
        )
    }
}

@Composable
private fun SettingItemRow(
    settingOptions: TempData.SettingData,
    onClick: () -> Unit,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .padding(15.dp)
            .clickable {
                onClick()
            }
    ) {
        Image(
            painter = painterResource(id = settingOptions.icon), contentDescription = null,
            modifier = Modifier
                .size(25.dp), contentScale = ContentScale.Crop
        )
        Text(
            text = stringResource(id = settingOptions.title),
            fontSize = 16.sp,
            fontFamily = OpenSans,
            fontWeight = FontWeight.W600,
            color = Black80,
            modifier = Modifier
                .weight(1f)
                .padding(start = 15.dp),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        if (settingOptions.isArrowVisible) {
            Image(
                painter = painterResource(id = R.drawable.ic_arrow_right),
                contentDescription = null,
                modifier = Modifier.size(12.dp)
            )
        }
    }
}

