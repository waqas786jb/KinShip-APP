@file:Suppress("DEPRECATION")

package com.kinship.mobile.app.ux.main.message

import android.util.Log
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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.SwipeRefreshIndicator
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.kinship.mobile.app.R
import com.kinship.mobile.app.model.domain.response.chat.userGroup.MessageTabResponse
import com.kinship.mobile.app.navigation.HandleNavigation
import com.kinship.mobile.app.navigation.scaffold.AppScaffold
import com.kinship.mobile.app.ui.compose.common.CustomLoader
import com.kinship.mobile.app.ui.compose.common.TopBarComponent
import com.kinship.mobile.app.ui.theme.AppThemeColor
import com.kinship.mobile.app.ui.theme.Black
import com.kinship.mobile.app.ui.theme.Black40
import com.kinship.mobile.app.ui.theme.HoneyFlower50
import com.kinship.mobile.app.ui.theme.OpenSans
import com.kinship.mobile.app.ui.theme.White

@Preview
@Composable
fun MessageScreen(
    navController: NavController = rememberNavController(),
    viewModel: MessageViewModel = hiltViewModel()
) {
    val uiState = viewModel.uiState
    val isRefreshing by uiState.isAllEventsRefreshing.collectAsStateWithLifecycle()
    if (uiState == null) {
        Log.e("MessageScreen", "UI state is null")
        return
    }
    AppScaffold(
        containerColor = White,
        topAppBar = {
            TopBarComponent(
                header = stringResource(id = R.string.direct_messages),
                isLineVisible = true,
                isBackVisible = false,
                isTrailingIconVisible = true,
                trailingIcon = R.drawable.ic_new_message,
                onTrailingIconClick = {
                    uiState.onNavigateToNewMessage()
                }
            )
        }
    ) {
        val showLoader by uiState.showLoader.collectAsStateWithLifecycle()
        if (showLoader) {
            CustomLoader()
        }
        SwipeRefresh(
            state = rememberSwipeRefreshState(isRefreshing),
            onRefresh = {
                uiState.messageListAPICall() // Ensure this method refreshes the event list
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
            MessageScreenContent(uiState)
        }

    }
    Log.d("TAG", "MessageScreen: ${uiState.apiReload}")
    val lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current
    val lifecycleState by lifecycleOwner.lifecycle.currentStateFlow.collectAsState()

    LaunchedEffect(lifecycleState) {
        when (lifecycleState) {
            Lifecycle.State.RESUMED -> {
                uiState.messageListAPICall()
            }

            else -> {}
        }
    }
    HandleNavigation(viewModelNav = viewModel, navController = navController)
}

@Composable
private fun MessageScreenContent(uiState: MessageUiState) {
    val messageList by uiState.apiUserMessageList.collectAsStateWithLifecycle()
    val noChatAvailable by uiState.noChatAvailable.collectAsStateWithLifecycle()
    if (messageList == null) {
        Log.e("MessageScreenContent", "Message list is null")
        return
    }
    val isLoading by uiState.isLoading.collectAsStateWithLifecycle()
    val lazyColumnListState = rememberLazyListState()
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
            uiState.messageListAPICall()
        }
    }

    LazyColumn(state =lazyColumnListState ) {
        items(messageList) { item ->
            if (item != null) {
                MessageTabItem(item, uiState)
            } else {
                Log.e("MessageScreenContent", "Item is null")
            }
        }
        if (isLoading) {
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
    }

    if (noChatAvailable){
        Box(
            Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                Text(
                    text = stringResource(R.string.no_chats_available),
                    fontSize = 16.sp,
                    maxLines = 1,
                    color = Black,
                    fontFamily = OpenSans,
                )
            }
        }

    }
    /* messageList.loadState.refresh.apply {


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
                                 messageList.retry()
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

             LoadState.Loading -> {
                 CustomLoader()
             }

             is LoadState.NotLoading -> {
                 if (messageList.itemCount > 0) {

                 } else {

                 }
             }
         }
     }*/
}

@Composable
fun MessageTabItem(item: MessageTabResponse, uiState: MessageUiState) {
    if (item.userId.isEmpty()) {
        Log.e("MessageTabItem", "User ID list is empty")
        return
    }
    Row(modifier = Modifier
        .padding(15.dp)
        .fillMaxWidth()
        .clickable() {
            uiState.navigateToSingleUserChat(item)
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
        if (item.userId.size == 2) {
            AsyncImage(
                modifier = Modifier
                    .clip(CircleShape)
                    .size(38.dp),
                model = item.profileImage ?: R.drawable.ic_placeholder,  // Ensure model is not null
                contentDescription = null,
                placeholder = painterResource(R.drawable.ic_placeholder),
                error = painterResource(id = R.drawable.ic_placeholder),
                contentScale = ContentScale.Crop
            )
        } else {
            Surface(
                shape = CircleShape,
                color = AppThemeColor,
                modifier = Modifier.size(38.dp)
            ) {
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = item.userId.size.toString(),
                        color = White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.W600,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
        Column(
            horizontalAlignment = Alignment.Start,
            modifier = Modifier
                .weight(1f)
                .padding(start = 8.dp)
        ) {
            Text(
                text = if (item.name.isNullOrEmpty()) stringResource(id = R.string.you) else item.name,
                style = style,
                fontSize = 10.sp,
                maxLines = 1,
                fontFamily = OpenSans,
                fontWeight = FontWeight.W600,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(end = 30.dp)
            )
            Spacer(modifier = Modifier.padding(2.dp))
            Text(
                text = item.message ?: "",  // Provide a default value if null
                style = style.copy(color = Black40),
                maxLines = 1,
                fontFamily = OpenSans,
                fontWeight = FontWeight.W600,
                fontSize = 12.sp,
                textAlign = TextAlign.Start,
                overflow = TextOverflow.Ellipsis
            )
        }
        if (item.count > 0) {
            Card(
                colors = CardDefaults.cardColors(containerColor = AppThemeColor),
                shape = RoundedCornerShape(50.dp),
                modifier = Modifier.padding(top = 7.dp)
            ) {
                Text(
                    text = if (item.count > 99) "99+" else item.count.toString(),
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


