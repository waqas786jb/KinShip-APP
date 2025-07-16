package com.kinship.mobile.app.ux.container.communityFeature.myCommunities

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.kinship.mobile.app.R
import com.kinship.mobile.app.model.domain.response.communities.MyCommunitiesResponse
import com.kinship.mobile.app.navigation.HandleNavigation
import com.kinship.mobile.app.navigation.scaffold.AppNavBarData
import com.kinship.mobile.app.navigation.scaffold.AppNavBarType
import com.kinship.mobile.app.navigation.scaffold.AppScaffold
import com.kinship.mobile.app.ui.compose.common.BottomButtonComponent
import com.kinship.mobile.app.ui.compose.common.CustomLoader
import com.kinship.mobile.app.ui.compose.common.TopBarComponent
import com.kinship.mobile.app.ui.theme.AppThemeColor
import com.kinship.mobile.app.ui.theme.Black
import com.kinship.mobile.app.ui.theme.Black40
import com.kinship.mobile.app.ui.theme.BlackLiteF0
import com.kinship.mobile.app.ui.theme.OpenSans
import com.kinship.mobile.app.ui.theme.White

@Composable
fun MyCommunitiesScreen(
    navController: NavController = rememberNavController(),
    viewModel: MyCommunitiesViewModel = hiltViewModel()
) {
    val uiState = viewModel.uiState
    AppScaffold(
        containerColor = White,
        navBarData = AppNavBarData(
            appNavBarType = AppNavBarType.NAV_BAR,
            navBar = {
                Column {
                    BottomButtonComponent(
                        text = "Explore New Communities", onClick = {
                            uiState.onClickOfExploreCommunity()
                        },
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 20.dp)
                    )
                    Button(
                        onClick = {
                            uiState.onClickOfNewSuggestion()
                        },
                        border = BorderStroke(width = 1.dp, color = AppThemeColor),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp)
                            .heightIn(50.dp), colors = ButtonDefaults.buttonColors(
                            containerColor = White
                        )
                    ) {
                        Text(
                            text = "Send New Suggestion",
                            textAlign = TextAlign.Center,
                            fontFamily = OpenSans,
                            color = AppThemeColor,
                            fontWeight = FontWeight.W400,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            fontSize = 17.sp
                        )
                    }
                    Spacer(modifier = Modifier.padding(10.dp))

                }

            }
        ),
        topAppBar = {
            TopBarComponent(
                header = stringResource(R.string.my_communities),
                isBackVisible = true,
                isLineVisible = true,
                isTrailingIconVisible = true,
                trailingIcon = R.drawable.ic_search,
                onTrailingIconClick = {
                    uiState.onClickSearchCommunity()
                },
                onClick = {
                    uiState.onBackClick()

                }
            )
        }

    ) {
        MyCommunitiesContact(uiState)
    }
    val showLoader by uiState.showLoader.collectAsStateWithLifecycle()
    if (showLoader) {
        CustomLoader()
    }
    val lifecycleOwner = LocalLifecycleOwner.current
    val lifecycleState by lifecycleOwner.lifecycle.currentStateFlow.collectAsState()

    LaunchedEffect(lifecycleState) {
        // Trigger the API call when the lifecycle state is RESUMED
        when (lifecycleState) {
            Lifecycle.State.RESUMED -> {
                uiState.myCommunityAPICall()
            }

            else -> {}
        }
    }
    HandleNavigation(viewModelNav = viewModel, navController = navController)
}

@Composable
fun MyCommunitiesContact(uiState: MyCommunitiesUiState) {
    val communitiesList by uiState.communitiesList.collectAsStateWithLifecycle()
    val noDataFound by uiState.noDataFoundText.collectAsStateWithLifecycle()
    if (noDataFound) {
        Box(
            Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                Text(
                    text = stringResource(R.string.add_new_community),
                    fontSize = 16.sp,
                    maxLines = 1,
                    color = Black,
                    fontFamily = OpenSans,
                )
            }
        }
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        LazyColumn {
            items(communitiesList) { myCommunities ->
                MyCommunityItem(myCommunitiesResponse = myCommunities, uiState = uiState)
            }
        }
    }
}

@Composable
fun MyCommunityItem(myCommunitiesResponse: MyCommunitiesResponse, uiState: MyCommunitiesUiState) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
        .padding(15.dp)
        .fillMaxWidth()
        .clickable {
            uiState.navigateToCommunityPost(myCommunitiesResponse.id,myCommunitiesResponse.name)
        }) {
        val style = remember {
            TextStyle(
                fontFamily = OpenSans,
                fontWeight = FontWeight.SemiBold,
                lineHeight = 14.sp,
                textAlign = TextAlign.Center,
                color = Black
            )
        }
        Surface(
            color = BlackLiteF0,
            shape = CircleShape,
            modifier = Modifier.size(55.dp)
        ) {
            Image(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(15.dp),
                painter = painterResource(id = R.drawable.ic_community_profile),
                contentDescription = null,
                contentScale = ContentScale.Crop
            )
        }
        Spacer(modifier = Modifier.padding(3.dp))
        Column(
            horizontalAlignment = Alignment.Start,
            modifier = Modifier
                .padding(start = 8.dp)
        ) {
            Text(
                text = myCommunitiesResponse.name,
                style = style,
                fontSize = 14.sp,
                maxLines = 1,
                color = Black,
                fontFamily = OpenSans,
                fontWeight = FontWeight.W600,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(end = 15.dp)
            )
            Spacer(modifier = Modifier.padding(2.dp))
            Text(
                text = "${myCommunitiesResponse.members} Members",  // Provide a default value if null
                style = style.copy(color = Black40),
                maxLines = 1,
                fontFamily = OpenSans,
                fontWeight = FontWeight.W600,
                fontSize = 12.sp,
                textAlign = TextAlign.Start,
                overflow = TextOverflow.Ellipsis
            )
        }

        if (myCommunitiesResponse.unseenCount > 0) {
            Card(
                colors = CardDefaults.cardColors(containerColor = AppThemeColor),
                shape = RoundedCornerShape(50.dp),
                modifier = Modifier.padding(top = 7.dp)
            ) {
                Text(
                    text = "${myCommunitiesResponse.unseenCount}",
                    color = Color.White,
                    fontFamily = OpenSans,
                    fontWeight = FontWeight.W600,
                    fontSize = 8.sp,
                    modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
                )
            }
        }
    }
}


