package com.kinship.mobile.app.ux.main.home
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.bumptech.glide.integration.compose.placeholder
import com.kinship.mobile.app.R
import com.kinship.mobile.app.model.domain.response.ApiResponse
import com.kinship.mobile.app.model.domain.response.group.CreateGroup
import com.kinship.mobile.app.navigation.HandleNavigation
import com.kinship.mobile.app.navigation.scaffold.AppScaffold
import com.kinship.mobile.app.ui.networkresult.NetworkResultHandler
import com.kinship.mobile.app.ui.theme.Black
import com.kinship.mobile.app.ui.theme.Black60
import com.kinship.mobile.app.ui.theme.MineShaft
import com.kinship.mobile.app.ui.theme.OpenSans
import com.kinship.mobile.app.ui.theme.AppThemeColor
import com.kinship.mobile.app.ui.theme.GreenLight48
import com.kinship.mobile.app.ui.theme.White
import es.dmoral.toasty.Toasty

@Composable
fun HomeScreenHome(apiResponse: ApiResponse<CreateGroup>, uiState: HomeUiState) {
    AppScaffold(
        containerColor = White,
        topAppBar = {
            HomeTopBar(uiState,apiResponse)
        }
    ) {
        HomeScreenContent(modifier = Modifier, apiResponse, uiState)
    }
}
@Composable
fun HomeScreen(
    navController: NavController = rememberNavController(),
    viewModel: HomeViewModel = hiltViewModel(),

) {
    val uiState = viewModel.uiState
    AppScaffold(
        containerColor = White
    ) {
        val context = LocalContext.current.applicationContext
        val apiResultFlow by uiState.apiResultFlow.collectAsStateWithLifecycle()
        apiResultFlow?.let { it1 ->
            NetworkResultHandler(
                networkResult = it1,
                onSuccess = { apiResponse ->
                    if (apiResponse.data?.isGroupCreated == true) {
                        HomeScreenHome(apiResponse, uiState)
                    } else {
                        FindingYourKinshipContent(modifier = Modifier, uiState)
                    }
                    uiState.onSaveGroupData(apiResponse.data)
                }, onError = {
                    Toasty.error(context, it, Toast.LENGTH_SHORT, true).show()
                    uiState.clearAllApiResultFlow()
                },
                onUnAuthenticated = {
                    Toasty.warning(context, it, Toast.LENGTH_SHORT, true).show()
                    uiState.clearAllApiResultFlow()
                }
            )
        }
    }
    val lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current
    val lifecycleState by lifecycleOwner.lifecycle.currentStateFlow.collectAsState()
    LaunchedEffect(lifecycleState) {
        // Do something with your state
        // You may want to use DisposableEffect or other alternatives
        // instead of LaunchedEffect
        when (lifecycleState) {
            Lifecycle.State.RESUMED -> {
                uiState.groupAPICall()
            }

            else -> {}
        }
    }
    HandleNavigation(viewModelNav = viewModel, navController = navController)
}
@Composable
fun HomeScreenContent(
    modifier: Modifier = Modifier,
    apiResponse: ApiResponse<CreateGroup>,
    uiState: HomeUiState
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(15.dp)
    ) {
        Spacer(modifier = Modifier.padding(top = 30.dp))
        Image(
            painter = painterResource(id = R.drawable.ic_home_screen), contentDescription = null,
            modifier = Modifier
                .size(220.dp)
                .align(Alignment.CenterHorizontally)
        )
        if (uiState.kinshipReason == 1 || uiState.kinshipReason == 2) {
            Text(
                text = stringResource(R.string.connected_you_with_other_moms_moms_to_be),
                color = Black60,
                fontWeight = FontWeight.W400,
                fontFamily = OpenSans,
                fontSize = 14.sp,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        } else {
            Text(
                text = stringResource(R.string.mom),
                color = Black60,
                fontWeight = FontWeight.W400,
                fontFamily = OpenSans,
                fontSize = 14.sp,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }
        Text(
            text = stringResource(R.string.enjoy_your_journey),
            color = Color.Black,
            fontSize = 14.sp,
            fontWeight = FontWeight.W400,
            fontFamily = OpenSans,
            modifier = Modifier.padding(top = 40.dp, start = 20.dp)
        )
        GroupCardContent(apiResponse, uiState)
    }
}
@Preview
@Composable
fun GroupCardContentPreview(modifier: Modifier = Modifier) {
    val uiState=HomeUiState()
   val apiResponse= ApiResponse<CreateGroup>()
    HomeScreenHome(apiResponse,uiState)
}

@Composable
fun FindingYourKinshipContent(modifier: Modifier = Modifier, uiState: HomeUiState) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally, modifier = modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .padding(horizontal = 20.dp)
    ) {
        Spacer(modifier = modifier.padding(top = 150.dp))
        Text(
            text = stringResource(R.string.finding_your_kinship),
            color = AppThemeColor, fontSize = 20.sp,
            fontWeight = FontWeight.W400,
            fontFamily = OpenSans
        )

        Spacer(modifier = modifier.padding(top = 150.dp))
        if (uiState.kinshipReason == 1 || uiState.kinshipReason == 2) {
            Text(
                text = stringResource(R.string.moms_to_be),
                color = Black60, fontSize = 16.sp,
                textAlign = TextAlign.Center, fontWeight = FontWeight.W400,
                fontFamily = OpenSans
            )
        } else {
            Text(
                text = stringResource(R.string.moms),
                color = Black60, fontSize = 16.sp,
                textAlign = TextAlign.Center, fontWeight = FontWeight.W400,
                fontFamily = OpenSans
            )

        }

    }
}
@Composable
fun HomeTopBar(uiState: HomeUiState, apiResponse: ApiResponse<CreateGroup>) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(15.dp)
    ) {
        Column(modifier = Modifier.weight(2f)) {
            Text(
                text = stringResource(R.string.hello),
                color = AppThemeColor,
                fontSize = 13.sp,
                fontFamily = OpenSans,
                fontWeight = FontWeight.W600
            )
            Text(
                text = uiState.userName,
                color = MineShaft,
                fontSize = 15.sp,
                fontFamily = OpenSans,
                fontWeight = FontWeight.W400,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        Box(
            modifier = Modifier
                .weight(1f)
                .padding(end = 2.dp), contentAlignment = Alignment.CenterEnd

        ) {
            BadgedBox( badge = {
                if ((apiResponse.data?.notificationCount ?: 0) > 0) {
                    Badge(
                        containerColor = Color.Red,
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(bottom = 5.dp)
                            .clip(CircleShape)
                            .size(8.dp)
                    ) { }
                }
            }, modifier = Modifier.clickable {
                uiState.onNotificationClick()
            }) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_notification),
                    contentDescription = null,
                    modifier = Modifier.size(22.dp)
                )
            }
        }
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun GroupCardContent(apiResponse: ApiResponse<CreateGroup>, uiState: HomeUiState) {
    Column {
        Surface(
            color = Color.White,
            shape = RoundedCornerShape(26.dp),
            border = BorderStroke(1.dp, color = GreenLight48),
            onClick = {
                uiState.onChatClick()

            },
            shadowElevation = 1.7.dp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp)
                .height(85.dp)

        ) {
            Row(modifier = Modifier.padding(top = 12.dp, bottom = 12.dp, start = 20.dp, end = 20.dp)) {
                Box(
                    contentAlignment = Alignment.Center // Center content inside the Box
                ) {
                    GlideImage(
                        model = apiResponse.data?.image, // Replace with actual image URL
                        contentDescription = null,
                        loading = placeholder(R.drawable.ic_home_kinship), // Placeholder image resource
                        modifier = Modifier
                            .clip(CircleShape)
                            .size(60.dp),
                        contentScale = ContentScale.Crop
                    )
                    if ((apiResponse.data?.kinshipCount ?: 0) > 0) {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = AppThemeColor),
                            modifier = Modifier
                                .offset(x = 5.dp)
                                .align(Alignment.TopEnd) // Align card to the bottom center of the Box
                        ) {
                            Text(
                                text = if ((apiResponse.data?.kinshipCount ?: 0) > 99) "99+" else apiResponse.data?.kinshipCount.toString(),
                                color = Color.White,
                                fontFamily = OpenSans,
                                fontWeight = FontWeight.W600,
                                fontSize = 8.sp,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
                Column(
                    modifier = Modifier
                        .padding(start = 10.dp)
                        .fillMaxHeight()
                        .weight(1f), verticalArrangement = Arrangement.Center,

                ) {
                    Spacer(modifier = Modifier.padding(5.dp))
                    Text(
                            text = apiResponse.data?.groupName ?: "",
                            color = Black,
                            fontFamily = OpenSans,
                            fontWeight = FontWeight.W600,
                            fontSize = 14.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )


                    Row(modifier = Modifier.fillMaxHeight(), verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_multiple_person),
                            contentDescription = null,
                            modifier = Modifier
                        )
                        Text(
                            text = "${apiResponse.data?.count} members",
                            maxLines = 1,
                            color = Color.Black, fontFamily = OpenSans,
                            fontWeight = FontWeight.W400,
                            overflow = TextOverflow.Ellipsis,
                            fontSize = 14.sp,
                            modifier = Modifier
                                .align(alignment = Alignment.CenterVertically)
                                .padding(start = 5.dp)
                        )
                        Spacer(modifier = Modifier.padding(2.dp))
                    }
                }
                /*Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxHeight()
                ) {
                    Text(
                        text = stringResource(R.string.click_here),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.W400,
                        fontFamily = OpenSans,
                        color = Black,
                        modifier = Modifier.clickable {
                            uiState.onChatClick()
                        })
                    Image(
                        painter = painterResource(id = R.drawable.ic_right_arrow),
                        contentDescription = null,
                        colorFilter = ColorFilter.tint(color = Black),
                        modifier = Modifier
                            .size(26.dp)
                            .padding(start = 5.dp)
                    )
                }*/
            }
        }

        Surface(
            color = Color.White,
            shape = RoundedCornerShape(26.dp),
            border = BorderStroke(1.dp, color = GreenLight48),
            onClick = {
                uiState.navigateToMyCommunitiesScreen()

            },
            shadowElevation = 1.7.dp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 15.dp)
                .height(85.dp)

        ) {
            Row(modifier = Modifier.padding(top = 12.dp, bottom = 12.dp, start = 20.dp, end = 20.dp)) {
                Box(
                    contentAlignment = Alignment.Center // Center content inside the Box
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_communities_placeholder), // Replace with actual image URL
                        contentDescription = null,

                        modifier = Modifier
                            .clip(CircleShape)
                            .size(60.dp),
                        contentScale = ContentScale.Crop
                    )
                }
                Column(
                    modifier = Modifier
                        .padding(start = 10.dp)
                        .fillMaxHeight()
                        .weight(1f), verticalArrangement = Arrangement.Center,

                    ) {
                    Spacer(modifier = Modifier.padding(5.dp))

                    Text(
                        text = "My Communities",
                        color = Black,
                        fontFamily = OpenSans,
                        fontWeight = FontWeight.W600,
                        fontSize = 14.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Row(modifier = Modifier.fillMaxHeight(), verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_location),
                            contentDescription = null,
                            colorFilter = ColorFilter.tint(color = Black),
                            modifier = Modifier.size(15.dp)
                        )
                        Text(
                            text = apiResponse.data?.city?:"null",
                            maxLines = 1,
                            color = Color.Black, fontFamily = OpenSans,
                            fontWeight = FontWeight.W400,
                            overflow = TextOverflow.Ellipsis,
                            fontSize = 14.sp,
                            modifier = Modifier
                                .align(alignment = Alignment.CenterVertically)
                                .padding(start = 5.dp)
                        )
                        Spacer(modifier = Modifier.padding(2.dp))
                    }
                }
               /* Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxHeight()
                ) {
                    Text(
                        text = stringResource(R.string.click_here),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.W400,
                        fontFamily = OpenSans,
                        color = Black,
                        modifier = Modifier.clickable {
                            uiState.navigateToMyCommunitiesScreen()
                        })
                    Image(
                        painter = painterResource(id = R.drawable.ic_right_arrow),
                        contentDescription = null,
                        colorFilter = ColorFilter.tint(color = Black),
                        modifier = Modifier
                            .size(26.dp)
                            .padding(start = 5.dp)
                    )
                }*/
            }
        }
        Spacer(modifier = Modifier.padding(50.dp))

    }
}