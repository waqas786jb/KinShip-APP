package com.kinship.mobile.app.ux.container.communityFeature.comment

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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.kinship.mobile.app.R
import com.kinship.mobile.app.data.source.Constants
import com.kinship.mobile.app.model.domain.response.communities.communityPost.CommunityPostResponse
import com.kinship.mobile.app.navigation.HandleNavigation
import com.kinship.mobile.app.navigation.scaffold.AppScaffold
import com.kinship.mobile.app.ui.compose.common.CustomLoader
import com.kinship.mobile.app.ui.compose.common.TopBarComponent
import com.kinship.mobile.app.ui.theme.OpenSans
import com.kinship.mobile.app.ui.theme.AppThemeColor
import com.kinship.mobile.app.ui.theme.Black
import com.kinship.mobile.app.ui.theme.Black50
import com.kinship.mobile.app.ui.theme.BlackC9
import com.kinship.mobile.app.ui.theme.BlackLiteF0
import com.kinship.mobile.app.ui.theme.HoneyFlower20
import com.kinship.mobile.app.ui.theme.White
import com.kinship.mobile.app.utils.AppUtils
import es.dmoral.toasty.Toasty

@Preview
@Composable
fun CommentScreen(
    navController: NavController = rememberNavController(),
    viewModel: CommentViewModel = hiltViewModel()
) {
    val uiState = viewModel.uiState
    val keyboardController = LocalSoftwareKeyboardController.current
    AppScaffold(
        containerColor = White,
        topAppBar = {
            TopBarComponent(
                header = "Reply",
                isBackVisible = true,
                isLineVisible = true,
                onTrailingIconClick = {
                    uiState.onClickSearchCommunity()
                },
                onClick = {
                    keyboardController?.hide()
                    uiState.onBackClick()
                }
            )
        }
    ) {
        val showLoader by uiState.showLoader.collectAsStateWithLifecycle()
        if (showLoader) {
            CustomLoader()
        }
        CommentScreenContent(uiState = uiState)
    }
    HandleNavigation(viewModelNav = viewModel, navController = navController)
}

@Composable
fun CommentScreenContent(uiState: CommentUiState) {
    val commentData by uiState.commentData.collectAsStateWithLifecycle()
    val keyboardController = LocalSoftwareKeyboardController.current
    val screenName by uiState.screenName.collectAsStateWithLifecycle()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .clickable(indication = null,
                interactionSource = remember { MutableInteractionSource() }) {
                keyboardController?.hide()
            }
            .verticalScroll(rememberScrollState())
            .background(White)
    ) {
        Box(modifier = Modifier.weight(1f)) {
            CommentScreenView(uiState = uiState)
        }
        if (screenName==Constants.AppScreen.NOTIFICATION_SCREEN){
            SendCommentView(uiState = uiState)
        }else{
            if (commentData.joinCommunity) {
                SendCommentView(uiState = uiState)
            }
        }
    }
}
@Composable
fun SendCommentView(uiState: CommentUiState) {
    var textLines by remember { mutableIntStateOf(1) }
    val commentFlow by uiState.commentFlow.collectAsStateWithLifecycle()
    val interactionSource = remember { MutableInteractionSource() }
    val keyboardController = LocalSoftwareKeyboardController.current
    Column(modifier = Modifier.padding(bottom = 20.dp, start = 20.dp, end = 20.dp)) {
        Card(
            shape = RoundedCornerShape(30.dp),
            colors = CardDefaults.cardColors(containerColor = White),
            elevation = CardDefaults.cardElevation(3.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .background(color = White)
                    .fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .heightIn(max = (4 * 24).dp)
                        .padding(start = 15.dp)
                        .verticalScroll(reverseScrolling = true, state = rememberScrollState())
                ) {
                    OutlinedTextField(
                        value = commentFlow,
                        onValueChange = {
                            uiState.onCommentValueChange(it)
                            textLines = it.count { ch -> ch == '\n' } + 1
                        },
                        interactionSource = interactionSource,
                        modifier = Modifier
                            .fillMaxWidth()
                            .offset(x = ((-4).dp)),
                        colors = TextFieldDefaults.colors(
                            focusedIndicatorColor = White,
                            unfocusedIndicatorColor = White,
                            focusedContainerColor = White,
                            unfocusedContainerColor = White,
                            cursorColor = Black,
                            disabledIndicatorColor = White
                        ),
                        placeholder = {
                            Text(
                                text = stringResource(R.string.reply),
                                fontFamily = OpenSans,
                                fontWeight = FontWeight.W400,
                                fontSize = 12.sp,
                                color = Black50,
                                modifier = Modifier
                                    .offset(x = ((-2).dp))

                            )
                        },
                    )
                }
                IconButton(onClick = {
                    uiState.onCommentSend(keyboardController)
                }) {
                    Image(
                        painter = painterResource(R.drawable.ic_send_msg),
                        contentDescription = "Send Message"
                    )
                }
            }
        }
    }
}

@Composable
fun CommentScreenView(uiState: CommentUiState) {
    val commentData by uiState.commentData.collectAsStateWithLifecycle()
    val commentList by uiState.commentList.collectAsStateWithLifecycle()
    val noDataFound by uiState.noDataFoundText.collectAsStateWithLifecycle()
    val lazyListState = rememberLazyListState()
    /*var checkedState by remember {
        mutableStateOf(
            commentData.isLiked ?: false
        )
    }*/
    val context = LocalContext.current
   // var likeCount by remember { mutableStateOf("") }
   // likeCount=commentData.like.toString()

    val checkedStateItem = if (commentData.isLiked == true) Pair(
        R.drawable.ic_filled_heart,
        "Checked"
    ) else Pair(R.drawable.ic_unfilled_heart, "Unchecked")
    var imagePreview by remember {
        mutableStateOf<Pair<Boolean, String?>>(
            Pair(
                false,
                null
            )
        )
    }
    val screenName by uiState.screenName.collectAsStateWithLifecycle()
    val background = if (commentData.isLiked == true) HoneyFlower20 else BlackLiteF0
    LazyColumn(
        state = lazyListState,
        modifier = Modifier
            .padding(horizontal = 20.dp, vertical = 15.dp)
            .fillMaxSize()
    ) {
        item {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                AsyncImage(
                    model = commentData.profileImage,
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
                        text = commentData.firstName.plus(" ").plus(commentData.lastName),
                        fontWeight = FontWeight.W600,
                        fontFamily = OpenSans,
                        color = AppThemeColor,
                        fontSize = 12.sp
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = AppUtils.formatTimestampToDateTime(commentData.createdAt ?: 0L),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.W600,
                        fontFamily = OpenSans,
                        color = Color.Gray
                    )
                }
            }
        }
        commentData.message?.takeIf { it.isNotEmpty() }?.let { message ->
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = message,
                    fontSize = 14.sp,
                    fontFamily = OpenSans,
                    color = Black,
                    fontWeight = FontWeight.W600,
                    lineHeight = 22.sp,
                    modifier = Modifier.padding(horizontal = 4.dp)
                )
            }
        }
        commentData.file?.takeIf { it.isNotEmpty() }?.let { imageUrl ->
            item {
                Spacer(modifier = Modifier.height(10.dp))
                AsyncImage(
                    model = imageUrl,
                    contentDescription = "Post Image",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .clickable {
                            imagePreview = Pair(true, commentData.file)
                        }
                        .height(200.dp)
                )
            }
        }

        item {
            Spacer(modifier = Modifier.padding(3.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(background)
                    .clickable {
                        if (commentData.joinCommunity) {
                            commentData.isLiked = commentData.isLiked != true
                            uiState.communityPostLikeDislike(commentData.id ?: "", commentData.isLiked?:false)
                            commentData.like = if (commentData.isLiked==true) {
                                commentData.like?.plus(1)
                            } else {
                                commentData.like?.minus(1)
                            }

                        } else {
                            if (screenName == Constants.AppScreen.NOTIFICATION_SCREEN) {
                                commentData.isLiked = commentData.isLiked !=true
                                uiState.communityPostLikeDislike(commentData.id ?: "", commentData.isLiked?:false)
                                commentData.like = if (commentData.isLiked==true) {
                                    commentData.like?.plus(1)
                                } else {
                                    commentData.like?.minus(1)
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
                    }
                    .padding(horizontal = 15.dp, vertical = 7.dp)
            ) {
                Image(
                    painter = painterResource(id = checkedStateItem.first),
                    modifier = Modifier.size(15.dp),
                    contentDescription = checkedStateItem.second,
                )
                Spacer(modifier = Modifier.padding(4.dp))
                Text(
                    text = commentData.like.toString(),
                    fontSize = 10.sp,
                    fontFamily = OpenSans,
                    fontWeight = FontWeight.W600,
                    color = Black50
                )
                Log.d("TAG", "IsLike:api:uiScreen ${commentData.like}")
            }
        }
        item {
            Spacer(modifier = Modifier.padding(10.dp))
            Row {
                Text(
                    text = "All Comments",
                    color = Black,
                    fontFamily = OpenSans,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.W900
                )
                Spacer(Modifier.weight(1f))
                Text(
                    text = "${commentData.commentCount} Comments",
                    color = Black,
                    fontFamily = OpenSans,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.W900
                )
            }
            Spacer(Modifier.padding(5.dp))
            HorizontalDivider(thickness = 0.5.dp, color = BlackC9)
        }

        if (noDataFound) {
            item {
                Box(
                    Modifier
                        .fillMaxWidth()
                        .padding(vertical = 20.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(R.string.no_comment_yet),
                        fontSize = 16.sp,
                        maxLines = 1,
                        color = Black,
                        fontFamily = OpenSans,
                    )
                }
            }
        }
        // Comments List
        items(commentList) { item ->
            CommentItem(item, uiState)
        }
    }
    LaunchedEffect(commentList) {
        if (commentList.isNotEmpty()) {
            lazyListState.scrollToItem(0)
        }
    }
    if (imagePreview.first) {
        Dialog(onDismissRequest = { imagePreview = Pair(false, null)}) {
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

@Composable
fun CommentItem(item: CommunityPostResponse, uiState: CommentUiState) {
    var checkedState by rememberSaveable {
        mutableStateOf(
            item.isLiked ?: false
        )
    }
    val context = LocalContext.current
    val commentData by uiState.commentData.collectAsStateWithLifecycle()
    var likeCount by remember { mutableStateOf(item.like) }
    val checkedStateItem = if (checkedState) Pair(
        R.drawable.ic_filled_heart,
        "Checked"
    ) else Pair(R.drawable.ic_unfilled_heart, "Unchecked")
    val screenName by uiState.screenName.collectAsStateWithLifecycle()
    val background = if (checkedState) HoneyFlower20 else BlackLiteF0
    Column(modifier = Modifier.padding(top = 15.dp)) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            AsyncImage(
                model = item.profileImage,
                contentDescription = "Profile Image",
                contentScale = ContentScale.Crop,
                placeholder = painterResource(id = R.drawable.ic_placeholder),
                error = painterResource(R.drawable.ic_placeholder),
                modifier = Modifier
                    .size(35.dp)
                    .clip(CircleShape)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = item.firstName.plus(" ").plus(item.lastName),
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
        Spacer(Modifier.padding(3.dp))
        Text(
            text = item.commentText,
            fontSize = 10.sp,
            color = Black,
            fontFamily = OpenSans,
            fontWeight = FontWeight.W900,
            lineHeight = 18.sp,
            modifier = Modifier.padding(horizontal = 4.dp)
        )
        Spacer(Modifier.padding(5.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(background)
                .clickable {
                    if (commentData.joinCommunity) {
                        checkedState = !checkedState
                        uiState.commentPostLikeDislike(item.id ?: "", checkedState)
                        likeCount = if (checkedState) {
                            likeCount?.plus(1)
                        } else {
                            likeCount?.minus(1)
                        }

                    } else {
                        if (screenName == Constants.AppScreen.NOTIFICATION_SCREEN) {
                            checkedState = !checkedState
                            uiState.commentPostLikeDislike(item.id ?: "", checkedState)
                            likeCount = if (checkedState) {
                                likeCount?.plus(1)
                            } else {
                                likeCount?.minus(1)
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
                }
                .padding(horizontal = 15.dp, vertical = 7.dp)
        ) {
            Image(
                painter = painterResource(checkedStateItem.first),
                modifier = Modifier.size(15.dp),
                contentDescription = checkedStateItem.second
            )
            Spacer(modifier = Modifier.padding(4.dp))
            Text(
                text = likeCount.toString(),
                fontSize = 10.sp,
                fontFamily = OpenSans,
                fontWeight = FontWeight.W600,
                color = Black50
            )
        }

    }

}

@Preview
@Composable
fun CommentScreenContentPreview(modifier: Modifier = Modifier) {
    val uiState = CommentUiState()
    CommentScreenContent(uiState)
}



