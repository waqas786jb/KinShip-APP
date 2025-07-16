package com.kinship.mobile.app.ux.container.communityFeature.searchCommunity

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
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
import com.kinship.mobile.app.R
import com.kinship.mobile.app.model.domain.response.communities.MyCommunitiesResponse
import com.kinship.mobile.app.navigation.HandleNavigation
import com.kinship.mobile.app.navigation.scaffold.AppScaffold
import com.kinship.mobile.app.ui.compose.common.CustomLoader
import com.kinship.mobile.app.ui.compose.common.TopBarComponent
import com.kinship.mobile.app.ui.theme.AppThemeColor
import com.kinship.mobile.app.ui.theme.Black
import com.kinship.mobile.app.ui.theme.Black40
import com.kinship.mobile.app.ui.theme.Black50
import com.kinship.mobile.app.ui.theme.BlackLiteF0
import com.kinship.mobile.app.ui.theme.HoneyFlower50
import com.kinship.mobile.app.ui.theme.OpenSans
import com.kinship.mobile.app.ui.theme.White

@Preview
@Composable
fun SearchCommunityScreen(
    navController: NavController = rememberNavController(),
    viewModel: SearchCommunityViewModel = hiltViewModel()
) {
    val uiState = viewModel.uiState
    AppScaffold(
        containerColor = White,
        topAppBar = {
            TopBarComponent(
                header = stringResource(R.string.search_community),
                isBackVisible = true,
                isLineVisible = true,
                onClick = {
                    uiState.onBackClick()
                }
            )
        }

    ) {
        SearchCommunityContent(uiState)
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
                uiState.myCommunitySearchAPICall()
            }
            else -> {}
        }
    }
    HandleNavigation(viewModelNav = viewModel, navController = navController)
}

@Composable
fun SearchCommunityContent(uiState: SearchCommunityUiState) {
    val searchList by uiState.searchList.collectAsStateWithLifecycle()
    val searchMessageFlow by uiState.searchMessageFlow.collectAsStateWithLifecycle()
    val searchItem = searchList.filter { item ->
        item.name.contains(searchMessageFlow, ignoreCase = true)
    }
    val keyboardController = LocalSoftwareKeyboardController.current
    Column(
        modifier = Modifier
            .background(White)
            .clickable(indication = null,
                interactionSource = remember { MutableInteractionSource() }) {
                keyboardController?.hide()
            }
            .padding(horizontal = 20.dp)
            .fillMaxSize()
    ) {
        Spacer(Modifier.padding(10.dp))
        SearchField(onSearchValueChange = {
            uiState.onSearchMessageValueChange(it)
        }, searchMessage = searchMessageFlow)
        Spacer(Modifier.padding(10.dp))
        if (searchMessageFlow.isNotEmpty()){
            if (searchItem.isNotEmpty()){

                LazyColumn {
                    items(searchItem) { item ->
                        SearchItem(item,uiState)
                    }
                }

            }else{
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.no_search_results_found),
                            fontSize = 16.sp,
                            maxLines = 1,
                            color = Black,
                            fontFamily = OpenSans,
                        )
                    }
                }
            }
        }else{
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    Text(
                        text = stringResource(R.string.please_search_messages),
                        fontSize = 16.sp,
                        maxLines = 1,
                        color = Black,
                        fontFamily = OpenSans,
                    )
                }
            }
        }


    }
}

@Composable
fun SearchItem(item: MyCommunitiesResponse, uiState: SearchCommunityUiState) {
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
                text = "${item.members} Members",
                style = style.copy(color = Black40),
                maxLines = 1,
                fontFamily = OpenSans,
                fontWeight = FontWeight.W600,
                fontSize = 12.sp,
                textAlign = TextAlign.Start,
                overflow = TextOverflow.Ellipsis
            )
        }
        if (item.unseenCount > 0) {
            Card(
                colors = CardDefaults.cardColors(containerColor = AppThemeColor),
                shape = RoundedCornerShape(50.dp),
                modifier = Modifier.padding(top = 7.dp)
            ) {
                Text(
                    text = item.unseenCount.toString(),
                    color = Color.White,
                    fontFamily = OpenSans,
                    fontWeight = FontWeight.W600,
                    fontSize = 8.sp,
                    modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
                )
            }
        }
    }
    Spacer(Modifier.padding(10.dp))
}


@Composable
fun SearchField(
    searchMessage: String,
    onSearchValueChange: (String) -> Unit
) {
    Surface(
        shape = RoundedCornerShape(30.dp),
        border = BorderStroke(width = 1.dp, color = HoneyFlower50),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp)
    ) {
        Row(
            modifier = Modifier
                .background(color = White)
        ) {
            OutlinedTextField(
                value = searchMessage,
                onValueChange = onSearchValueChange,
                singleLine = true,
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = White,
                    unfocusedIndicatorColor = White,
                    focusedContainerColor = White,
                    unfocusedContainerColor = White,
                    cursorColor = Black,
                    disabledIndicatorColor = White
                ),
                modifier = Modifier,
                leadingIcon = {
                    Image(
                        painter = painterResource(id = R.drawable.ic_message_search),
                        contentDescription = stringResource(R.string.searchmessage)
                    )
                },
                keyboardOptions = KeyboardOptions(
                    autoCorrectEnabled = true,
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Done
                ),
                placeholder = {
                    Text(
                        text = stringResource(id = R.string.search_message),
                        fontFamily = OpenSans,
                        fontWeight = FontWeight.W400,
                        fontSize = 12.sp,
                        color = Black50,
                    )
                }
            )
        }
    }


}
