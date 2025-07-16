@file:Suppress("NAME_SHADOWING", "DEPRECATION")

package com.kinship.mobile.app.ux.container.chat

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.net.Uri
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.SoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import co.touchlab.kermit.Logger
import coil.compose.AsyncImage
import com.kinship.mobile.app.R
import com.kinship.mobile.app.data.source.Constants
import com.kinship.mobile.app.data.source.local.TempData
import com.kinship.mobile.app.model.domain.response.chat.KinshipGroupChatListData
import com.kinship.mobile.app.navigation.HandleNavigation
import com.kinship.mobile.app.navigation.scaffold.AppScaffold
import com.kinship.mobile.app.ui.compose.KinshipNameEditDialog
import com.kinship.mobile.app.ui.compose.common.CameraGalleryDialog
import com.kinship.mobile.app.ui.compose.common.CustomLoader
import com.kinship.mobile.app.ui.compose.common.TopBarComponent
import com.kinship.mobile.app.ui.compose.common.WelcomeKinshipDialog
import com.kinship.mobile.app.ui.theme.AppThemeColor
import com.kinship.mobile.app.ui.theme.Black
import com.kinship.mobile.app.ui.theme.Black30
import com.kinship.mobile.app.ui.theme.Black50
import com.kinship.mobile.app.ui.theme.GrayF1
import com.kinship.mobile.app.ui.theme.HoneyFlower50
import com.kinship.mobile.app.ui.theme.HoneyFlower70
import com.kinship.mobile.app.ui.theme.OpenSans
import com.kinship.mobile.app.ui.theme.White
import com.kinship.mobile.app.utils.AppUtils
import com.kinship.mobile.app.utils.DateTimeUtils
import com.kinship.mobile.app.utils.socket.SocketClass
import kotlinx.coroutines.launch

@Composable
fun GroupChatScreen(
    navController: NavController = rememberNavController(),
    viewModel: GroupChatViewModel = hiltViewModel(),
) {
    val uiState = viewModel.uiState
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val keyboardController = LocalSoftwareKeyboardController.current
    val coroutine = rememberCoroutineScope()
    val name by uiState.kinshipName.collectAsStateWithLifecycle()
    val messageId = uiState.messageId
    val screenName by uiState.screenName.collectAsStateWithLifecycle()
    val tempAccessToken by uiState.tempAccessToken.collectAsStateWithLifecycle()
    Log.d("TAG", "messageId: $messageId")
    BackHandler(onBack = {
        if (messageId.isEmpty()) {
            uiState.onBackClick()
        } else {
            if (screenName == Constants.AppScreen.NOTIFICATION_SCREEN) {
                uiState.onNotificationBackClick()
            } else {
                uiState.navigateToSearchScreen()
            }
        }
    })
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                Column(
                    modifier = Modifier
                        .requiredWidth(260.dp)
                        .fillMaxHeight()
                        .clickable(indication = null,
                            interactionSource = remember { MutableInteractionSource() })
                        {
                            keyboardController?.hide()
                        }
                        .background(Color.White)
                ) {
                    SideMenuContent(uiState)
                }
            },
            content = {
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                    val showHideSendMessageView by uiState.hideSendMessageView.collectAsStateWithLifecycle()
                    AppScaffold(
                        containerColor = White,
                        topAppBar = {
                            TopBarComponent(
                                header = name,
                                isLineVisible = true,
                                isBackVisible = true,
                                isTrailingIconVisible = true,
                                trailingIcon = R.drawable.ic_side_menu,
                                onClick = {
                                    if (messageId.isEmpty()) {
                                        uiState.onBackClick()
                                    } else {
                                        if (screenName == Constants.AppScreen.NOTIFICATION_SCREEN) {
                                            uiState.onNotificationBackClick()
                                        } else {
                                            uiState.navigateToSearchScreen()
                                        }
                                    }
                                },
                                onTrailingIconClick = {
                                    coroutine.launch {
                                        drawerState.apply {
                                            if (isClosed) open() else close()
                                        }
                                    }
                                }
                            )
                        },
                        navBarData = null
                    ) {
                        Column(
                            modifier = Modifier
                                .padding(
                                    start = 15.dp,
                                    end = 15.dp,
                                    bottom = 10.dp,
                                )
                                .fillMaxSize(),

                            ) {
                            Box(modifier = Modifier.weight(1f)) {
                                MessageListView(uiState, keyboardController)
                            }
                            if (showHideSendMessageView) {
                                SendMessageView(uiState = uiState)
                            }
                            val showLoader by uiState.showLoader.collectAsStateWithLifecycle()
                            if (showLoader) {
                                CustomLoader()
                            }
                        }
                    }
                }
            }
        )
    }
    HandleNavigation(viewModelNav = viewModel, navController = navController)
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
        Log.d("TAG", "GroupChatScreen: $tempAccessToken")
    }
    LaunchedEffect(drawerState.isOpen) {
        if (drawerState.isOpen) {
            keyboardController?.hide()
        }
    }
}

@Preview
@Composable
fun SideMenu(modifier: Modifier = Modifier) {
    val uiState = GroupChatUiState()
    SideMenuContent(uiState = uiState)
}

@Composable
fun MessageListView(
    uiState: GroupChatUiState,
    keyboardController: SoftwareKeyboardController?
) {
    val isFlag by uiState.isFlag.collectAsStateWithLifecycle()
    val messageId = uiState.messageId
    Column(
        modifier = Modifier.clickable(indication = null,
            interactionSource = remember { MutableInteractionSource() })
        {
            keyboardController?.hide()
        }
    ) {
        if (messageId.isEmpty()) {
            GroupChatListDataShow(uiState)
        } else {
            SearchChatListDataShow(uiState)
        }
    }
    if (isFlag) {
        WelcomeKinshipDialog(
            title = if (uiState.kinshipReason == 1 || uiState.kinshipReason == 2) {
                "We’ve matched you with ${
                    uiState.memberCount.toInt().minus(1)
                } other mom - to - be who we think you'll relate to."
            } else {
                "We’ve matched you with ${
                    uiState.memberCount.toInt().minus(1)
                } other mom who we think you'll relate to."
            }.toString(),
            onPositiveClick = {
                uiState.userFlagAPICall()
                uiState.onIsFlagDialog(false)
            },
            onDismissRequest = {
            }
        )
    }
}

@Composable
fun GroupChatListDataShow(uiState: GroupChatUiState) {
    val lazyColumnListState = rememberLazyListState()
    val chatList by uiState.apiChatListResultFlow.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val instantMessages = uiState.msgListFlow.collectAsStateWithLifecycle()
    val isLoading = uiState.isLoading.collectAsStateWithLifecycle()
    val shouldLoadMore = remember {
        derivedStateOf {
            val layoutInfo = lazyColumnListState.layoutInfo
            val lastVisibleItemIndex = layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
            val totalItemsCount = layoutInfo.totalItemsCount
            lastVisibleItemIndex >= totalItemsCount - 1 && totalItemsCount > 0
        }
    }
    LaunchedEffect(shouldLoadMore.value) {
        if (shouldLoadMore.value && !isLoading.value) {
            uiState.onLoadNextPage()
        }
    }
    LaunchedEffect(instantMessages.value) {
        if (instantMessages.value.isNotEmpty()) {
            lazyColumnListState.animateScrollToItem(0)  // Scroll to first item (latest in reverseLayout)
        }
    }
    val showLoader by uiState.showLoader.collectAsStateWithLifecycle()
    val groupedStaticMsg = instantMessages.value.groupBy {
        DateTimeUtils.getCurrentDateTime()
    }
    val groupedRemoteMsg =
        chatList.groupBy { item ->
            item.let {
                DateTimeUtils.getMessageTimestamp(
                    it.createdAt ?: 0,
                    context
                )
            }
        }
    if (chatList.isNotEmpty() || instantMessages.value.isNotEmpty()) {
        LazyColumn(
            state = lazyColumnListState,
            modifier = Modifier.fillMaxSize(),
            reverseLayout = true,
        ) {
            groupedStaticMsg.forEach { (dateStampStatic, messageStatic) ->
                items(messageStatic) { item ->
                    ChatItemContent(item, uiState)
                }
                item {
                    if (!groupedRemoteMsg.contains("Today")) {
                        Text(
                            text = "Today",
                            color = HoneyFlower70,
                            fontFamily = OpenSans,
                            fontWeight = FontWeight.W600,
                            fontSize = 12.sp,
                            modifier = Modifier
                                .padding(vertical = 10.dp)
                                .fillMaxWidth(),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
            groupedRemoteMsg.forEach { (dateStamp, remoteMessage) ->
                items(remoteMessage) { item ->
                    ChatItemContent(item, uiState)
                }
                item {
                    Text(
                        text = dateStamp,
                        color = HoneyFlower70,
                        fontFamily = OpenSans,
                        fontWeight = FontWeight.W600,
                        fontSize = 12.sp,
                        modifier = Modifier
                            .padding(vertical = 10.dp)
                            .fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                }
            }
            if (isLoading.value) {
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
    } else {
        if (!showLoader) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(15.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(R.string.no_messages_in_this_group_yet),
                    fontSize = 16.sp,
                    maxLines = 1,
                    color = Black,
                    fontFamily = OpenSans,
                )
            }
        }
    }
}

@Composable
fun ChatItemContent(
    item: KinshipGroupChatListData,
    uiState: GroupChatUiState,
) {
    var imagePreview by remember {
        mutableStateOf<Pair<Boolean, String?>>(
            Pair(
                false,
                null
            )
        )
    }
    val context = LocalContext.current
    /* var checkedState by remember {
         mutableStateOf(
             item.isgrpLiked ?: false
         )
     }*/
    val checkedStateItem = if (item.isgrpLiked == true) Pair(
        R.drawable.ic_filled_heart,
        "Checked"
    ) else Pair(R.drawable.ic_unfilled_heart, "Unchecked")
    uiState.senderName(item.name ?: "")

    if (item.type == 5) {
        Spacer(modifier = Modifier.padding(5.dp))
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
        ) {
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = GrayF1,
            ) {
                Text(
                    text = item.message ?: "",
                    color = Black50,
                    fontFamily = OpenSans,
                    fontWeight = FontWeight.W600,
                    fontSize = 10.sp,
                    textAlign = TextAlign.Center,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 2,
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp)
                )
            }
        }
    } else {
        Column {
            Row(
                verticalAlignment = Alignment.Top,
                modifier = Modifier.padding(5.dp)
            ) {
                Surface(
                    shape = CircleShape,
                    border = BorderStroke(width = 1.dp, color = AppThemeColor),
                    modifier = Modifier.size(30.dp)
                ) {
                    AsyncImage(
                        model = item.profileImage,
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        error = painterResource(id = R.drawable.ic_placeholder),
                        placeholder = painterResource(R.drawable.ic_placeholder)
                    )
                }
                Column(
                    horizontalAlignment = Alignment.Start,
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 8.dp)
                ) {
                    Text(
                        text = if (item.name.isNullOrEmpty()) "You" else item.name,
                        color = Black50,
                        fontFamily = OpenSans,
                        fontWeight = FontWeight.W600,
                        fontSize = 12.sp,
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1
                    )

                    item.message?.let { message ->
                        if (message.isNotBlank()) {
                            val annotatedString = buildAnnotatedString {
                                val urlPattern =
                                    "(https?://[\\w-]+(\\.[\\w-]+)+(/[#&\\w-./?%+=]*)?)|www\\.[\\w-]+(\\.[\\w-]+)+(/[\\w-./?%&=#]*)?"
                                val regex = Regex(urlPattern)
                                var lastIndex = 0
                                regex.findAll(message).forEach { matchResult ->
                                    val start = matchResult.range.first
                                    val end = matchResult.range.last + 1
                                    if (start > lastIndex) {
                                        append(message.substring(lastIndex, start))
                                    }
                                    val url = message.substring(start, end)
                                    val annotatedUrl = if (url.startsWith("www.")) {
                                        "http://$url"
                                    } else {
                                        url
                                    }
                                    pushStringAnnotation(
                                        tag = "URL",
                                        annotation = annotatedUrl
                                    )
                                    withStyle(
                                        style = SpanStyle(
                                            color = Color.Blue,
                                            textDecoration = TextDecoration.Underline
                                        )
                                    ) {
                                        append(url)
                                    }
                                    pop()
                                    lastIndex = end
                                }
                                if (lastIndex < message.length) {
                                    append(message.substring(lastIndex))
                                }
                            }
                            ClickableText(
                                text = annotatedString,
                                style = TextStyle(
                                    color = Black,
                                    fontFamily = OpenSans,
                                    fontWeight = FontWeight.W600,
                                    fontSize = 13.sp,
                                ),
                                onClick = { offset ->
                                    annotatedString.getStringAnnotations(
                                        tag = "URL",
                                        start = offset,
                                        end = offset
                                    ).firstOrNull()?.let { annotation ->
                                        val intent =
                                            Intent(
                                                Intent.ACTION_VIEW,
                                                Uri.parse(annotation.item)
                                            )
                                        context.startActivity(intent)
                                    }
                                }
                            )
                            Spacer(modifier = Modifier.height(5.dp))
                        }
                    }


                    if (item.message.isNullOrEmpty() && item.image != null) {
                        Spacer(modifier = Modifier.height(5.dp))
                    }
                    if (item.type == Constants.Message.ONLY_IMAGE || item.type == Constants.Message.IMAGE_AND_MESSAGE) {
                        Surface(
                            border = BorderStroke(width = 1.dp, color = AppThemeColor),
                            shape = RoundedCornerShape(8.dp),
                            onClick = {
                                imagePreview = Pair(true, item.image)
                            },
                            modifier = Modifier.padding(start = 0.dp)
                        ) {
                            AsyncImage(
                                model = item.image,
                                contentDescription = null,
                                modifier = Modifier.size(95.dp),
                                contentScale = ContentScale.Crop
                            )
                        }
                    }
                }
                Image(
                    painter = painterResource(id = checkedStateItem.first),
                    contentDescription = checkedStateItem.second,
                    modifier = Modifier
                        .padding(top = 5.dp)
                        .size(20.dp)
                        .clickable {
                            // checkedState = !checkedState
                            item.isgrpLiked = item.isgrpLiked != true
                            uiState.onIsLikeAndDislikeAPICall(item.id.toString())
                        }
                )
            }
        }
    }
    if (imagePreview.first) {
        Dialog(onDismissRequest = { imagePreview = Pair(false, null) }) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.5f)
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
fun SearchChatListDataShow(uiState: GroupChatUiState) {
    val searchChatAPI =
        uiState.apiSearchChat.collectAsStateWithLifecycle()
    val messageId by uiState.messageMainID.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val coroutine = rememberCoroutineScope()
    val groupedRemoteMsg =
        searchChatAPI.value.groupBy { item ->
            item.let {
                DateTimeUtils.getMessageTimestamp(
                    it.createdAt ?: 0,
                    context
                )
            }
        }

    val listState = rememberLazyListState()
    LaunchedEffect(messageId, searchChatAPI) {
        if (messageId.isNotEmpty()) {
            val targetIndex = searchChatAPI.value.indexOfFirst { it.id == messageId }
            if (targetIndex != -1) {
                coroutine.launch {
                    listState.animateScrollToItem(targetIndex, scrollOffset = -50)
                }
            }
        }
    }
    uiState.onHideSendMessageView(false)
    LazyColumn(
        reverseLayout = true,
        state = listState,
        modifier = Modifier.fillMaxSize(),

        ) {
        groupedRemoteMsg.forEach { (dateStamp, messages) ->
            items(messages) { item ->
                ChatItemContent(item, uiState)
            }
            item {
                Text(
                    text = dateStamp,
                    color = HoneyFlower70,
                    fontFamily = OpenSans,
                    fontWeight = FontWeight.W600,
                    fontSize = 12.sp,
                    modifier = Modifier
                        .padding(vertical = 10.dp)
                        .fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}


@Composable
fun SendMessageView(uiState: GroupChatUiState) {
    val msgValue by uiState.msgValue.collectAsStateWithLifecycle()
    val profileImage by uiState.profilePicFlow.collectAsStateWithLifecycle()
    var textLines by remember { mutableIntStateOf(1) }
    val interactionSource = remember { MutableInteractionSource() }
    var showDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current
    var capturedImageUri by remember { mutableStateOf<Uri>(Uri.EMPTY) }
    val uri by uiState.captureUri.collectAsStateWithLifecycle()
    val onMsgValueChange by rememberUpdatedState(newValue = uiState.onMsgValueChange)

    val pickMedia =
        rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            uri?.let {
                val selectedProfileImagePath = AppUtils.getFileFromContentUri(
                    context, it,
                    Constants.AppInfo.DIR_NAME.plus(System.currentTimeMillis())
                )?.absolutePath.orEmpty()
                uiState.onProfileImgPick(selectedProfileImagePath)
            } ?: Log.e("Profile", "OpenPhotoPicker: No media selected")
        }

    val cameraLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { captured ->
            if (captured) {
                capturedImageUri = uri ?: Uri.EMPTY
                val selectedProfileImagePath = AppUtils.getFileFromContentUri(
                    context, capturedImageUri,
                    Constants.AppInfo.DIR_NAME.plus(System.currentTimeMillis())
                )?.absolutePath.orEmpty()
                uiState.onProfileImgPick(selectedProfileImagePath)
            } else {
                uiState.onClearUnUsedUseState()
            }
        }

    val permissionLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) {
            if (it) uiState.onClickOfCamera(context)
        }
    Column {
        Card(
            shape = RoundedCornerShape(30.dp),
            colors = CardDefaults.cardColors(containerColor = White),
            elevation = CardDefaults.cardElevation(3.dp)
        ) {
            if (profileImage.isNotEmpty()) {
                Spacer(modifier = Modifier.padding(5.dp))
                Row {
                    Spacer(modifier = Modifier.padding(12.dp))
                    AsyncImage(
                        model = profileImage,
                        contentDescription = "Image",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier


                            .size(100.dp)
                            .clip(shape = RoundedCornerShape(10.dp))
                    )
                    IconButton(
                        onClick = { uiState.onProfileImgPick("") },
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_close),
                            tint = Color.Gray,
                            contentDescription = "Remove Image",
                            modifier = Modifier
                                .offset(x = (-3).dp, y = (-3).dp)
                        )
                    }
                }
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .background(color = White)
                    .fillMaxWidth()
            ) {
                IconButton(onClick = { showDialog = true }) {
                    Image(
                        painter = painterResource(R.drawable.ic_add_event),
                        contentDescription = "Add Event",
                        modifier = Modifier.padding(start = 15.dp)
                    )
                }
                Box(
                    modifier = Modifier
                        .weight(1f) // Ensures the text field takes up the remaining space
                        .heightIn(max = (4 * 24).dp)
                        .verticalScroll(reverseScrolling = true, state = rememberScrollState())
                ) {
                    OutlinedTextField(
                        value = msgValue,
                        onValueChange = {
                            onMsgValueChange(it)
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
                                text = stringResource(id = R.string.send_message),
                                fontFamily = OpenSans,
                                fontWeight = FontWeight.W400,
                                fontSize = 12.sp,
                                color = Black50,
                                modifier = Modifier.offset(x = ((-2).dp))
                            )
                        }
                    )
                }
                IconButton(onClick = {
                    var preparedMsgValue = msgValue.trim()
                    val urlPattern = ".*(http|https|www\\.).*".toRegex()

                    if (preparedMsgValue.isNotEmpty() && profileImage.isEmpty()) {
                        if (msgValue.startsWith("www.")) {
                            preparedMsgValue = "http://$msgValue"
                        }
                        if (msgValue.contains(urlPattern)) {
                            uiState.onMessageSend(preparedMsgValue, 3)
                        } else {
                            uiState.onMessageSend(msgValue, 1)
                        }
                    } else if (profileImage.isNotEmpty()) {
                        uiState.sendMessageAPICall()
                    }
                    onMsgValueChange("")
                    uiState.onProfileImgPick("")
                }) {
                    Image(
                        painter = painterResource(R.drawable.ic_send_msg),
                        contentDescription = "Send Message"
                    )
                }
            }
        }
    }

    if (showDialog) {
        CameraGalleryDialog(
            onDismissRequest = { showDialog = false },
            topText = stringResource(id = R.string.gallery),
            bottomText = stringResource(id = R.string.camera),
            onTopClick = {
                showDialog = false
                pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            },
            onBottomClick = {
                showDialog = false
                val permissionCheckResult = ContextCompat.checkSelfPermission(
                    context, Manifest.permission.CAMERA
                )
                if (permissionCheckResult == PackageManager.PERMISSION_GRANTED) {
                    uiState.onClickOfCamera(context)
                } else {
                    permissionLauncher.launch(Manifest.permission.CAMERA)
                }
            }
        )
    }
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    val launchCamera by uiState.launchCamera.collectAsStateWithLifecycle()
    LaunchedEffect(key1 = launchCamera, key2 = isLandscape) {
        if (launchCamera && !isLandscape) { // Skip launching in landscape
            uri?.let { validUri ->
                Logger.e("capture uri: $validUri")
                cameraLauncher.launch(validUri)
            } ?: Logger.e("URI is null, cannot launch camera")
        } else if (isLandscape) {
            Logger.e("Skipping camera launch in landscape mode")
        }
    }
}

@Composable
fun SideMenuContent(uiState: GroupChatUiState) {
    val showKinshipEditDialog by uiState.openKinshipEditNameDialog.collectAsStateWithLifecycle()
    val kinshipNameEdit by uiState.kinshipNameEdit.collectAsStateWithLifecycle()
    val kinshipName by uiState.kinshipName.collectAsStateWithLifecycle()
    val drawerListingData = listOf(
        TempData.DrawerData(
            title = uiState.memberCount.plus(" Members"),
            icon = R.drawable.ic_member,
            true
        ),
        TempData.DrawerData(
            title = stringResource(id = R.string.gallery),
            icon = R.drawable.ic_gallery,
            true
        ),
        TempData.DrawerData(
            title = stringResource(id = R.string.links),
            icon = R.drawable.ic_link,
            true
        ),
        TempData.DrawerData(
            title = stringResource(id = R.string.search_messages),
            icon = R.drawable.ic_search,
            true
        ),
        TempData.DrawerData(
            title = stringResource(id = R.string.liked_messages),
            icon = R.drawable.ic_menu_heart,
            true
        ),
    )
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
        Column(
            modifier = Modifier
                .background(White)
                .padding(horizontal = 20.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = kinshipName,
                    modifier = Modifier
                        .weight(1f),
                    color = Color.Black,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    fontWeight = FontWeight.W600,
                    fontFamily = OpenSans
                )
                Image(
                    painter = painterResource(id = R.drawable.ic_edit),
                    contentDescription = "edit",
                    modifier = Modifier
                        .clickable {
                            uiState.onKinshipEditNameDialog(true)
                        }
                )
            }
            Box(
                modifier = Modifier
                    .background(color = Black30)
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .heightIn(1.5.dp)
            )
            DrawerNavigationListItem(list = drawerListingData, uiState)
        }
        if (showKinshipEditDialog) {
            KinshipNameEditDialog(
                negativeText = stringResource(R.string.ok),
                positiveText = stringResource(R.string.save),
                value = kinshipNameEdit,
                onValueChange = { uiState.onKinshipNameValueChange(it) },
                onDismissRequest = {
                    uiState.onKinshipEditNameDialog(false)
                    uiState.clearKinshipNameEditState()
                },
                onPositiveClick = {
                    uiState.kinshipNameEditAPICall()
                },
            )
        }
    }
}

@SuppressLint("CheckResult")
@Composable
fun DrawerNavigationListItem(
    list: List<TempData.DrawerData>,
    uiState: GroupChatUiState,
) {
    list.forEachIndexed { index, settingData ->
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(vertical = 15.dp)
                .clickable {
                    when (index) {
                        0 -> {
                            uiState.onMemberClickButton()
                        }

                        1 -> {
                            uiState.onGalleryClickButton()
                        }

                        2 -> {
                            uiState.onLinkButtonButton()
                        }

                        3 -> {
                            uiState.onSearchMessageButton()
                        }

                        4 -> {
                            uiState.onLikeMessageButton()
                        }
                    }
                },
        ) {
            Image(
                painter = painterResource(id = settingData.icon),
                contentDescription = null,
                modifier = Modifier,
                contentScale = ContentScale.Crop
            )
            Text(
                text = settingData.title,
                fontSize = 12.sp,
                fontFamily = OpenSans,
                fontWeight = FontWeight.W600,
                color = Black,
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 15.dp),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            if (settingData.isArrowVisible) {
                Image(
                    painter = painterResource(id = R.drawable.ic_arrow_right),
                    contentDescription = null,
                    modifier = Modifier.size(14.dp)
                )
            }
        }
    }
}

