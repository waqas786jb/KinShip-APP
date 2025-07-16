@file:Suppress("DEPRECATION")

package com.kinship.mobile.app.ux.container.communityFeature.communityPost
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
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
import com.kinship.mobile.app.data.source.Constants
import com.kinship.mobile.app.model.domain.response.communities.communityPost.CommunityPostResponse
import com.kinship.mobile.app.navigation.HandleNavigation
import com.kinship.mobile.app.navigation.scaffold.AppScaffold
import com.kinship.mobile.app.ui.compose.common.BottomButtonComponent
import com.kinship.mobile.app.ui.compose.common.CustomLoader
import com.kinship.mobile.app.ui.compose.common.LeaveCommunityDialog
import com.kinship.mobile.app.ui.compose.common.TopBarComponent
import com.kinship.mobile.app.ui.theme.AppThemeColor
import com.kinship.mobile.app.ui.theme.Black
import com.kinship.mobile.app.ui.theme.Black50
import com.kinship.mobile.app.ui.theme.BlackLiteF0
import com.kinship.mobile.app.ui.theme.HoneyFlower20
import com.kinship.mobile.app.ui.theme.OpenSans
import com.kinship.mobile.app.ui.theme.White
import com.kinship.mobile.app.utils.AppUtils
import es.dmoral.toasty.Toasty
import kotlinx.coroutines.launch

@Preview
@Composable
fun CommunityPostScreen(
    navController: NavController = rememberNavController(),
    viewModel: CommunityPostViewModel = hiltViewModel()
) {
    val uiState = viewModel.uiState
    val communityName by uiState.communityName.collectAsStateWithLifecycle()
    val joinCommunity by uiState.joinCommunity.collectAsStateWithLifecycle()
    val showLeaveKinshipDialog by uiState.openPickImgDialog.collectAsStateWithLifecycle()
    val screenName by uiState.screenName.collectAsStateWithLifecycle()
    val isRefreshing by uiState.isAllEventsRefreshing.collectAsStateWithLifecycle()
    AppScaffold(
        containerColor = White,
        topAppBar = {
            TopBarComponent(
                header = communityName,
                isLineVisible = true,
                isBackVisible = true,
                isTrailingIconVisible = true,
                onTrailingIconClick = {
                    uiState.onOpenORDismissDialog(true)
                },
                trailingIcon =  if (screenName==Constants.AppScreen.NOTIFICATION_SCREEN) null else if (joinCommunity) R.drawable.ic_community_leave else null,
                onClick = {
                    uiState.onBackClick()
                }
            )
        },
    ) {
        val showLoader by uiState.showLoader.collectAsStateWithLifecycle()
        if (showLoader) {
            CustomLoader()
        }
        SwipeRefresh(
            state = rememberSwipeRefreshState(isRefreshing),
            onRefresh = {
                uiState.communityPostAPICall() // Ensure this method refreshes the event list
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
            CommunityPostContent(uiState)
        }

    }
    val lifecycleOwner = LocalLifecycleOwner.current
    val lifecycleState by lifecycleOwner.lifecycle.currentStateFlow.collectAsState()
    LaunchedEffect(lifecycleState) {
        when (lifecycleState) {
            Lifecycle.State.RESUMED -> {
                uiState.communityPostAPICall()
            }
            else -> {}
        }
    }
    if (showLeaveKinshipDialog) {
        LeaveCommunityDialog(
            onDismissRequest = { uiState.onOpenORDismissDialog(false) },
            title = stringResource(R.string.leave_community),
            positiveText = stringResource(R.string.yes),
            description = stringResource(R.string.are_you_sure_you_want_to_leave_this_community),
            negative = stringResource(R.string.no),
            onPositiveClick = {
                uiState.leaveCommunityAPICall()
            }
        )
    }
    HandleNavigation(viewModelNav = viewModel, navController = navController)
}

@Composable
private fun CommunityPostContent(uiState: CommunityPostUiState) {
    val communityPostList by uiState.communityPostList.collectAsStateWithLifecycle()
    val noDataFound by uiState.noDataFoundText.collectAsStateWithLifecycle()
    val joinCommunity by uiState.joinCommunity.collectAsStateWithLifecycle()
    val listState = rememberLazyListState()
    val screenName by uiState.screenName.collectAsStateWithLifecycle()
    val communityId by uiState.communityId.collectAsStateWithLifecycle()
    LaunchedEffect(communityPostList.size) {
        if (communityPostList.isNotEmpty()) {
            listState.animateScrollToItem(0)
        }
    }
    if (screenName==Constants.AppScreen.NOTIFICATION_SCREEN){
        LaunchedEffect(communityId, communityPostList) {
            // screen top not a show
            // scrollOffset
            val targetIndex = communityPostList.indexOfFirst { it.id == communityId }
            if (targetIndex != -1) {
                listState.animateScrollToItem(targetIndex, scrollOffset = -50)
            }
        }
    }


    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 15.dp)
            .background(White)
    ) {
        if (noDataFound) {
            Box(
                Modifier
                    .fillMaxSize()
                    .padding(20.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(R.string.no_community_post_here),
                    fontSize = 16.sp,
                    maxLines = 1,
                    color = Black,
                    fontFamily = OpenSans,
                )
            }
        }
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 50.dp)
        ) {
            items(communityPostList) { item ->
                CommunityPostItem(item, uiState)
            }
        }
        if (!joinCommunity) {
            if (screenName==Constants.AppScreen.NOTIFICATION_SCREEN) return
            BottomButtonComponent(
                onClick = { uiState.onJoinCommunityClick() },
                text = stringResource(R.string.join_community),
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(vertical = 20.dp)
            )
        } else {
            if (screenName==Constants.AppScreen.NOTIFICATION_SCREEN) return
            Row(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 5.dp)
                    .clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }
                    ) {
                        uiState.navigateToAddNewPost()
                    }
            ) {
                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .clip(CircleShape)
                        .background(AppThemeColor),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_community_add),
                        contentDescription = null
                    )
                }
            }


        }
    }
}

@Composable
fun CommunityPostItem(item: CommunityPostResponse, uiState: CommunityPostUiState) {
    val joinCommunity by uiState.joinCommunity.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val checkedState by remember { mutableStateOf(item.isLiked ?: false) }
    Log.d("TAG", "CommunityPostItemCheck: ${item.isLiked}")
    Log.d("TAG", "CommunityPostItemCount: ${item.like}")

    val checkedStateItem = if (item.isLiked == true) {
        Pair(R.drawable.ic_filled_heart, "Checked")
    } else {
        Pair(R.drawable.ic_unfilled_heart, "Unchecked")
    }

    Log.d("TAG", "CommunityPostItem: $checkedState")

    val backgroundColor = if (item.isLiked == true) HoneyFlower20 else BlackLiteF0
    var imagePreview by remember {
        mutableStateOf<Pair<Boolean, String?>>(
            Pair(
                false,
                null
            )
        )
    }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(White)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            AsyncImage(
                model = item.profileImage,
                contentDescription = "Profile Image",
                contentScale = ContentScale.Crop,
                placeholder = painterResource(id = R.drawable.ic_placeholder),
                error = painterResource(id = R.drawable.ic_placeholder),
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = "${item.firstName} ${item.lastName}",
                    fontWeight = FontWeight.W600,
                    fontFamily = OpenSans,
                    color = AppThemeColor,
                    fontSize = 12.sp
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = AppUtils.formatTimestampToDateTime(item.createdAt ?: 0L),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.W600,
                    fontFamily = OpenSans,
                    color = Color.Gray
                )
            }
        }
        item.message?.takeIf { it.isNotEmpty() }?.let { message ->
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = message,
                fontSize = 12.sp,
                color = Black,
                fontWeight = FontWeight.W500,
                lineHeight = 18.sp,
                modifier = Modifier.padding(horizontal = 4.dp)
            )
        }
        item.file?.takeIf { it.isNotEmpty() }?.let { imageUrl ->
            Spacer(modifier = Modifier.height(10.dp))
            AsyncImage(
                model = imageUrl,
                contentDescription = "Post Image",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .clickable {
                        imagePreview = Pair(true, item.file)
                    }
                    .height(200.dp)
            )
        }

        Spacer(modifier = Modifier.height(10.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,

            ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(backgroundColor)
                    .clickable {
                        if (joinCommunity) {
                            scope.launch {
                                item.isLiked = item.isLiked != true
                                item.isLiked?.let {
                                    uiState.communityPostLikeDislike(
                                        item.id ?: "",
                                        it
                                    )
                                }
                                item.like =
                                    if (item.isLiked == true) item.like?.plus(1) else item.like?.minus(
                                        1
                                    )
                                Log.d("TAG", "CommunityPostItem: $checkedState")

                            }
                        } else {
                            Toasty
                                .warning(
                                    context,
                                    "You need to join Community",
                                    Toast.LENGTH_SHORT,
                                    false
                                )
                                .show()
                        }
                    }
                    .padding(horizontal = 15.dp, vertical = 7.dp)
            ) {
                Image(
                    painter = painterResource(id = checkedStateItem.first),
                    modifier = Modifier.size(15.dp),
                    contentDescription = checkedStateItem.second
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = item.like.toString(),
                    fontSize = 10.sp,
                    fontFamily = OpenSans,
                    fontWeight = FontWeight.W600,
                    color = Black50
                )
            }
            Spacer(Modifier.padding(10.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(BlackLiteF0)
                    .clickable {
                        uiState.navigateToCommentScreen(
                            CommunityPostResponse(
                                profileImage = item.profileImage,
                                firstName = item.firstName,
                                lastName = item.lastName,
                                createdAt = item.createdAt,
                                file = item.file,
                                message = item.message,
                                like = item.like,
                                isLiked = item.isLiked,
                                commentCount = item.commentCount,
                                id = item.id,
                                joinCommunity = joinCommunity
                            )
                        )
                    }
                    .padding(horizontal = 15.dp, vertical = 7.dp)
            ) {
                Image(
                    painter = painterResource(R.drawable.ic_comment),
                    modifier = Modifier.size(15.dp),
                    contentDescription = null
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = item.commentCount.toString(),
                    fontSize = 10.sp,
                    fontFamily = OpenSans,
                    fontWeight = FontWeight.W600,
                    color = Black50
                )
            }
        }
    }
    Spacer(modifier = Modifier.padding(15.dp))
    if (imagePreview.first) {
        Dialog(onDismissRequest = { imagePreview = Pair(false, null) }) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .clickable { imagePreview = Pair(false, null) },
            ) {
                AsyncImage(
                    model = imagePreview.second,
                    contentDescription = null,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .background(Color.White, shape = RoundedCornerShape(8.dp))
                )
            }
        }
    }
}

@Preview
@Composable
private fun Preview() {
    val uiState = CommunityPostUiState()
    CommunityPostContent(uiState)
}