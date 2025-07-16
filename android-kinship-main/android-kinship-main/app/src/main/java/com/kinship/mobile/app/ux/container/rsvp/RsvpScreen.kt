package com.kinship.mobile.app.ux.container.rsvp
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.kinship.mobile.app.R
import com.kinship.mobile.app.model.domain.response.evenrName.EventNameData
import com.kinship.mobile.app.navigation.HandleNavigation
import com.kinship.mobile.app.navigation.scaffold.AppScaffold
import com.kinship.mobile.app.ui.compose.common.CustomLoader
import com.kinship.mobile.app.ui.compose.common.TopBarComponent
import com.kinship.mobile.app.ui.compose.common.message.RsvpDisplayComponent
import com.kinship.mobile.app.ui.theme.Black
import com.kinship.mobile.app.ui.theme.Black50
import com.kinship.mobile.app.ui.theme.OpenSans
import com.kinship.mobile.app.ui.theme.AppThemeColor
import com.kinship.mobile.app.ui.theme.White

@Composable
fun RsvpScreen(
    navController: NavHostController,
    viewModel: RsvpViewModel = hiltViewModel(),
    eventId:String,

    ) {
    val uiState = viewModel.uiState
    AppScaffold(
        containerColor = White,
        topAppBar = {
            TopBarComponent(
                header = stringResource(id = R.string.rsvp),
                isBackVisible = true,
                onClick = {
                    uiState.onBackClick()
                }
            )
        },
        navBarData = null
    ) {
       uiState.eventId(eventId)
      //  uiState.screen(screen)
        RsvpContent(uiState)
    }
    HandleNavigation(viewModelNav = viewModel, navController = navController)
}
@Composable
fun RsvpContent(uiState: RsvpUiState) {
    val showRSVPYesText by uiState.showRSVPYesFoundText.collectAsStateWithLifecycle()
    val showRSVPNoText by uiState.showRSVPNoFoundText.collectAsStateWithLifecycle()
    val showRSVPMaybeText by uiState.showRSVPMaybeFoundText.collectAsStateWithLifecycle()
    val eventNameYesList by uiState.apiEventNameYesList.collectAsStateWithLifecycle()
    val eventNameNoList by uiState.apiEventNameNoList.collectAsStateWithLifecycle()
    val eventNameMaybeList by uiState.apiEventNameMaybeList.collectAsStateWithLifecycle()
    var tabIndex by remember { mutableIntStateOf(0) }
    val tabs = listOf(
        stringResource(id = R.string.yes),
        stringResource(id = R.string.no),
        stringResource(R.string.maybe)
    )
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 15.dp)
    ) {

        TabRow(
            selectedTabIndex = tabIndex,
            indicator = { tabPositions ->
                if (tabIndex < tabPositions.size) {
                    TabRowDefaults.SecondaryIndicator(
                        modifier = Modifier.tabIndicatorOffset(tabPositions[tabIndex]),
                        color = AppThemeColor,
                        height = 1.5.dp
                    )
                }
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
                    selected = tabIndex == index,
                    onClick = { tabIndex = index },
                    selectedContentColor = AppThemeColor,
                    unselectedContentColor = Black50
                )
            }
        }
        Spacer(modifier = Modifier.padding(5.dp))
        when (tabIndex) {
            0 -> {
                uiState.onYesClick()
                if (showRSVPYesText) {
                    YesRSVPEmptyListTextShow()
                }
                LazyColumn(verticalArrangement = Arrangement.spacedBy(15.dp)) {
                    items(eventNameYesList) { item ->
                        YesRsvpItem(item)
                    }
                }
            }

            1 -> {
                uiState.onNoClick()
                if (showRSVPNoText) {
                    NoRSVPEmptyListTextShow()
                }
                LazyColumn(verticalArrangement = Arrangement.spacedBy(15.dp)) {
                    items(eventNameNoList) { item ->
                        YesRsvpItem(item)
                    }
                }
            }

            2 -> {
                uiState.onMaybeClick()
                if (showRSVPMaybeText) {
                    MaybeRSVPEmptyListTextShow()
                }
                LazyColumn(verticalArrangement = Arrangement.spacedBy(15.dp)) {
                    items(eventNameMaybeList) { item ->
                        YesRsvpItem(item)
                    }
                }
            }
        }
        val showLoader by uiState.showLoader.collectAsStateWithLifecycle()
        if (showLoader) {
            CustomLoader()
        }
    }

}
@Composable
fun YesRsvpItem(item: EventNameData) {
    Spacer(modifier = Modifier.padding(5.dp))
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
    ) {
        RsvpDisplayComponent(
            profile = item.profileImage,
            username = item.firstName.plus(" ")
                .plus(item.lastName),
            modifier = Modifier.weight(1f)
        )
    }
}
@Composable
fun YesRSVPEmptyListTextShow() {
    Box(
        Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            Text(
                text = stringResource(R.string.no_attendees_confirmed),
                fontSize = 16.sp,
                maxLines = 1,
                color = Black,
                fontFamily = OpenSans,
            )
        }
    }
}

@Composable
fun NoRSVPEmptyListTextShow() {
    Box(
        Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            Text(
                text = stringResource(R.string.no_declines_received),
                fontSize = 16.sp,
                maxLines = 1,
                color = Black,
                fontFamily = OpenSans,
            )
        }
    }
}

@Composable
fun MaybeRSVPEmptyListTextShow() {
    Box(
        Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            Text(
                text = stringResource(R.string.no_responses_yet),
                fontSize = 16.sp,
                maxLines = 1,
                color = Black,
                fontFamily = OpenSans,
            )
        }
    }
}









