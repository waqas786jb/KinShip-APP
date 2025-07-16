package com.kinship.mobile.app.ux.main.events

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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import com.kinship.mobile.app.R
import com.kinship.mobile.app.model.domain.response.events.MyEventsData
import com.kinship.mobile.app.navigation.HandleNavigation
import com.kinship.mobile.app.navigation.scaffold.AppScaffold
import com.kinship.mobile.app.ui.compose.common.CustomLoader
import com.kinship.mobile.app.ui.compose.common.TopBarComponent
import com.kinship.mobile.app.ui.theme.AppThemeColor
import com.kinship.mobile.app.ui.theme.Black50
import com.kinship.mobile.app.ui.theme.OpenSans
import com.kinship.mobile.app.ui.theme.White
import com.kinship.mobile.app.ux.main.events.myEventDetails.AllEvent
import com.kinship.mobile.app.ux.main.events.myEventDetails.MyEvent
import com.kinship.mobile.app.ux.main.events.myEventDetails.UpcomingScreen
import kotlinx.coroutines.launch

@Composable
fun EventScreen(
    navController: NavController,
    viewModel: EventsViewModel = hiltViewModel(),
    mainId:String,
    type:Int
) {
    val eventList: ArrayList<MyEventsData> = arrayListOf()
    val uiState = viewModel.uiState
    AppScaffold(
        containerColor = White,
        topAppBar = {
            TopBarComponent(
                header = stringResource(id = R.string.events),
                isBackVisible = false,
                isTrailingIconVisible = true,
                trailingIcon = R.drawable.ic_add_event,
                onTrailingIconClick = {
                    uiState.navigateToCreateEvent()
                }
            )
        }
    ) {
        EventScreenContent(eventList, uiState, mainId = mainId, type = type)
        val showLoader by uiState.showLoader.collectAsStateWithLifecycle()
        if (showLoader) {
            CustomLoader()
        }
    }
    val lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current
    val lifecycleState by lifecycleOwner.lifecycle.currentStateFlow.collectAsState()

    LaunchedEffect(lifecycleState) {
        // Trigger the API call when the lifecycle state is RESUMED
        when (lifecycleState) {
            Lifecycle.State.RESUMED -> {
                // Make the API call to fetch event data or perform related actions
                uiState.myEventAndEventApiCall()
            }
            // No actions needed for other lifecycle states
            else -> {}
        }
    }
    HandleNavigation(viewModelNav = viewModel, navController = navController)
}


@OptIn(ExperimentalPagerApi::class)
@Composable
private fun EventScreenContent(
    list: ArrayList<MyEventsData>,
    uiState: EventsUiState,
    mainId: String,
    type: Int
) {
    val initialPage = if (type == 4) 2 else 0
    val pagerState = rememberPagerState(initialPage = initialPage)
    val coroutineScope = rememberCoroutineScope()
    val tabs = listOf(
        stringResource(R.string.all_events),
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
                    AllEvent(uiState, list, mainId = mainId)
                }

                1 -> {
                    MyEvent(uiState)
                }
                2->{
                    UpcomingScreen(uiState = uiState,mainId)
                }
            }
        }
    }
}


