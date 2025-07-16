package com.kinship.mobile.app.ux.container.singleUserChat

import android.Manifest
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
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
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
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.kinship.mobile.app.R
import com.kinship.mobile.app.data.source.Constants
import com.kinship.mobile.app.model.domain.response.chat.KinshipGroupChatListData
import com.kinship.mobile.app.model.domain.response.chat.userGroup.MessageTabResponse
import com.kinship.mobile.app.model.domain.response.chat.userGroup.addMember.groupMember.UserGroupMember
import com.kinship.mobile.app.model.domain.response.group.GroupMember
import com.kinship.mobile.app.navigation.HandleNavigation
import com.kinship.mobile.app.navigation.scaffold.AppScaffold
import com.kinship.mobile.app.ui.compose.common.CameraGalleryDialog
import com.kinship.mobile.app.ui.compose.common.CustomLoader
import com.kinship.mobile.app.ui.compose.common.message.MessageDisplayComponent
import com.kinship.mobile.app.ui.theme.AppThemeColor
import com.kinship.mobile.app.ui.theme.Black
import com.kinship.mobile.app.ui.theme.Black30
import com.kinship.mobile.app.ui.theme.Black50
import com.kinship.mobile.app.ui.theme.BlackC9
import com.kinship.mobile.app.ui.theme.HoneyFlower50
import com.kinship.mobile.app.ui.theme.HoneyFlower70
import com.kinship.mobile.app.ui.theme.OpenSans
import com.kinship.mobile.app.ui.theme.White
import com.kinship.mobile.app.utils.AppUtils
import com.kinship.mobile.app.utils.DateTimeUtils
import kotlinx.coroutines.launch

@Composable
fun SingleUserChatScreen(
    navController: NavController = rememberNavController(),
    viewModel: SingleGroupChatViewModel = hiltViewModel(),
    messageDataResponse: String,
    memberDataResponse: String,
    chatID: String

) {
    val uiState = viewModel.uiState
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val coroutine = rememberCoroutineScope()
    val context = LocalContext.current
    Log.d("TAG", "messageDataResponse: $messageDataResponse")
    Log.d("TAG", "memberDataResponse: $memberDataResponse")
    val messageData = remember { parseMessageData(messageDataResponse) }
    val memberData = remember { parseMemberData(memberDataResponse) }
    BackHandler(onBack = {
        uiState.onBackClick()
    })
    Log.d("TAG", "SingleUserChatScreen: ${memberData?.userId}")
    if (messageData?.userId?.size == 2 || memberData?.userId != null) {

        AppScaffold(
            containerColor = White,
            topAppBar = {

                TopBar(
                    profile = messageData?.profileImage ?: memberData?.profileImage,
                    userName = messageData?.name ?: memberData?.firstName.plus(" ")
                        .plus(memberData?.lastName),
                    userId = messageData?.userId ?: emptyList(),
                    trailingIconVisible = {
                        coroutine.launch {
                            drawerState.apply {
                                if (isClosed) open() else close()
                                uiState.userMemberListAPICall()
                            }
                        }
                    },
                    memberUserId = memberData?.userId ?: "",

                    leadingIconClick = {
                        uiState.onBackClick()
                    },

                    )
                /* TopBar(
                     profile = messageData.profileImage,
                     userName = messageData.name,
                     userId = messageData.userId,
                     trailingIconVisible = {
                         coroutine.launch {
                             drawerState.apply {
                                 if (isClosed) open() else close()
                                 uiState.userMemberListAPICall()
                             }
                         }
                     },
                     leadingIconClick = {
                         uiState.onBackClick()
                     }
                 )*/
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
                    if (messageDataResponse.isNotEmpty()){
                        uiState.sendMessageData(
                            messageDataResponse,
                            memberData?.userId ?: "",
                            chatID,
                            memberData?.chatGroupId ?: ""
                        )

                    }else{

                        uiState.sendMemberData(memberDataResponse,chatID)
                    }
                    SingleUserChatContent(
                        uiState,
                        messageData?.userId ?: emptyList(),

                        )
                }
                SendSingleMessageView(uiState = uiState)
            }
        }
    } else {


        MenuSlider(uiState, drawerState, messageData, messageDataResponse, memberData, chatID)
    }
   // val groupId by uiState.groupid.collectAsStateWithLifecycle()
    val tempGroupId = remember { mutableStateOf("") }

    val showLoader by uiState.showLoader.collectAsStateWithLifecycle()
    if (showLoader) {
        CustomLoader()
    }
    HandleNavigation(viewModelNav = viewModel, navController = navController)
    val lifecycleOwner = LocalLifecycleOwner.current
    val lifecycleState by lifecycleOwner.lifecycle.currentStateFlow.collectAsState()
    LaunchedEffect(lifecycleState) {
        when (lifecycleState) {
            Lifecycle.State.RESUMED -> {
                uiState.initSocketListener(context)
            }
            Lifecycle.State.DESTROYED -> {
                //uiState.userGroupId(tempGroupId.value)



            }

            else -> {}
        }
    }
}
@Composable
fun MenuSlider(
    uiState: SingleGroupChatUiState,
    drawerState: DrawerState,
    messageData: MessageTabResponse?,
    messageDataResponse: String,
    memberData: GroupMember?,
    chatID: String
) {
    val coroutine = rememberCoroutineScope()
    val context = LocalContext.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val screenName by uiState.screenName.collectAsStateWithLifecycle()
    if (screenName!=Constants.AppScreen.NOTIFICATION_SCREEN){
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
            ModalNavigationDrawer(
                drawerState = drawerState,
                drawerContent = {
                    Column(
                        modifier = Modifier
                            .requiredWidth(260.dp)
                            .fillMaxHeight()
                            .background(Color.White)
                            .clickable(indication = null,
                                interactionSource = remember { MutableInteractionSource() })
                            {
                                keyboardController?.hide()
                            }
                    ) {
                        UserGroupMenuContent(uiState, messageData)
                    }
                },
                content = {
                    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                        AppScaffold(
                            containerColor = White,
                            topAppBar = {
                                if (messageData != null) {
                                    TopBarTemp(
                                        profile = messageData.profileImage,
                                        userName = messageData.name,
                                        userId = messageData.userId,
                                        trailingIconVisible = {
                                            coroutine.launch {
                                                drawerState.apply {
                                                    if (isClosed) open() else close()
                                                    uiState.userMemberListAPICall()
                                                }
                                            }
                                        },
                                        memberUserId = memberData?.userId ?: "",
                                        leadingIconClick = {
                                            uiState.onBackClick()
                                        }
                                    )

                                }
                            },
                            navBarData = null
                        ) {
                            Column(
                                modifier = Modifier
                                    .padding(
                                        start = 20.dp,
                                        end = 20.dp,
                                        bottom = 10.dp,
                                    )
                                    .fillMaxSize(),

                                ) {
                                Box(modifier = Modifier.weight(1f)) {
                                    uiState.sendMessageData(
                                        messageDataResponse,
                                        memberData?.chatGroupId ?: "",
                                        chatID,
                                        memberData?.chatGroupId ?: ""
                                    )
                                    SingleUserChatContent(
                                        uiState,
                                        messageData?.userId ?: emptyList(),
                                    )
                                }
                                SendSingleMessageView(uiState = uiState)
                            }
                        }
                    }
                }
            )
        }
    }else{
        AppScaffold(
            containerColor = White,
            topAppBar = {
                val notificationData by uiState.notificationProfileData.collectAsStateWithLifecycle()
                Log.d("TAG", "notificationData: ${notificationData.name}")
                TopBarNotificationTemp(
                        profile = notificationData.profileImage,
                        userName = notificationData.name,
                        userId = notificationData.userId?: emptyList(),
                        trailingIconVisible = {
                            if (screenName!=Constants.AppScreen.NOTIFICATION_SCREEN){
                                coroutine.launch {
                                    drawerState.apply {
                                        if (isClosed) open() else close()
                                        uiState.userMemberListAPICall()
                                    }
                                }
                            }
                        },
                        leadingIconClick = {
                            uiState.onBackClick()
                        },
                        screen = screenName
                    )


            },
            navBarData = null
        ) {
            Column(
                modifier = Modifier
                    .padding(
                        start = 20.dp,
                        end = 20.dp,
                        bottom = 10.dp,
                    )
                    .fillMaxSize(),

                ) {
                Box(modifier = Modifier.weight(1f)) {
                    uiState.sendMessageData(
                        messageDataResponse,
                        memberData?.chatGroupId ?: "",
                        chatID,
                        memberData?.chatGroupId ?: ""
                    )
                    SingleUserChatContent(
                        uiState,
                        messageData?.userId ?: emptyList(),
                    )
                }
                if (screenName!=Constants.AppScreen.NOTIFICATION_SCREEN){
                    SendSingleMessageView(uiState = uiState)
                }
            }
        }
    }

    LaunchedEffect(drawerState.isOpen) {
        if (drawerState.isOpen) {
            uiState.userMemberListAPICall()
        }
    }

}

private fun parseMessageData(messageDataResponse: String): MessageTabResponse? {
    return try {
        Gson().fromJson(messageDataResponse, MessageTabResponse::class.java)
    } catch (e: JsonSyntaxException) {
        Log.e("SingleUserChatScreen", "Failed to parse message data JSON", e)
        null
    }
}

private fun parseMemberData(memberDataResponse: String): GroupMember? {
    return try {
        Gson().fromJson(memberDataResponse, GroupMember::class.java)
    } catch (e: JsonSyntaxException) {
        Log.e("SingleUserChatScreen", "Failed to parse message data JSON", e)
        null
    }
}

@Composable
fun UserGroupMenuContent(uiState: SingleGroupChatUiState, messageData: MessageTabResponse?) {
    val userMemberList by uiState.apiMemberList.collectAsStateWithLifecycle()
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
        Column(
            modifier = Modifier
                .background(White)
                .padding(horizontal = 20.dp)
        ) {
            Text(
                text = "Members [${messageData?.userId?.size}]",
                modifier = Modifier
                    .padding(16.dp)
                    .align(alignment = Alignment.CenterHorizontally),
                color = Black,
                fontWeight = FontWeight.W600,
                fontFamily = OpenSans
            )
            Box(
                modifier = Modifier
                    .background(color = Black30)
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .heightIn(1.5.dp)
            )
            Spacer(modifier = Modifier.padding(10.dp))
            LazyColumn(verticalArrangement = Arrangement.spacedBy(20.dp)) {
                items(userMemberList) { member ->
                    UserGroupMemberItem(member)
                }
            }
        }
    }
}

@Composable
fun UserGroupMemberItem(member: UserGroupMember) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        MessageDisplayComponent(
            profile = member.profileImage,
            username = member.name,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun SingleUserChatContent(
    uiState: SingleGroupChatUiState,
    userId: List<String>,
    ) {
    val keyboardController = LocalSoftwareKeyboardController.current
    Column(
        modifier = Modifier
            .fillMaxSize()
            .clickable(indication = null,
                interactionSource = remember { MutableInteractionSource() })
            {
                keyboardController?.hide()
            },
    ) {
        SingleUserChatView(uiState, userId)
        val showLoader by uiState.showLoader.collectAsStateWithLifecycle()
        if (showLoader) {
            CustomLoader()
        }
    }
}

@Composable
fun TopBar(
    modifier: Modifier = Modifier,
    profile: String?,
    userId: List<String>,
    memberUserId: String,
    userName: String?,
    leadingIconClick: () -> Unit = {},
    trailingIconVisible: () -> Unit = {},
) {
    Column(
        modifier = Modifier
            .background(White)
            .padding(horizontal = 20.dp)
    ) {
        Row(
            modifier = modifier
                .requiredHeight(54.dp)
                .fillMaxWidth()

        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_back),
                contentDescription = null,
                modifier = modifier
                    .align(Alignment.CenterVertically)
                    .clickable {
                        leadingIconClick()
                    }
            )
            Spacer(modifier = Modifier.width(8.dp))
            // Profile picture if type is 2
            if (userId.size == 2 || memberUserId != null) {
                AsyncImage(
                    model = profile,
                    contentDescription = null,
                    error = painterResource(id = R.drawable.ic_placeholder),
                    placeholder = painterResource(id = R.drawable.ic_placeholder),
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .clip(shape = CircleShape)
                        .size(30.dp),
                    contentScale = ContentScale.Crop,
                )
            }
            if (userId.size > 2) {
                Spacer(modifier = Modifier.padding(horizontal = 40.dp))
                Text(
                    text = userName ?: "",
                    color = Color.Black,
                    fontSize = 16.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .weight(1f)
                        .align(Alignment.CenterVertically),
                    fontFamily = OpenSans,
                    fontWeight = FontWeight.W600
                )
            }
            if (userId.size == 2 || memberUserId != null) {
                Spacer(modifier = Modifier.padding(horizontal = 10.dp))
                Text(
                    text = userName ?: "",
                    color = Color.Black,
                    fontSize = 16.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .align(Alignment.CenterVertically),
                    fontFamily = OpenSans,
                    fontWeight = FontWeight.W600
                )
            } else {
                Spacer(modifier = Modifier.weight(1f))
                Image(
                    painter = painterResource(id = R.drawable.ic_group_profile),
                    contentDescription = null,
                    modifier = modifier
                        .align(Alignment.CenterVertically)
                        .clickable {
                            trailingIconVisible()
                        }
                )
            }
        }
        Box(
            modifier = modifier
                .background(color = BlackC9)
                .fillMaxWidth()
                .alpha(70f)
                .padding(horizontal = 30.dp)
                .height(0.5.dp)
        )
    }
}

@Composable
fun TopBarNotificationTemp(
    modifier: Modifier = Modifier,
    profile: String?,
    userId: List<String>,
    userName: String?,
    leadingIconClick: () -> Unit = {},
    trailingIconVisible: () -> Unit = {},
    screen:String,
) {

    Column(
        modifier = Modifier
            .background(White)
            .padding(horizontal = 20.dp)
    ) {
        Row(
            modifier = modifier
                .requiredHeight(54.dp)
                .fillMaxWidth()

        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_back),
                contentDescription = null,
                modifier = modifier
                    .align(Alignment.CenterVertically)
                    .clickable {
                        leadingIconClick()
                    }
            )

            Spacer(modifier = Modifier.width(8.dp))
            if (userId.size == 2) {
                AsyncImage(
                    model = profile,
                    contentDescription = null,
                    error = painterResource(id = R.drawable.ic_placeholder),
                    placeholder = painterResource(id = R.drawable.ic_placeholder),
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .clip(shape = CircleShape)
                        .size(30.dp),
                    contentScale = ContentScale.Crop,
                )
            }

            if (userId.size > 2) {
                Spacer(modifier = Modifier.padding(horizontal = 40.dp))
                Text(
                    text = userName ?: "",
                    color = Color.Black,
                    fontSize = 16.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .weight(1f)
                        .align(Alignment.CenterVertically),
                    fontFamily = OpenSans,
                    fontWeight = FontWeight.W600
                )
                Log.d("TAG", "TopBarNotificationTemp: $userName")
                Log.d("TAG", "TopBarNotificationTemp: $userId")
            }
            if (userId.size == 2) {
                Spacer(modifier = Modifier.padding(horizontal = 10.dp))
                Text(
                    text = userName ?: "",
                    color = Color.Black,
                    fontSize = 16.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .align(Alignment.CenterVertically),
                    fontFamily = OpenSans,
                    fontWeight = FontWeight.W600
                )
                Log.d("TAG", "TopBarNotificationTemp: $userName")
                Log.d("TAG", "TopBarNotificationTemp: $userId")
            }

            Spacer(modifier = Modifier.weight(1f))

            if (screen!=Constants.AppScreen.NOTIFICATION_SCREEN){
                Image(
                    painter = painterResource(id = R.drawable.ic_group_profile),
                    contentDescription = null,
                    modifier = modifier
                        .align(Alignment.CenterVertically)
                        .clickable {
                            trailingIconVisible()
                        }
                )
            }

        }
        Box(
            modifier = modifier
                .background(color = BlackC9)
                .fillMaxWidth()
                .alpha(70f)
                .padding(horizontal = 30.dp)
                .height(0.5.dp)
        )
    }
}

@Composable
fun TopBarTemp(
    modifier: Modifier = Modifier,
    profile: String?,
    userId: List<String>,
    memberUserId: String,
    userName: String?,
    leadingIconClick: () -> Unit = {},
    trailingIconVisible: () -> Unit = {},
) {
    Column(
        modifier = Modifier
            .background(White)
            .padding(horizontal = 20.dp)
    ) {
        Row(
            modifier = modifier
                .requiredHeight(54.dp)
                .fillMaxWidth()

        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_back),
                contentDescription = null,
                modifier = modifier
                    .align(Alignment.CenterVertically)
                    .clickable {
                        leadingIconClick()
                    }
            )
            Spacer(modifier = Modifier.width(8.dp))


            if (userId.size > 2) {
                Spacer(modifier = Modifier.padding(horizontal = 40.dp))
                Text(
                    text = userName ?: "",
                    color = Color.Black,
                    fontSize = 16.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .weight(1f)
                        .align(Alignment.CenterVertically),
                    fontFamily = OpenSans,
                    fontWeight = FontWeight.W600
                )
            }

            Spacer(modifier = Modifier.weight(1f))
            Image(
                painter = painterResource(id = R.drawable.ic_group_profile),
                contentDescription = null,
                modifier = modifier
                    .align(Alignment.CenterVertically)
                    .clickable {
                        trailingIconVisible()
                    }
            )

        }
        Box(
            modifier = modifier
                .background(color = BlackC9)
                .fillMaxWidth()
                .alpha(70f)
                .padding(horizontal = 30.dp)
                .height(0.5.dp)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SingleUserChatView(
    uiState: SingleGroupChatUiState,
    userId: List<String>,
) {
    val context = LocalContext.current
    val userGroupList by uiState.apiUserGroupListResultFlow.collectAsStateWithLifecycle()
    val isLoading by uiState.isLoading.collectAsStateWithLifecycle()
    val lazyColumnListState = rememberLazyListState()
    val instantSingleMessages by uiState.msgListFlow.collectAsStateWithLifecycle()
    val showLoader by uiState.showLoader.collectAsStateWithLifecycle()
    val subId by uiState.subId.collectAsStateWithLifecycle()

    // Grouping static and remote messages by date
    val groupedStaticMsg = instantSingleMessages.groupBy { DateTimeUtils.getCurrentDateTime() }
    val groupedRemoteMsg = userGroupList.groupBy {
        DateTimeUtils.getMessageTimestamp(it.createdAt ?: 0, context)
    }

    // Load more messages when reaching the end of the list
    val shouldLoadMore = remember {
        derivedStateOf {
            val lastVisibleItemIndex =
                lazyColumnListState.layoutInfo.visibleItemsInfo.lastOrNull()?.index
            lastVisibleItemIndex != null && lastVisibleItemIndex >= lazyColumnListState.layoutInfo.totalItemsCount - 5
        }
    }
    LaunchedEffect(shouldLoadMore.value) {
        if (shouldLoadMore.value) {
            uiState.onLoadSingleGroupNextPage()
        }
    }
    val listState = rememberLazyListState()
    LaunchedEffect(subId, userGroupList) {
        if (subId.isNotEmpty()) {
            val allMessages = userGroupList
            val targetIndex = allMessages.indexOfFirst { it.id == subId }
            if (targetIndex != -1) {
                listState.animateScrollToItem(targetIndex, scrollOffset = -50)
            }
        }
    }
    LaunchedEffect(instantSingleMessages.size) {
        if (instantSingleMessages.isNotEmpty()) {
            lazyColumnListState.animateScrollToItem(0) // Scroll to the first item (latest message)
        }
    }
    // Content display: Check if we have messages to show
    if (userGroupList.isNotEmpty() || instantSingleMessages.isNotEmpty()) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            state = lazyColumnListState,
            reverseLayout = true
        ) {
            // Display static messages (instant messages)
            groupedStaticMsg.forEach { (_, messageStatic) ->
                items(messageStatic) { item ->
                    SingleChatItem(item)
                }
            }
            // Display today's date if it's not in the groupedRemoteMsg
            item {
                if (!groupedRemoteMsg.contains(stringResource(R.string.today))) {
                    Text(
                        text = stringResource(id = R.string.today),
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
            // Display remote messages (grouped by date)

                groupedRemoteMsg.forEach { (dateStamp, messages) ->
                    items(messages) { item ->
                        SingleChatItem(item)
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

            // Loading indicator while fetching more messages
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
    } else {
        // No messages case
        if (!showLoader) {
            Box(
                Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    Text(
                        text = if (userId.size == 2|| userId.isEmpty()) {
                            stringResource(R.string.no_messages_available)
                        } else {
                            stringResource(R.string.no_messages_in_this_group_yet)
                        },
                        fontSize = 16.sp,
                        color = Black,
                        fontFamily = OpenSans
                    )
                }
            }
        }
    }
}


@Composable
fun SingleChatItem(item: KinshipGroupChatListData) {
    var imagePreview by remember { mutableStateOf<Pair<Boolean, String?>>(Pair(false, null)) }
    val context = LocalContext.current

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
                // User's name
                Text(
                    text = if (item.name.isNullOrEmpty()) stringResource(id = R.string.you) else item.name,
                    color = Black50,
                    fontFamily = OpenSans,
                    fontWeight = FontWeight.W600,
                    fontSize = 12.sp,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1
                )

                // Handling text messages
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
                                pushStringAnnotation(tag = "URL", annotation = annotatedUrl)
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
                                        Intent(Intent.ACTION_VIEW, Uri.parse(annotation.item))
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
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .background(Color.White, shape = RoundedCornerShape(8.dp))
                )
            }
        }
    }
}
@Composable
fun SendSingleMessageView(uiState: SingleGroupChatUiState) {
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
                        contentDescription = stringResource(R.string.image),
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
                            contentDescription = stringResource(R.string.remove_image),
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
                        contentDescription = stringResource(R.string.add_event),
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
                            )
                        }
                    )
                }
                IconButton(onClick = {
                    var preparedMsgValue = msgValue
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
                        contentDescription = stringResource(id = R.string.send_message_content_description)

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