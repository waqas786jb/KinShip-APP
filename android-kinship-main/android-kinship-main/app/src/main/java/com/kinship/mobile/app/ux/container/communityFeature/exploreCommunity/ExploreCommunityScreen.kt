package com.kinship.mobile.app.ux.container.communityFeature.exploreCommunity

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
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
import com.kinship.mobile.app.navigation.scaffold.AppScaffold
import com.kinship.mobile.app.ui.compose.common.CustomLoader
import com.kinship.mobile.app.ui.compose.common.TopBarComponent
import com.kinship.mobile.app.ui.theme.Black
import com.kinship.mobile.app.ui.theme.Black40
import com.kinship.mobile.app.ui.theme.BlackLiteF0
import com.kinship.mobile.app.ui.theme.GrayF5
import com.kinship.mobile.app.ui.theme.OpenSans
import com.kinship.mobile.app.ui.theme.White

@Preview
@Composable
fun ExploreCommunityScreen(
    navController: NavController = rememberNavController(),
    viewModel: ExploreCommunityViewModel = hiltViewModel()
) {
    val uiState = viewModel.uiState

    AppScaffold(
        containerColor = White,
        topAppBar = {
            TopBarComponent(
                header = stringResource(R.string.explore_new_community),
                isBackVisible = true,
                isTrailingIconVisible = true,
                trailingIcon = R.drawable.ic_search,
                isLineVisible = true,
                onTrailingIconClick = {
                    uiState.onClickSearchCommunity()
                },
                onClick = {
                    uiState.onBackClick()
                }
            )
        }

    ) {
        ExploreCommunityContent(uiState)
    }
    val showLoader by uiState.showLoader.collectAsStateWithLifecycle()
    if (showLoader) {
        CustomLoader()
    }
    val lifecycleOwner = LocalLifecycleOwner.current
    val lifecycleState by lifecycleOwner.lifecycle.currentStateFlow.collectAsState()
    LaunchedEffect(lifecycleState) {

        when (lifecycleState) {
            Lifecycle.State.RESUMED -> {
                uiState.exploreCommunityAPICall()
            }

            else -> {}
        }
    }
    HandleNavigation(viewModelNav = viewModel, navController = navController)
}

@Composable
fun ExploreCommunityContent(uiState: ExploreCommunityUiState) {
    val exploreCommunityFeature by uiState.exploreCommunitiesList.collectAsStateWithLifecycle()
    val noDataFound by uiState.noDataFoundText.collectAsStateWithLifecycle()
    Column(
        modifier = Modifier
            .padding(horizontal = 20.dp, vertical = 20.dp)
            .background(White)
            .fillMaxSize()
    ) {
        Surface(
            shape = RoundedCornerShape(8.dp),
            color = GrayF5,
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp),
            ) {
                Image(
                    painter = painterResource(R.drawable.ic_communities_placeholder),
                    contentDescription = null
                )
                Image(painter = painterResource(R.drawable.ic_line), contentDescription = null)
                Spacer(Modifier.padding(10.dp))
                Column(
                    verticalArrangement = Arrangement.Top
                ) {
                    Text(
                        text = stringResource(R.string.explore_communities),
                        color = Black,
                        fontFamily = OpenSans,
                        fontWeight = FontWeight.W600,
                        fontSize = 14.sp
                    )
                    Spacer(Modifier.padding(3.dp))
                    Text(
                        text = stringResource(R.string.explore_and_join_communities_that_match_your_interests_connect_and_engage_with_like_minded_individuals),
                        color = Black,
                        fontFamily = OpenSans,
                        lineHeight = 18.sp,
                        fontWeight = FontWeight.W400,
                        fontSize = 12.sp
                    )
                }
            }
        }
        Spacer(Modifier.padding(10.dp))
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
                        text = stringResource(R.string.no_communities_found),
                        fontSize = 16.sp,
                        maxLines = 1,
                        color = Black,
                        fontFamily = OpenSans,
                    )
                }
            }
        }
        LazyColumn {
            items(exploreCommunityFeature) { item ->
                ExploreNewCommunityItem(item,uiState)
            }
        }
    }
}

@Composable
fun ExploreNewCommunityItem(item: MyCommunitiesResponse, uiState: ExploreCommunityUiState) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
               uiState.navigateToCommunityPost(item.id,item.name)
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
                text = item.name,
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
                text = "${item.members} Members",  // Provide a default value if null
                style = style.copy(color = Black40),
                maxLines = 1,
                fontFamily = OpenSans,
                fontWeight = FontWeight.W600,
                fontSize = 12.sp,
                textAlign = TextAlign.Start,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
    Spacer(Modifier.padding(10.dp))
}


