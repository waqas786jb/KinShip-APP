package com.kinship.mobile.app.ux.container.memeberSingleChat

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.paging.compose.collectAsLazyPagingItems
import co.touchlab.kermit.Logger
import coil.compose.AsyncImage
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.kinship.mobile.app.R
import com.kinship.mobile.app.data.source.Constants
import com.kinship.mobile.app.model.domain.response.chat.KinshipGroupChatListData
import com.kinship.mobile.app.model.domain.response.group.GroupMember
import com.kinship.mobile.app.navigation.HandleNavigation
import com.kinship.mobile.app.navigation.scaffold.AppScaffold
import com.kinship.mobile.app.ui.compose.common.CameraGalleryDialog
import com.kinship.mobile.app.ui.compose.common.CustomLoader
import com.kinship.mobile.app.ui.theme.Black
import com.kinship.mobile.app.ui.theme.Black50
import com.kinship.mobile.app.ui.theme.BlackC9
import com.kinship.mobile.app.ui.theme.OpenSans
import com.kinship.mobile.app.ui.theme.AppThemeColor
import com.kinship.mobile.app.ui.theme.Pink70

import com.kinship.mobile.app.ui.theme.HoneyFlower50
import com.kinship.mobile.app.ui.theme.White
import com.kinship.mobile.app.utils.AppUtils
import com.kinship.mobile.app.utils.DateTimeUtils

@Composable
fun MemberSingleChatScreen(
    navController: NavHostController,
    memberResponse: String,
    chatId: String,
    viewModel: MemberSingleChatViewModel = hiltViewModel()
) {
    val uiState = viewModel.uiState
    val memberData = remember { parseMemberData(memberResponse) }
    val showLoader by uiState.showLoader.collectAsStateWithLifecycle()
    val context = LocalContext.current
    BackHandler(onBack = {
        uiState.onBackClick()
    })
    AppScaffold(
        containerColor = White,
        topAppBar = {
            MemberTopBar(
                profile = memberData?.profileImage,
                userName = memberData?.firstName?.plus("").plus(memberData?.lastName),
                leadingIconClick = {
                    uiState.onBackClick()
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
                uiState.sendMemberData(memberResponse, chatId)
                uiState.getContext(context)
                MemberUserChatContent(uiState)
            }
            SendSingleMessageView(uiState = uiState)

        }
        if (showLoader) {
            CustomLoader()
        }
    }
    HandleNavigation(viewModelNav = viewModel, navController = navController)
    val lifecycleOwner = LocalLifecycleOwner.current
    val lifecycleState by lifecycleOwner.lifecycle.currentStateFlow.collectAsState()
    LaunchedEffect(lifecycleState) {
        when (lifecycleState) {
            Lifecycle.State.RESUMED -> {
                uiState.initSocketListener(context)
            }
            else -> {}
        }
    }
}

private fun parseMemberData(memberDataResponse: String): GroupMember? {
    return try {
        Gson().fromJson(memberDataResponse, GroupMember::class.java)
    } catch (e: JsonSyntaxException) {
        Log.e("SingleUserChatScreen", "Failed to parse member data JSON", e)
        null
    }
}

@Composable
fun MemberUserChatContent(
    uiState: MemberSingleChatUiState,


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
        MemberSingleChatView(uiState)

    }
}


@Composable
fun MemberTopBar(
    modifier: Modifier = Modifier,
    profile: String?,
    userName: String?,
    leadingIconClick: () -> Unit = {},

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
            Spacer(modifier = Modifier.padding(8.dp))

            AsyncImage(
                model = profile,
                contentDescription = null,
                error = painterResource(id = R.drawable.ic_placeholder),
                placeholder = painterResource(id = R.drawable.ic_placeholder),
                modifier = modifier
                    .align(Alignment.CenterVertically)
                    .clip(shape = CircleShape)
                    .size(30.dp),
                contentScale = ContentScale.Crop,
            )


            Spacer(modifier = Modifier.padding(10.dp))
            Text(
                text = userName ?: "",
                color = Color.Black,
                fontSize = 16.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center,
                modifier = modifier
                    .padding(end = 50.dp)
                    .align(Alignment.CenterVertically), fontFamily = OpenSans,
                fontWeight = FontWeight.W600
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

@SuppressLint("SuspiciousIndentation")
@Composable
fun MemberSingleChatView(
    uiState: MemberSingleChatUiState,

    ) {
    val context = LocalContext.current
    uiState.apiSingleListResultFlow.collectAsLazyPagingItems()

    val showLoader by uiState.showLoader.collectAsStateWithLifecycle()


    val userGroupList by
    uiState.apiUserGroupListResultFlow.collectAsStateWithLifecycle()
    val isLoading = uiState.isLoading.collectAsStateWithLifecycle()
    val lazyColumnListState = rememberLazyListState()
    val instantSingleMessages = uiState.msgListFlow.collectAsStateWithLifecycle()
    val groupedStaticMsg = instantSingleMessages.value.groupBy { item ->
        DateTimeUtils.getCurrentDateTime()
    }
    val groupedRemoteMsg =
        userGroupList.groupBy { item ->
            item.let {
                DateTimeUtils.getMessageTimestamp(it.createdAt ?: 0, context)
            }
        }

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

    if (userGroupList.isNotEmpty() || instantSingleMessages.value.isNotEmpty()) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            reverseLayout = true,
        ) {
            groupedStaticMsg.forEach { (dateStampStatic, messageStatic) ->
                items(messageStatic) { item ->
                    MemberSingleChatItem(item)
                }
                item {
                    if (!groupedRemoteMsg.contains(stringResource(id = R.string.today))) {
                        Text(
                            text = stringResource(id = R.string.today),
                            color = Pink70,
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
            groupedRemoteMsg.forEach { (dateStamp, messages) ->
                items(messages) { item ->
                    MemberSingleChatItem(item)
                }
                item {
                    Text(
                        text = dateStamp,
                        color = Pink70,
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
            Box(
                Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    Text(
                        text = stringResource(
                            R.string.no_messages_available
                        ),
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
fun MemberSingleChatItem(item: KinshipGroupChatListData) {
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
                Text(
                    text = if (item.name.isNullOrEmpty()) stringResource(id = R.string.you) else item.name,
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
fun SendSingleMessageView(uiState: MemberSingleChatUiState) {
    val msgValue by uiState.msgValue.collectAsStateWithLifecycle()
    val profileImage by uiState.profilePicFlow.collectAsStateWithLifecycle()
    val interactionSource = remember { MutableInteractionSource() }
    val showDialog by uiState.openPickImgDialog.collectAsStateWithLifecycle()
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
            elevation = CardDefaults.cardElevation(1.dp)
        ) {
            if (profileImage.isNotEmpty()) {
                Spacer(modifier = Modifier.padding(5.dp))
                Row {
                    Spacer(modifier = Modifier.padding(12.dp))
                    AsyncImage(
                        model = profileImage,
                        contentDescription = stringResource(id = R.string.image),
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
                            contentDescription = stringResource(id = R.string.remove_image)
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
                OutlinedTextField(
                    value = msgValue,
                    onValueChange = { onMsgValueChange(it) },
                    interactionSource = interactionSource,
                    modifier = Modifier.weight(1f),
                    colors = TextFieldDefaults.colors(
                        focusedIndicatorColor = White,
                        unfocusedIndicatorColor = White,
                        focusedContainerColor = White,
                        unfocusedContainerColor = White,
                        cursorColor = Black,
                        disabledIndicatorColor = White
                    ),
                    trailingIcon = {
                        Row {
                            Spacer(modifier = Modifier.padding(5.dp))
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
                    },
                    leadingIcon = {
                        IconButton(onClick = { uiState.onOpenORDismissDialog(true) }) {
                            Image(
                                painter = painterResource(R.drawable.ic_add_event),
                                contentDescription = stringResource(R.string.user),
                                modifier = Modifier.padding(start = 15.dp)
                            )
                        }
                    },
                    keyboardOptions = KeyboardOptions(
                        autoCorrectEnabled = true,
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Done
                    ),
                    placeholder = {
                        Text(
                            text = stringResource(id = R.string.send_message),
                            fontFamily = OpenSans,
                            fontWeight = FontWeight.W400,
                            fontSize = 12.sp,
                            color = Black50
                        )
                    }
                )
            }
        }
    }
    if (showDialog) {
        CameraGalleryDialog(
            onDismissRequest = { uiState.onOpenORDismissDialog(false) },
            topText = stringResource(id = R.string.gallery),
            bottomText = stringResource(id = R.string.camera),
            onTopClick = {
                uiState.onOpenORDismissDialog(false)
                pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            },
            onBottomClick = {
                uiState.onOpenORDismissDialog(false)
                val permissionCheckResult =
                    ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
                if (permissionCheckResult == PackageManager.PERMISSION_GRANTED) {
                    uiState.onClickOfCamera(context)
                } else {
                    permissionLauncher.launch(Manifest.permission.CAMERA)
                }
            }
        )
    }
    val launchCamera by uiState.launchCamera.collectAsStateWithLifecycle()
    LaunchedEffect(key1 = launchCamera) {
        if (launchCamera) {
            uri?.let {
                Logger.e("capture uri: $it")
                cameraLauncher.launch(it)
            } ?: Logger.e("URI is null, cannot launch camera")
        }
    }
}