@file:Suppress("DEPRECATION")

package com.kinship.mobile.app.ux.container.setting.notification.notificationListing

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
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
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import coil.compose.AsyncImage
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.SwipeRefreshIndicator
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.kinship.mobile.app.R
import com.kinship.mobile.app.data.source.Constants
import com.kinship.mobile.app.model.domain.response.notification.NotificationListResponse
import com.kinship.mobile.app.navigation.HandleNavigation
import com.kinship.mobile.app.navigation.scaffold.AppScaffold
import com.kinship.mobile.app.ui.compose.common.CustomLoader
import com.kinship.mobile.app.ui.compose.common.TopBarComponent
import com.kinship.mobile.app.ui.theme.AppThemeColor
import com.kinship.mobile.app.ui.theme.Black
import com.kinship.mobile.app.ui.theme.Black50
import com.kinship.mobile.app.ui.theme.HoneyFlower50
import com.kinship.mobile.app.ui.theme.OpenSans
import com.kinship.mobile.app.ui.theme.White
import com.kinship.mobile.app.utils.AppUtils

@Preview
@Composable
fun NotificationListingScreen(
    navController: NavController = rememberNavController(),
    viewModel: NotificationListingViewModel = hiltViewModel()
) {
    val uiState = viewModel.uiState
    val isRefreshing by uiState.isAllEventsRefreshing.collectAsStateWithLifecycle()
    BackHandler(onBack = {
       uiState.onBackClick()
    })
    AppScaffold(
        containerColor = White,
        topAppBar = {
            TopBarComponent(
                header = stringResource(id = R.string.notifications),
                isLineVisible = true, isBackVisible = true,
                isTrailingIconVisible = false,
                onClick = { uiState.onBackClick() }
            )
        },
        navBarData = null
    ) {
        val showLoader by uiState.showLoader.collectAsStateWithLifecycle()
        if (showLoader) {
            CustomLoader()
        }

        SwipeRefresh(
            state = rememberSwipeRefreshState(isRefreshing),
            onRefresh = {
                uiState.notificationListingAPICall() // Ensure this method refreshes the event list
            },
            indicator = { state, trigger ->
                SwipeRefreshIndicator(
                    state = state,
                    refreshTriggerDistance = trigger,
                    // Primary color for the loader
                    contentColor = AppThemeColor, // Change this to your desired color
                    // Optional: Background of the indicator can also be customized if needed
                    backgroundColor = White // Optional
                )
            }
        ) {

            NotificationListingScreenContent(uiState = uiState)
        }

    }
    val lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current
    val context = LocalContext.current
    val lifecycleState by lifecycleOwner.lifecycle.currentStateFlow.collectAsState()
    LaunchedEffect(lifecycleState) {
        when (lifecycleState) {
            Lifecycle.State.RESUMED -> {
                uiState.initSocketListener(context)
            }
            else -> {}
        }
    }
    HandleNavigation(viewModelNav = viewModel, navController = navController)
}

@Composable
private fun NotificationListingScreenContent(uiState: NotificationListingUiState) {
    //val notificationList by uiState.notificationList.collectAsStateWithLifecycle()
    val notificationList = uiState.notificationList.collectAsLazyPagingItems()
    val lazyColumnListState = rememberLazyListState()
    val isLoading by uiState.isLoading.collectAsStateWithLifecycle()
    val shouldLoadMore = remember {
        derivedStateOf {
            val layoutInfo = lazyColumnListState.layoutInfo
            val lastVisibleItemIndex = layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
            val totalItemsCount = layoutInfo.totalItemsCount
            lastVisibleItemIndex >= totalItemsCount - 1 && totalItemsCount > 0
        }
    }
    LaunchedEffect(shouldLoadMore.value) {
        if (shouldLoadMore.value && !isLoading) {
            uiState.onLoadNextPage()
        }
    }
    val noDataFound by uiState.noDataFound.collectAsStateWithLifecycle()
    if (noDataFound) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(15.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(id = R.string.no_notification_found),
                textAlign = TextAlign.Center,
                fontSize = 16.sp,
                maxLines = 1,
                color = Black,
            )
        }
    }
    notificationList.loadState.refresh.apply {
        when (this) {
            is LoadState.Error -> {
                Box(
                    Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        Text(
                            text = stringResource(id = R.string.something_went_wrong),
                            fontSize = 16.sp,
                            maxLines = 1,
                            color = Black,
                            fontFamily = OpenSans,
                        )
                        Text(
                            modifier = Modifier.clickable {
                                notificationList.retry()
                            },
                            text = stringResource(id = R.string.tap_here_to_refresh_it),
                            fontSize = 16.sp,
                            maxLines = 1,
                            color = Black,
                            fontFamily = OpenSans,
                        )
                    }
                }
            }

            is LoadState.Loading -> {
                CustomLoader()
            }

            is LoadState.NotLoading -> {
                if (notificationList.itemCount == 0) {
                    Box(
                        Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(5.dp)
                        ) {
                            Text(
                                text = stringResource(id = R.string.no_notification_found),
                                fontSize = 16.sp,
                                maxLines = 1,
                                color = Black,
                                fontFamily = OpenSans,
                            )
                        }
                    }

                } else {
                    LazyColumn(state = lazyColumnListState)
                    {
                        items(notificationList.itemCount) { index ->
                            notificationList[index]?.let { notification ->
                                NotificationListItem(notification, uiState = uiState)
                            }
                        }
                        when (notificationList.loadState.append) {
                            is LoadState.Error -> {
                                item {
                                    Box(
                                        Modifier.fillMaxSize(),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Column(
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                            verticalArrangement = Arrangement.spacedBy(5.dp)
                                        ) {
                                            Text(
                                                text = stringResource(id = R.string.something_went_wrong),
                                                fontSize = 16.sp,
                                                maxLines = 1,
                                                color = Black,
                                                fontFamily = OpenSans,
                                            )
                                            Text(
                                                modifier = Modifier.clickable {
                                                    notificationList.retry()
                                                },
                                                text = stringResource(id = R.string.tap_here_to_refresh_it),
                                                fontSize = 16.sp,
                                                maxLines = 1,
                                                color = Black,
                                                fontFamily = OpenSans,
                                            )
                                        }
                                    }
                                }
                            }

                            LoadState.Loading -> {
                                item {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 16.dp),
                                        contentAlignment = Alignment.TopCenter
                                    ) {
                                        CircularProgressIndicator(color = HoneyFlower50)
                                    }
                                }
                            }

                            is LoadState.NotLoading -> Unit
                        }

                    }

                }
            }
        }
    }
}

@Composable
fun NotificationListItem(item: NotificationListResponse, uiState: NotificationListingUiState) {
    val context = LocalContext.current
    Row(modifier = Modifier
        .padding(15.dp)
        .clickable {
            uiState.navigateToNotificationItem(item)
        }) {
        Surface(
            modifier = Modifier
                .align(Alignment.CenterVertically)
        ) {
            AsyncImage(
                modifier = Modifier
                    .clip(CircleShape)
                    .size(38.dp),
                model = item.profileImage, contentDescription = null,
                placeholder = painterResource(R.drawable.ic_placeholder),
                error = painterResource(R.drawable.ic_placeholder),
                contentScale = ContentScale.Crop
            )
        }
        Column(
            verticalArrangement = Arrangement.spacedBy(0.dp),
            modifier = Modifier
                .padding(start = 10.dp)
                .weight(1f)
        ) {
            Text(
                text = item.firstName.plus(" ").plus(item.lastName),
                color = Black50,
                fontFamily = OpenSans,
                fontWeight = FontWeight.W600,
                fontSize = 12.sp,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1
            )
            Text(
                text = item.message ?: "",
                color = Color.Black,
                fontFamily = OpenSans,
                fontWeight = FontWeight.W600,
                fontSize = 13.sp,
            )
        }
        Text(
            text = AppUtils.getDateLabel(item.createdAt ?: 0L, context),
            color = Color.Black,
            fontFamily = OpenSans,
            fontWeight = FontWeight.W600,
            fontSize = 13.sp,
            overflow = TextOverflow.Ellipsis,
            maxLines = 1,
            modifier = Modifier
                .align(Alignment.Bottom)
                .padding(start = 7.dp)
        )
    }
}