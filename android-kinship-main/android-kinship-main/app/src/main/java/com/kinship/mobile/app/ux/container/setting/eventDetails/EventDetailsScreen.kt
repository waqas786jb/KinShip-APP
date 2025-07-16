package com.kinship.mobile.app.ux.container.setting.eventDetails
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import com.kinship.mobile.app.R
import com.kinship.mobile.app.navigation.HandleNavigation
import com.kinship.mobile.app.navigation.scaffold.AppScaffold
import com.kinship.mobile.app.ui.compose.common.CustomLoader
import com.kinship.mobile.app.ui.compose.common.TopBarComponent
import com.kinship.mobile.app.ui.theme.Black50
import com.kinship.mobile.app.ui.theme.OpenSans
import com.kinship.mobile.app.ui.theme.AppThemeColor
import com.kinship.mobile.app.ui.theme.White
import com.kinship.mobile.app.ux.container.setting.eventDetails.myEvents.MyEventsScreen
import com.kinship.mobile.app.ux.container.setting.eventDetails.myUpcomingEvents.UpcomingEventsScreen
import kotlinx.coroutines.launch

@Composable
fun EventDetailsScreen(
    navController: NavController = rememberNavController(),
    viewModel: EventDetailsViewModel = hiltViewModel(),
    screen: String,
) {
    val uiState = viewModel.uiState
    val isRefreshing by uiState.isAllEventsRefreshing.collectAsStateWithLifecycle()
    AppScaffold(
        containerColor = White,
        topAppBar = {
            TopBarComponent(
                header = stringResource(id = R.string.events_details),
                isLineVisible = false, isBackVisible = true,
                isTrailingIconVisible = false,
                onClick = {
                    uiState.onBackClick()
                }
            )
        }
    ) {
        uiState.sendScreenName(screen)
        EventDetailsContent(uiState)
        val showLoader by uiState.showLoader.collectAsStateWithLifecycle()
        if (showLoader) {
            CustomLoader()
        }
    }
    val lifecycleOwner = LocalLifecycleOwner.current
    val lifecycleState by lifecycleOwner.lifecycle.currentStateFlow.collectAsState()
    LaunchedEffect(lifecycleState) {
        // Do something with your state
        // You may want to use DisposableEffect or other alternatives
        // instead of LaunchedEffect
        when (lifecycleState) {
            Lifecycle.State.RESUMED -> {
                uiState.myEventAndEventApiCall()
            }

            else -> {}
        }
    }
    HandleNavigation(viewModelNav = viewModel, navController = navController)
}
@OptIn(ExperimentalPagerApi::class)
@Composable
fun EventDetailsContent(
    uiState: EventDetailsUiState
) {
    val pagerState = rememberPagerState()
    val coroutineScope = rememberCoroutineScope()
    val tabs = listOf(
        stringResource(id = R.string.my_events),
        stringResource(id = R.string.upcoming_events),

    )
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 15.dp)
    ) {
        TabRow(
            selectedTabIndex = pagerState.currentPage,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[pagerState.currentPage]),
                    color = AppThemeColor,
                    height = 1.5.dp
                )
            },
            containerColor = White
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    text = {
                        Text(
                            title,
                            fontFamily = OpenSans,
                            fontWeight = FontWeight.W600,
                            fontSize = 12.sp
                        )
                    },
                    selected = pagerState.currentPage == index,
                    onClick = {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(index)
                        }
                    },
                    selectedContentColor = AppThemeColor,
                    unselectedContentColor = Black50
                )
            }
        }
        Spacer(modifier = Modifier.padding(5.dp))
        HorizontalPager(
            count = tabs.size,
            state = pagerState
        ) { page ->
            when (page) {
                0 -> {
                    MyEventsScreen(uiState)
                    uiState.myEventClick()
                }

                1 -> {
                    UpcomingEventsScreen(uiState)
                    uiState.upComingClick()
                }
            }
        }
    }
}
