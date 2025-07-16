package com.kinship.mobile.app.ux.container.chat
import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.mutableStateListOf
import androidx.core.content.FileProvider
import co.touchlab.kermit.Logger
import com.kinship.mobile.app.BuildConfig
import com.kinship.mobile.app.R
import com.kinship.mobile.app.data.source.Constants
import com.kinship.mobile.app.data.source.local.datastore.AppPreferenceDataStore
import com.kinship.mobile.app.data.source.remote.helper.NetworkResult
import com.kinship.mobile.app.data.source.remote.repository.ApiRepository
import com.kinship.mobile.app.model.domain.request.resendOtp.IsLikeDislikeRequest
import com.kinship.mobile.app.model.domain.response.ApiResponse
import com.kinship.mobile.app.model.domain.response.chat.KinshipGroupChatListData
import com.kinship.mobile.app.model.domain.response.chat.isLIkeDislike.IsLikeDislikeResponse
import com.kinship.mobile.app.model.domain.response.group.CreateGroup
import com.kinship.mobile.app.model.domain.response.signUp.UserAuthResponseData
import com.kinship.mobile.app.navigation.NavigationAction
import com.kinship.mobile.app.utils.AppUtils
import com.kinship.mobile.app.utils.DateTimeUtils.formatDateTimeToUTCTimeStamp
import com.kinship.mobile.app.utils.ext.requireActivity
import com.kinship.mobile.app.utils.socket.OnSocketEventsListener
import com.kinship.mobile.app.utils.socket.SocketClass
import com.kinship.mobile.app.utils.socket.SocketClass.loggerE
import com.kinship.mobile.app.ux.container.ContainerActivity
import com.kinship.mobile.app.ux.container.search.SearchRoute
import com.kinship.mobile.app.ux.main.MainActivity
import es.dmoral.toasty.Toasty
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONException
import org.json.JSONObject
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Objects
import javax.inject.Inject

class GetGroupChatUiStateUseCase @Inject constructor(
    private val apiRepository: ApiRepository,
    private val appPreferenceDataStore: AppPreferenceDataStore
) : OnSocketEventsListener {

    private val _apiChatListResultFlow =
        MutableStateFlow<List<KinshipGroupChatListData>>(emptyList())
    private val apiChatListResultFlow: StateFlow<List<KinshipGroupChatListData>> =
        _apiChatListResultFlow

    private val apiSearchChatList = MutableStateFlow<List<KinshipGroupChatListData>>(emptyList())
    private val openKinshipEditNameDialog = MutableStateFlow(false)
    private val hideSendMessageView = MutableStateFlow(true)
    private val launchCamera = MutableStateFlow(false)
    private val captureUri = MutableStateFlow<Uri?>(null)
    private val profileImgFlow = MutableStateFlow("")
    private val msgListFlow = MutableStateFlow<List<KinshipGroupChatListData>>(emptyList())
    private val tempMsgList = mutableStateListOf<KinshipGroupChatListData>()
    private val msgValue = MutableStateFlow("")
    private val isLikeDislikeId = MutableStateFlow("")
    private val apiResultFlow =
        MutableStateFlow<NetworkResult<ApiResponse<IsLikeDislikeResponse>>?>(null)
    private val _isFlag = MutableStateFlow(false)
    private val isFlag: StateFlow<Boolean> get() = _isFlag
    private val kinshipName: StateFlow<String> get() = _kinshipName
    private val _kinshipName = MutableStateFlow("")

    private val showLoader = MutableStateFlow(false)
    private var roomId: String = ""
    private var senderID: String = ""
    private var receiverID: String = ""
    private var groupId: String = ""
    private var accessToken = ""
    private var name = ""
    private var image = ""
    private var senderName = ""
    private val kinshipNameFlow = MutableStateFlow("")
    private val isLoader = MutableStateFlow(false)
    private val _screenName = MutableStateFlow("")
    private val screenName: StateFlow<String> get() = _screenName


    private val _tempAccessToken = MutableStateFlow("")
    private val tempAccessToken: StateFlow<String> get() = _tempAccessToken

    private val _messageId = MutableStateFlow("")
    private val messageId: StateFlow<String> get() = _messageId

    private var currentPage = 1
    private val noDataFoundText = MutableStateFlow(false)



    operator fun invoke(
        context: Context,
        coroutineScope: CoroutineScope,
        messageId: String,
        screenName:String,
        subId:String,
        navigate: (NavigationAction) -> Unit,
    ): GroupChatUiState {
        if (kinshipNameFlow.value.isBlank()) {
            kinshipNameFlow.value = getCreateGroupData(coroutineScope)?.groupName ?: ""
        }
        _tempAccessToken.value =getAccessToken(coroutineScope)
        Log.d(TAG, "subId: $messageId")
       this._screenName.value=screenName
        this._messageId.value=subId
        initializeState(coroutineScope, messageId, context, subId = subId)
        initSocket()
        val kinshipName = getCreateGroupData(coroutineScope)?.groupName.orEmpty()
        _kinshipName.value = kinshipName
        return GroupChatUiState(
            initSocketListener = { initSocketListener(it) },
            onMemberClickButton = {
                SocketClass.getSocket(accessToken)
                    ?.emit(Constants.Socket.ROOM_DISCONNECT, roomDisconnect())
                navigateToBottomNavigationScreen(
                    context,
                    navigate,
                    Constants.ContainerScreens.MEMBER_SCREEN
                )
            },
            onGalleryClickButton = {
                SocketClass.getSocket(accessToken)
                    ?.emit(Constants.Socket.ROOM_DISCONNECT, roomDisconnect())
                navigateToWithOutBottomNavigationScreen(
                    context,
                    navigate,
                    Constants.ContainerScreens.GALLERY_SCREEN
                )
            },
            messageId = messageId,
            onLinkButtonButton = {
                SocketClass.getSocket(accessToken)
                    ?.emit(Constants.Socket.ROOM_DISCONNECT, roomDisconnect())
                navigateToBottomNavigationScreen(
                    context,
                    navigate,
                    Constants.ContainerScreens.LINK_SCREEN
                )
            },
            showLoader = showLoader,
            onLikeMessageButton = {
                SocketClass.getSocket(accessToken)
                    ?.emit(Constants.Socket.ROOM_DISCONNECT, roomDisconnect())
                navigateToWithOutBottomNavigationScreen(
                    context,
                    navigate,
                    Constants.ContainerScreens.IS_LIKE
                )
            },
            isFlag = isFlag,
            onIsFlagDialog = { _isFlag.value = it },
            onSearchMessageButton = {
                SocketClass.getSocket(accessToken)
                    ?.emit(Constants.Socket.ROOM_DISCONNECT, roomDisconnect())
                navigateToWithOutBottomNavigationScreen(
                    context,
                    navigate,
                    Constants.ContainerScreens.SEARCH_SCREEN
                )
            },
            msgListFlow = msgListFlow,
            onBackClick = {
                // navigate(NavigationAction.Pop())
                navigateToHomeScreen(context, navigate, coroutineScope)
                SocketClass.getSocket(accessToken)
                    ?.emit(Constants.Socket.ROOM_DISCONNECT, roomDisconnect())
            },
            kinshipReason = getUserData(coroutineScope)?.profile?.kinshipReason ?: 0,
            kinshipName = this.kinshipName,
            memberCount = getCreateGroupData(coroutineScope)?.count.toString(),
            msgValue = msgValue,
            onMsgValueChange = { msgValue.value = it },
            onMessageSend = { msg, type -> sendMsg(msg, type, coroutineScope) },
            openKinshipEditNameDialog = openKinshipEditNameDialog,
            onKinshipEditNameDialog = { openKinshipEditNameDialog.value = it },
            apiChatListResultFlow = apiChatListResultFlow,
            captureUri = captureUri,
            hideSendMessageView = hideSendMessageView,
            onHideSendMessageView = { hideSendMessageView.value = it },
            navigateToSearchScreen = { navigate(NavigationAction.Navigate(SearchRoute.createRoute())) },
            profilePicFlow = profileImgFlow,
            apiSearchChat = apiSearchChatList,
            onProfileImgPick = { profileImgFlow.value = it;launchCamera.value = false },
            launchCamera = launchCamera,
            onClearUnUsedUseState = { clearUnUsedUseState() },
            sendMessageAPICall = { callSendImageAPI(coroutineScope, context) },
            onIsLikeAndDislikeAPICall = {
                isLikeDislikeId.value = it
                isLikeDislikeId.value.contains(it)
                makeIsLikeAndDislikeReq(coroutineScope, context)
            },
            onClickOfCamera = { createUriForCaptureImg(it) },
            senderName = { senderName = it },
            apiResultFlow = apiResultFlow,
            userFlagAPICall = { callUserFlagAPI(coroutineScope, context) },
            kinshipNameEdit = kinshipNameFlow,
            onKinshipNameValueChange = {
                kinshipNameFlow.value = it
            },
            clearKinshipNameEditState = {
                clearKinshipNameEditState()
            },
            kinshipNameEditAPICall = {
                if (kinshipNameFlow.value.isBlank()) {
                    Toasty.warning(
                        context,
                        context.getString(R.string.please_enter_Kinship_name),
                        Toast.LENGTH_SHORT,
                        false
                    ).show()
                } else {
                    try {
                        SocketClass.getSocket(accessToken)?.let { it1 ->
                            if (!it1.connected()) {
                                SocketClass.connectSocket(accessToken)
                            }
                            it1.emit(
                                Constants.Socket.SEND_MESSAGE,
                                sendGroupName(
                                    kinshipNameFlow.value,
                                    coroutineScope
                                ),
                            )
                            val tempTime: Long = Date().formatDateTimeToUTCTimeStamp()
                            _kinshipName.value = kinshipNameFlow.value
                            openKinshipEditNameDialog.value = false
                            val message = KinshipGroupChatListData(
                                senderId = senderID,
                                image = image,
                                message = kinshipNameFlow.value,
                                receiverId = receiverID,
                                name = context.getString(R.string.you),
                                profileImage = getUserData(coroutineScope)?.profile?.profileImage,
                                createdAt = tempTime,
                                type = 2
                            )
                            msgListFlow.value = tempMsgList.toList()
                        }
                    } catch (e: Exception) {
                        loggerE("JSONException: ${e.message}")
                    }
                    // makeKinshipNameEditInReq(coroutineScope, context)
                }
            },
            onLoadNextPage = {
                initializeState(coroutineScope, messageId, context, subId = subId)
            },

            isLoading = isLoader,
            screenName = this.screenName,
            onNotificationBackClick={
                navigate(NavigationAction.Pop())
            },
            messageMainID = this.messageId,
            tempAccessToken = tempAccessToken


        )
    }
    private fun navigateToHomeScreen(
        context: Context,
        navigate: (NavigationAction) -> Unit,
        coroutineScope: CoroutineScope
    ) {
        coroutineScope.launch {
            val intent = Intent(context, MainActivity::class.java)
            navigate(NavigationAction.NavigateIntent(intent = intent, finishCurrentActivity = true))
        }
    }


    private fun initializeState(
        coroutineScope: CoroutineScope,
        messageId: String,
        context: Context,
        subId: String
    ) {
        Log.d("TAG", "messageId: $messageId")
        runBlocking {
            accessToken = getAccessToken(coroutineScope)
            groupId = getCreateGroupData(coroutineScope)?.chatGroupId.orEmpty()
            senderID = getUserData(coroutineScope)?._id.orEmpty()
            accessToken = getAccessToken(coroutineScope)
            name = getUserFullName(coroutineScope)
            image = getUserData(coroutineScope)?.profile?.profileImage.orEmpty()
            if (messageId.isEmpty()) {
                callKinshipGroupChatListAPI(
                    coroutineScope,
                    groupId,
                    2,
                    0,
                    "",
                    page = currentPage,
                    context
                )

            } else {
                if (screenName.value==Constants.AppScreen.NOTIFICATION_SCREEN){
                    callUserNotificationRedirectAPICall(coroutineScope = coroutineScope, context = context, communityId = messageId, notificationType =1, subId = subId )
                }else{

                    callSearchChatAPI(messageId, context, coroutineScope)
                }
            }
        }
    }


    private fun callUserNotificationRedirectAPICall(
        coroutineScope: CoroutineScope,
        context: Context,
        communityId: String,
        notificationType:Int,
        subId: String,
    ) {
        Logger.e("myCommunities: API called")
        coroutineScope.launch {
            apiRepository.userKinshipGroupNotificationRedirect(mainId = communityId, subId = subId, type = notificationType).collect {
                when (it) {
                    is NetworkResult.Error -> {
                        showLoader.value = false
                        Toasty.error(context, it.message.toString(), Toast.LENGTH_SHORT, false)
                            .show()
                    }
                    is NetworkResult.Loading -> {
                        showLoader.value = true
                    }
                    is NetworkResult.Success -> {
                        showLoader.value = false
                        apiSearchChatList.value = it.data?.data ?: emptyList()
                    }
                    is NetworkResult.UnAuthenticated -> {
                        showLoader.value = false
                        Toasty.warning(context, it.message.toString(), Toast.LENGTH_SHORT, false)
                            .show()
                    }

                }
            }
        }
    }
    private fun getUserFullName(coroutineScope: CoroutineScope): String {
        val userData = getUserData(coroutineScope)
        return userData?.profile?.firstName.orEmpty() + " " + userData?.profile?.lastName.orEmpty()
    }
    private fun createUriForCaptureImg(context: Context) {
        val file = context.createImageFile()
        val uri = FileProvider.getUriForFile(
            Objects.requireNonNull(context),
            BuildConfig.APPLICATION_ID + ".provider", file
        )
        Logger.e("capture uri: $uri")
        captureUri.value = uri
        launchCamera.value = true
    }
    @SuppressLint("SimpleDateFormat")
    fun Context.createImageFile(): File {
        // Create an image file name
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val imageFileName = "JPEG_" + timeStamp + "_"
        val image = File.createTempFile(
            imageFileName, /* prefix */
            ".jpg", /* suffix */
            externalCacheDir      /* directory */
        )
        return image
    }

    private fun initSocketListener(context: Context) {
        (context.requireActivity() as ContainerActivity).initSocketListener(this@GetGroupChatUiStateUseCase)
    }

    private fun getCreateGroupData(coroutineScope: CoroutineScope): CreateGroup? {
        var data: CreateGroup? = null
        coroutineScope.launch { data = appPreferenceDataStore.getCreateGroupData() }
        return data
    }

    private fun getAccessToken(coroutineScope: CoroutineScope): String {
        var data = ""
        coroutineScope.launch {
            data = appPreferenceDataStore.getUserData()?.auth?.accessToken.toString()
        }
        return data
    }

    private fun getUserData(coroutineScope: CoroutineScope): UserAuthResponseData? {
        var data: UserAuthResponseData? = null
        coroutineScope.launch { data = appPreferenceDataStore.getUserData() }
        return data
    }

    private fun callSearchChatAPI(
        messageId: String,
        context: Context,
        coroutineScope: CoroutineScope
    ) {
        coroutineScope.launch {
            apiRepository.searchChat(messageId, groupId).collect {
                when (it) {
                    is NetworkResult.Error -> {
                        showLoader.value = false
                        Toasty.error(context, it.message.toString(), Toast.LENGTH_SHORT, false)
                            .show()
                    }

                    is NetworkResult.Loading -> {
                        showLoader.value = true
                    }

                    is NetworkResult.Success -> {
                        apiSearchChatList.value = it.data?.data ?: emptyList()
                        showLoader.value = false
                    }

                    is NetworkResult.UnAuthenticated -> {
                        showLoader.value = false
                        Toasty.error(context, it.message.toString(), Toast.LENGTH_SHORT, false)
                            .show()
                    }
                }
            }
        }
    }

    private fun navigateToBottomNavigationScreen(
        context: Context,
        navigate: (NavigationAction) -> Unit,
        screenName: String
    ) {
        val intent = Intent(context, MainActivity::class.java)
        intent.putExtra(Constants.IntentData.SCREEN_NAME, screenName)
        navigate(NavigationAction.NavigateIntent(intent = intent, finishCurrentActivity = false))
    }

    private fun navigateToWithOutBottomNavigationScreen(
        context: Context,
        navigate: (NavigationAction) -> Unit,
        screenName: String
    ) {
        val intent = Intent(context, ContainerActivity::class.java)
        intent.putExtra(Constants.IntentData.SCREEN_NAME, screenName)
        navigate(NavigationAction.NavigateIntent(intent = intent, finishCurrentActivity = false))
    }

    private fun callKinshipGroupChatListAPI(
        coroutineScope: CoroutineScope,
        id: String,
        type: Int,
        imageOrLinkType: Int,
        search: String,
        page: Int,
        context: Context
    ) {
        isLoader.value = page != 1
        coroutineScope.launch {
            apiRepository.getKinshipGroupChatList(
                id,
                type,
                imageOrLinkType,
                search,
                page = page
            ).collect { result ->
                when (result) {
                    is NetworkResult.Error -> {
                        isLoader.value = false
                        showLoader.value = false
                        Toasty.error(context, result.message.toString(), Toast.LENGTH_SHORT, false)
                            .show()
                    }

                    is NetworkResult.Loading -> {
                        if (page == 1) showLoader.value = true else isLoader.value = true
                    }

                    is NetworkResult.Success -> {
                        isLoader.value = false
                        showLoader.value = false
                        noDataFoundText.value = false
                        val newData = result.data?.data ?: emptyList()
                        _isFlag.value = result.data?.flag ?: false
                        _apiChatListResultFlow.update { it + newData }

                    }

                    is NetworkResult.UnAuthenticated -> {
                        isLoader.value = false
                        showLoader.value = false
                        Toasty.warning(
                            context,
                            result.message.toString(),
                            Toast.LENGTH_SHORT,
                            false
                        ).show()
                    }
                }
            }
            currentPage++
        }
    }
    private fun callUserFlagAPI(
        coroutineScope: CoroutineScope,
        context: Context,
    ) {
        coroutineScope.launch {
            apiRepository.userFlag().collect {
                when (it) {
                    is NetworkResult.Error -> {
                        showLoader.value = false
                        Toasty.error(context, it.message.toString(), Toast.LENGTH_SHORT, false)
                            .show()
                    }
                    is NetworkResult.Loading -> {
                        showLoader.value = true
                    }
                    is NetworkResult.Success -> {
                        showLoader.value = false
                    }
                    is NetworkResult.UnAuthenticated -> {
                        showLoader.value = false
                        Toasty.error(context, it.message.toString(), Toast.LENGTH_SHORT, false)
                            .show()
                    }
                }
            }
        }
    }

    private fun clearKinshipNameEditState() {
        kinshipNameFlow.value = _kinshipName.value.trim()
        openKinshipEditNameDialog.value = false
    }

    private fun initSocket() {
        try {
            runBlocking {
                SocketClass.getSocket(accessToken)?.let {
                    if (!it.connected()) {
                        SocketClass.connectSocket(accessToken)
                    }
                    it.emit(Constants.Socket.CREATE_ROOM, createRoom())
                    it.emit(Constants.Socket.UPDATE_STATUS_TO_ONLINE, updateStatusToOnline())
                }
            }
        } catch (e: Exception) {
            Log.e("TAG", "initSocket Activity ==: ${e.message}")
        }
    }
    private fun createRoom(): JSONObject {
        val userObject = JSONObject()
        userObject.put(Constants.Socket.SENDER_ID, senderID)
        userObject.put(Constants.Socket.RECEIVER_ID, receiverID)
        userObject.put(Constants.Socket.GROUP_ID, groupId)
        Log.e("Socket createRoom ", userObject.toString())
        return userObject
    }
    private fun roomDisconnect(): JSONObject {
        val userObject = JSONObject()
        userObject.put(Constants.Socket.SENDER_ID, senderID)
        userObject.put(Constants.Socket.RECEIVER_ID, receiverID)
        userObject.put(Constants.Socket.GROUP_ID, groupId)
        userObject.put(Constants.Socket.ROOM_ID, roomId)
        Log.e("Socket roomDisconnect ", userObject.toString())
        return userObject
    }
    private fun updateStatusToOnline(): JSONObject {
        val statusObject = JSONObject()
        statusObject.put(Constants.Socket.SENDER_ID, senderID)
        statusObject.put(Constants.Socket.RECEIVER_ID, receiverID)
        statusObject.put(Constants.Socket.GROUP_ID, groupId)
        Log.e("updateStatusToOnline ", statusObject.toString())
        return statusObject
    }
    private fun sendMsg(msg: String, type: Int, coroutineScope: CoroutineScope) {
        if (msg.isNotEmpty()) {
            try {
                SocketClass.getSocket(accessToken)?.let {
                    if (!it.connected()) {
                        SocketClass.connectSocket(accessToken)
                    }
                    if (type == 1 || type == 3) {
                        it.emit(
                            Constants.Socket.SEND_MESSAGE,
                            sendMessage(
                                message = msg,
                                messageType = type,
                                coroutineScope
                            ),
                        )
                    }
                }
            } catch (e: Exception) {
                loggerE("JSONException: ${e.message}")
            }
            val tempTime: Long = Date().formatDateTimeToUTCTimeStamp()
            val message = KinshipGroupChatListData(
                senderId = senderID,
                message = msg,
                receiverId = receiverID,
                name = "You",
                image = msg,
                profileImage = getUserData(coroutineScope)?.profile?.profileImage,
                createdAt = tempTime,
                type = type,
            )
            /* val updatedList = msgListFlow.value.toMutableList().apply {
                 add(0, message)  // Add the message to the top since reverse layout is enabled
             }
             msgListFlow.value = updatedList*/
            // tempMsgList.add(0, message)
        }
    }

    private fun makeIsLikeAndDislikeReq(coroutineScope: CoroutineScope, context: Context) {
        val isLikeAndDislike = IsLikeDislikeRequest(
            messageId = isLikeDislikeId.value
        )
        callIsLikeAndDislike(coroutineScope, isLikeAndDislike, context)
    }

    private fun callIsLikeAndDislike(
        coroutineScope: CoroutineScope,
        isLikeAndDislike: IsLikeDislikeRequest,
        context: Context,
    ) {
        Log.d("TAG", "callIsLikeAndDislike: $isLikeDislikeId")
        coroutineScope.launch {
            apiRepository.isLikeDislike(isLikeAndDislike).collect {
                when (it) {
                    is NetworkResult.Error -> {
                        showLoader.value = false
                        Toasty.error(context, it.message.toString(), Toast.LENGTH_SHORT, false)
                            .show()
                    }
                    is NetworkResult.Loading -> {
                        showLoader.value = true
                    }
                    is NetworkResult.Success -> {
                        showLoader.value = false
                    }
                    is NetworkResult.UnAuthenticated -> {
                        showLoader.value = false
                        Toasty.error(context, it.message.toString(), Toast.LENGTH_SHORT, false)
                            .show()
                    }
                }
            }
        }
    }

    private fun clearUnUsedUseState() {
        launchCamera.value = false
    }

    private fun callSendImageAPI(
        coroutineScope: CoroutineScope,
        context: Context,
    ) {
        val map: HashMap<String, RequestBody> = hashMapOf()
        map[Constants.SendImage.GROUP_ID] =
            groupId.toRequestBody("multipart/form-data".toMediaTypeOrNull())
        map[Constants.SendImage.RECEIVER_ID] =
            receiverID.toRequestBody("multipart/form-data".toMediaTypeOrNull())
        if (profileImgFlow.value.isNotBlank() && msgValue.value.isNotBlank()) {
            map[Constants.SendImage.TYPE] =
                4.toString().toRequestBody("multipart/form-data".toMediaTypeOrNull())
        } else {
            map[Constants.SendImage.TYPE] =
                2.toString().toRequestBody("multipart/form-data".toMediaTypeOrNull())
        }
        map[Constants.SendImage.MESSAGE] =
            msgValue.value.toRequestBody("multipart/form-data".toMediaTypeOrNull())
        val profileImageFile = File(profileImgFlow.value)
        val profileImage =
            AppUtils.createMultipartBody(profileImageFile, Constants.SendImage.FILE)
        coroutineScope.launch {
            apiRepository.sendMessage(map, profileImage).collect {
                when (it) {
                    is NetworkResult.Error -> {
                        showLoader.value = false
                        Toasty.error(context, it.message.toString(), Toast.LENGTH_SHORT, false)
                            .show()
                    }

                    is NetworkResult.Loading -> {
                        showLoader.value = true
                    }

                    is NetworkResult.Success -> {
                        showLoader.value = false
                        val image = it.data?.data?.image
                        val sendMessageChatId = it.data?.data?._id ?: ""
                        val type = it.data?.data?.type
                        val message = it.data?.data?.message
                        try {
                            SocketClass.getSocket(accessToken)?.let { it1 ->
                                if (!it1.connected()) {
                                    SocketClass.connectSocket(accessToken)
                                }
                                it1.emit(
                                    Constants.Socket.SEND_MESSAGE,
                                    sendImage(
                                        message = message.toString(),
                                        sendMessageChatId = sendMessageChatId,
                                        messageType = type ?: 0,
                                        image = image.toString(),
                                        coroutineScope = coroutineScope

                                    ),
                                )
                                val tempTime: Long = Date().formatDateTimeToUTCTimeStamp()
                                val message = KinshipGroupChatListData(
                                    senderId = senderID,
                                    image = image,
                                    message = message,
                                    receiverId = receiverID,
                                    name = "You",
                                    profileImage = getUserData(coroutineScope)?.profile?.profileImage,
                                    createdAt = tempTime,
                                    type = 2
                                )
                                //  tempMsgList.add(0, message)
                                msgListFlow.value = tempMsgList.toList()
                            }
                        } catch (e: Exception) {
                            loggerE("JSONException: ${e.message}")
                        }
                    }

                    is NetworkResult.UnAuthenticated -> {
                        showLoader.value = false
                        Toasty.error(context, it.message.toString(), Toast.LENGTH_SHORT, false)
                            .show()
                    }
                }
            }
        }
    }

    private fun sendMessage(
        message: String,
        messageType: Int,
        coroutineScope: CoroutineScope
    ): JSONObject {
        val msgObject = JSONObject()
        msgObject.put(Constants.Socket.MESSAGE, message)
        msgObject.put(Constants.Socket.SENDER_ID, senderID)
        msgObject.put(Constants.Socket.PROFILE, getUserData(coroutineScope)?.profile?.profileImage)
        msgObject.put(
            Constants.Socket.SENDER_NAME,
            getUserData(coroutineScope)?.profile?.firstName.plus(" ")
                .plus(getUserData(coroutineScope)?.profile?.lastName),
        )
        msgObject.put(Constants.Socket.RECEIVER_ID, receiverID)
        msgObject.put(Constants.Socket.GROUP_ID, groupId)
        msgObject.put(Constants.Socket.MESSAGE_TYPE, messageType)
        msgObject.put(Constants.Socket.ROOM_ID, roomId)
        Log.e("TAG", "sendMessage: $msgObject")
        return msgObject
    }

    private fun sendImage(
        message: String,
        messageType: Int,
        image: String,
        sendMessageChatId: String,
        coroutineScope: CoroutineScope,
    ): JSONObject {
        val msgObject = JSONObject()
        msgObject.put(Constants.Socket.MESSAGE, message)
        msgObject.put(Constants.Socket.SENDER_ID, senderID)
        msgObject.put(Constants.Socket.RECEIVER_ID, receiverID)
        msgObject.put(Constants.Socket.IMAGE, image)
        msgObject.put(Constants.Socket.GROUP_ID, groupId)
        msgObject.put(
            Constants.Socket.SENDER_NAME,
            getUserData(coroutineScope)?.profile?.firstName.plus(" ")
                .plus(getUserData(coroutineScope)?.profile?.lastName),
        )
        msgObject.put(Constants.Socket.MESSAGE_TYPE, messageType)
        msgObject.put(Constants.Socket.ROOM_ID, roomId)
        msgObject.put(Constants.Socket.CHAT_ID, sendMessageChatId)
        Log.e("TAG", "sendMessage: $msgObject")
        return msgObject
    }

    private fun sendGroupName(groupName: String, coroutineScope: CoroutineScope): JSONObject {
        val msgObject = JSONObject()
        msgObject.put(Constants.Socket.MESSAGE, groupName)
        msgObject.put(Constants.Socket.SENDER_ID, senderID)
        msgObject.put(Constants.Socket.RECEIVER_ID, receiverID)
        msgObject.put(Constants.Socket.IMAGE, image)
        msgObject.put(Constants.Socket.GROUP_ID, groupId)
        msgObject.put(
            Constants.Socket.SENDER_NAME,
            getUserData(coroutineScope)?.profile?.firstName.plus(" ")
                .plus(getUserData(coroutineScope)?.profile?.lastName),
        )
        msgObject.put(Constants.Socket.MESSAGE_TYPE, 5)
        msgObject.put(Constants.Socket.ROOM_ID, roomId)
        Log.e("TAG", "sendMessage: $msgObject")
        return msgObject
    }

    override fun onRoomConnected(roomID: String) {
        loggerE("onRoom Connected: roomId: $roomID")
        this.roomId = roomID
    }

    override fun onNewMessageReceived(data: JSONObject) {
        try {
            data.let {
                loggerE("message response: $data")
                val messageType = it.optInt(Constants.Socket.MESSAGE_TYPE)
                val profile = it.optString(Constants.Socket.PROFILE, "")
                val chatId = it.optString(Constants.Socket.CHAT_ID, "")
                val messageContent = it.optString(Constants.Socket.MESSAGE, "")
                val senderId = it.optString(Constants.Socket.SENDER_ID, "")
                val receiverId = it.optString(Constants.Socket.RECEIVER_ID, "")
                val senderName = it.optString(Constants.Socket.SENDER_NAME, "")
                val createdAt = it.optLong(Constants.Socket.CREATED_AT, 0L)
                val imageUrl = it.optString(Constants.Socket.IMAGE, "")
                val message = KinshipGroupChatListData(
                    id = chatId,
                    message = messageContent,
                    profileImage = profile,
                    image = imageUrl,
                    type = messageType,
                    senderId = senderId,
                    receiverId = receiverId,
                    name = if (senderID == senderId) "You" else senderName,
                    createdAt = createdAt
                )
                if (!tempMsgList.contains(message)&&  it.optString(Constants.Socket.GROUP_ID, "")!="" && it.optString(Constants.Socket.GROUP_ID, "") == groupId){
                    tempMsgList.add(0, message)
                    msgListFlow.value = tempMsgList
                }
            }
        } catch (e: JSONException) {
            loggerE("JSONException: ${e.message}")
        } catch (e: Exception) {
            loggerE("Exception: ${e.message}")
        }
    }
}