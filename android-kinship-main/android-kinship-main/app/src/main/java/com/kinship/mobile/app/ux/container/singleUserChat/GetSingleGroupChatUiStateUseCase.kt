package com.kinship.mobile.app.ux.container.singleUserChat

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.mutableStateListOf
import androidx.core.content.FileProvider
import androidx.paging.PagingData
import co.touchlab.kermit.Logger
import com.google.gson.Gson
import com.kinship.mobile.app.AppActivity
import com.kinship.mobile.app.BuildConfig
import com.kinship.mobile.app.data.source.Constants
import com.kinship.mobile.app.data.source.local.datastore.AppPreferenceDataStore
import com.kinship.mobile.app.data.source.remote.helper.NetworkResult
import com.kinship.mobile.app.data.source.remote.repository.ApiRepository
import com.kinship.mobile.app.model.domain.response.chat.KinshipGroupChatListData
import com.kinship.mobile.app.model.domain.response.chat.grpObj
import com.kinship.mobile.app.model.domain.response.chat.userGroup.MessageTabResponse
import com.kinship.mobile.app.model.domain.response.chat.userGroup.addMember.groupMember.UserGroupMember
import com.kinship.mobile.app.model.domain.response.group.CreateGroup
import com.kinship.mobile.app.model.domain.response.group.GroupMember
import com.kinship.mobile.app.model.domain.response.signUp.UserAuthResponseData
import com.kinship.mobile.app.navigation.NavigationAction
import com.kinship.mobile.app.utils.AppUtils
import com.kinship.mobile.app.utils.DateTimeUtils.formatDateTimeToUTCTimeStamp
import com.kinship.mobile.app.utils.ext.requireActivity
import com.kinship.mobile.app.utils.socket.OnSocketEventsListener
import com.kinship.mobile.app.utils.socket.SocketClass
import com.kinship.mobile.app.ux.container.ContainerActivity
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
import javax.inject.Inject

class GetSingleGroupChatUiStateUseCase
@Inject constructor(
    private val apiRepository: ApiRepository,
    private val appPreferenceDataStore: AppPreferenceDataStore
) : OnSocketEventsListener, AppActivity() {
    private val msgValue = MutableStateFlow("")
    private val profileImgFlow = MutableStateFlow("")
    private val launchCamera = MutableStateFlow(false)
    private val openPickImgDialog = MutableStateFlow(false)

    private val captureUri = MutableStateFlow<Uri?>(null)
    private var roomId: String = ""
    private var senderID: String = ""
    private var groupId: String = ""
    private var accessToken = ""
    private var name = ""
    private var image = ""
    private val tempMsgList = mutableStateListOf<KinshipGroupChatListData>()
    private val apiSingleUserListResultFlow =
        MutableStateFlow<PagingData<KinshipGroupChatListData>>(
            PagingData.empty()
        )
    private var selectedUsers = mutableListOf<String>()

    private val _apiUserGroupUserListResultFlow =
        MutableStateFlow<List<KinshipGroupChatListData>>(emptyList())
    private val apiUserGroupUserListResultFlow: StateFlow<List<KinshipGroupChatListData>> =
        _apiUserGroupUserListResultFlow
    private val apiUserMemberList = MutableStateFlow<List<UserGroupMember>>(emptyList())
    private var isMessageDataFetched = false
    private var oneTimeRoomConnect = false
    private val msgListFlow = MutableStateFlow<List<KinshipGroupChatListData>>(emptyList())
    private val showLoader = MutableStateFlow(false)
    private var sendMessageChatId = ""
    private val receiverId = MutableStateFlow("")
    private var userGroupId = MutableStateFlow("")
    private var chatGroupId = MutableStateFlow("")
    private val userId = MutableStateFlow("")
    private val memberUserId = MutableStateFlow("")

    private val notificationProfileData = MutableStateFlow(grpObj())

    private val type = MutableStateFlow(0)
    private var messageData = MutableStateFlow("")
    private var currentPage = 1
    private val isLoader = MutableStateFlow(false)
    private val noDataFoundText = MutableStateFlow(false)
    private var messageChatGroupId = MutableStateFlow("")
    private val memberChatGroupId = MutableStateFlow("")

    private var memberChatId = MutableStateFlow("")
    private var chatGroupId2 = MutableStateFlow("")
    private val _screenName = MutableStateFlow("")
    private val screenName: StateFlow<String> get() = _screenName


    private val _subId = MutableStateFlow("")
    private val subId: StateFlow<String> get() = _subId

    operator fun invoke(
        context: Context,
        coroutineScope: CoroutineScope,
        mainId: String,
        subId: String,
        screenName: String,
        isSingle:Boolean,
        navigate: (NavigationAction) -> Unit,
    ): SingleGroupChatUiState {
        initializeState(coroutineScope)
        this._screenName.value = screenName
        Log.d(TAG, "invoke: $isSingle")
        this._subId.value = subId
        Log.d("TAG", "imageProfile: ${profileImgFlow.value}")
        Log.d("TAG", "invoke: ${messageChatGroupId.value} ${memberChatId.value}")
        return SingleGroupChatUiState(
            msgValue = msgValue,
            onMsgValueChange = { msgValue.value = it},
            profilePicFlow = profileImgFlow,
            initSocketListener = { initSocketListener(it) },
            onProfileImgPick = { profileImgFlow.value = it; launchCamera.value = false },
            openPickImgDialog = openPickImgDialog,
            onOpenORDismissDialog = { openPickImgDialog.value = it },
            captureUri = captureUri,
            onClearUnUsedUseState = {clearUnUsedUseState()},
            msgListFlow = msgListFlow,
            showLoader = showLoader,
            launchCamera = launchCamera,
            onBackClick = {
                if (type.value == 1) {
                    navigate(NavigationAction.PopIntent)
                    SocketClass.getSocket(accessToken)
                        ?.emit(Constants.Socket.ROOM_DISCONNECT, roomDisconnect())
                } else {
                    if (screenName==Constants.AppScreen.NOTIFICATION_SCREEN){
                        navigate(NavigationAction.Pop())
                    }else{
                        SocketClass.getSocket(accessToken)
                            ?.emit(Constants.Socket.ROOM_DISCONNECT, roomDisconnectSingleUser(navigate))
                    }
                }
            },
            onClickOfCamera = { createUriForCaptureImg(it) },
            apiSingleListResultFlow = apiSingleUserListResultFlow,
            apiUserGroupListResultFlow = apiUserGroupUserListResultFlow,
            sendMessageData = { messageData, chatGroupId, chatId, chatGroupId2 ->
                this.messageData.value = messageData
                if (!isMessageDataFetched) {
                    setMessageDataToFlow(
                        messageData,
                        coroutineScope,
                        context,
                        chatGroupId,
                        chatId,
                        chatGroupId2,
                        mainId = mainId,
                        subId = subId
                    )
                    isMessageDataFetched = true
                }
            },
            sendMemberData = { memberData, chatId ->
                setMemberDataToFlow(
                    memberData = memberData,
                    chatId = chatId,
                    coroutineScope = coroutineScope,
                    context = context
                )
            },
            apiMemberList = apiUserMemberList,
            userMemberListAPICall = {
                callUserGroupMemberList(coroutineScope, context)
            },
            sendMessageAPICall = {
                if (messageData.value.isNotEmpty()) {
                    callSendImageAPI(coroutineScope, context)
                } else {
                    callSendImageMemberAPI(coroutineScope = coroutineScope, context = context)
                }
            },
            onMessageSend = { msg, type ->
                sendMsg(msg, type, coroutineScope, context)
            },
            onLoadSingleGroupNextPage = {
                callKinshipGroupChatListAPI(
                    coroutineScope = coroutineScope,
                    userGroupId.value,
                    2,
                    0,
                    "",
                    page = currentPage,
                    context
                )

            },
            isLoading = isLoader,
            showEventNoFoundText = noDataFoundText,
            onShowLoaderDismissDialog = {
                showLoader.value = it
            },

            screenName = this.screenName,
            notificationProfileData = notificationProfileData,
            subId = this.subId,
            groupid = userGroupId
        )
    }

    private fun callSendImageAPI(
        coroutineScope: CoroutineScope,
        context: Context,
    ) {
        val id2 =
            if (memberChatGroupId.value.isNullOrEmpty()) memberChatId.value else memberChatGroupId.value
        val id = if (userGroupId.value.isNullOrEmpty()) chatGroupId2.value else userGroupId.value
        if (profileImgFlow.value.isBlank()) return // Do not proceed if no image is selected
        val map: HashMap<String, RequestBody> = hashMapOf()
        map[Constants.SendImage.GROUP_ID] =
            id.toRequestBody("multipart/form-data".toMediaTypeOrNull())
        if (memberChatGroupId.value.isNotEmpty() || memberChatId.value.isNotEmpty()) {
            map[Constants.SendImage.RECEIVER_ID] =
                id2.toRequestBody("multipart/form-data".toMediaTypeOrNull())
        } else {
            map[Constants.SendImage.RECEIVER_ID] =
                "".toRequestBody("multipart/form-data".toMediaTypeOrNull())
        }
        map[Constants.SendImage.TYPE] =
            if (profileImgFlow.value.isNotBlank() && msgValue.value.isNotBlank()) {
                4.toString().toRequestBody("multipart/form-data".toMediaTypeOrNull())
            } else {
                2.toString().toRequestBody("multipart/form-data".toMediaTypeOrNull())
            }
        map[Constants.SendImage.MESSAGE] =
            msgValue.value.toRequestBody("multipart/form-data".toMediaTypeOrNull())
        val profileImageFile = File(profileImgFlow.value)
        val profileImage =
            AppUtils.createMultipartBody(profileImageFile, Constants.SendImage.FILE)
        Log.d("TAG", "callSendImageAPI: $profileImageFile")
        Log.d(TAG, "id: $id")
        Log.d(TAG, "id2: $id2")
        coroutineScope.launch {
            apiRepository.sendMessage(map, profileImage).collect {
                when (it) {
                    is NetworkResult.Error -> {
                        showLoader.value = false

                    }

                    is NetworkResult.Loading -> {
                        showLoader.value = true
                    }

                    is NetworkResult.Success -> {
                        showLoader.value = false
                        val image = it.data?.data?.image
                        sendMessageChatId = it.data?.data?._id ?: ""
                        val type = it.data?.data?.type
                        val message = it.data?.data?.message
                        try {
                            SocketClass.getSocket(accessToken)?.let { socket ->
                                if (!socket.connected()) {
                                    SocketClass.connectSocket(accessToken)
                                }
                                socket.emit(
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
                                val messageData = KinshipGroupChatListData(
                                    senderId = senderID,
                                    image = image,
                                    message = message,
                                    receiverId = "",
                                    name = "You",
                                    profileImage = getUserData(coroutineScope)?.profile?.profileImage,
                                    createdAt = tempTime,
                                    type = 1
                                )
                                // tempMsgList.clear()
                                msgListFlow.value = tempMsgList.toList()

                            }
                        } catch (e: Exception) {
                            // Handle the exception appropriately
                            Log.e("SendImage", "Error sending image: ${e.message}")
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

    private fun callSendImageMemberAPI(
        coroutineScope: CoroutineScope,
        context: Context,
    ) {
        // val id2 = if (messageChatGroupId.value.isNullOrEmpty()) memberChatId.value else messageChatGroupId.value
        val id2 =
            if (memberChatGroupId.value.isNullOrEmpty()) userId.value else memberChatGroupId.value
        // if (messageChatGroupId.value.isNullOrEmpty()) if (memberChatId.value.isNullOrEmpty()) memberChatGroupId.value else memberChatId.value else messageChatGroupId.value
        // val id = if (userGroupId.value.isNullOrEmpty()) chatGroupId2.value else userGroupId.value
        //  val id = if (userGroupId.value.isNullOrEmpty()) if (memberChatId.value.isNullOrEmpty()) memberChatGroupId.value else memberChatId.value else userGroupId.value
        val id =
            if (memberChatGroupId.value.isNullOrEmpty()) userId.value else memberChatGroupId.value
        // if (userGroupId.value.isNullOrEmpty()) if (memberChatId.value.isNullOrEmpty()) memberChatGroupId.value else memberChatId.value else if (chatGroupId.value.isNullOrEmpty()) userGroupId.value else chatGroupId.value
        if (profileImgFlow.value.isBlank()) return // Do not proceed if no image is selected
        val map: HashMap<String, RequestBody> = hashMapOf()
        map[Constants.SendImage.GROUP_ID] =
            id.toRequestBody("multipart/form-data".toMediaTypeOrNull())

        map[Constants.SendImage.RECEIVER_ID] =
            id2.toRequestBody("multipart/form-data".toMediaTypeOrNull())

        map[Constants.SendImage.TYPE] =
            if (profileImgFlow.value.isNotBlank() && msgValue.value.isNotBlank()) {
                4.toString().toRequestBody("multipart/form-data".toMediaTypeOrNull())
            } else {
                2.toString().toRequestBody("multipart/form-data".toMediaTypeOrNull())
            }
        map[Constants.SendImage.MESSAGE] =
            msgValue.value.toRequestBody("multipart/form-data".toMediaTypeOrNull())
        val profileImageFile = File(profileImgFlow.value)
        val profileImage =
            AppUtils.createMultipartBody(profileImageFile, Constants.SendImage.FILE)
        Log.d("TAG", "callSendImageAPI: $profileImageFile")
        Log.d(TAG, "id: $id")
        Log.d(TAG, "id2: $id2")
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
                        sendMessageChatId = it.data?.data?._id ?: ""
                        val type = it.data?.data?.type
                        val message = it.data?.data?.message
                        try {
                            SocketClass.getSocket(accessToken)?.let { socket ->
                                if (!socket.connected()) {
                                    SocketClass.connectSocket(accessToken)
                                }
                                socket.emit(
                                    Constants.Socket.SEND_MESSAGE,
                                    memberSendImage(
                                        message = message.toString(),
                                        sendMessageChatId = sendMessageChatId,
                                        messageType = type ?: 0,
                                        image = image.toString(),
                                        coroutineScope = coroutineScope
                                    ),
                                )
                                val tempTime: Long = Date().formatDateTimeToUTCTimeStamp()
                                val messageData = KinshipGroupChatListData(
                                    senderId = senderID,
                                    image = image,
                                    message = message,
                                    receiverId = "",
                                    name = "You",
                                    profileImage = getUserData(coroutineScope)?.profile?.profileImage,
                                    createdAt = tempTime,
                                    type = 1
                                )

                                msgListFlow.value = tempMsgList.toList()
                                // msgListFlow.value = tempMsgList.toList()
                                // tempMsgList.clear()
                                //   msgListFlow.value = tempMsgList.toList()

                            }
                        } catch (e: Exception) {
                            // Handle the exception appropriately
                            Log.e("SendImage", "Error sending image: ${e.message}")
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

    private fun createUriForCaptureImg(context: Context) {
        val file = context.createImageFile()
        val uri =
            FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".provider", file)
        Logger.e("capture uri: $uri")
        captureUri.value = uri
        launchCamera.value = true
    }

    @SuppressLint("SimpleDateFormat")
    private fun Context.createImageFile(): File {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val imageFileName = "JPEG_${timeStamp}_"
        return File.createTempFile(imageFileName, ".jpg", externalCacheDir)
    }

    private fun roomDisconnect(): JSONObject {
        val userObject = JSONObject()
        userObject.put(Constants.Socket.SENDER_ID, senderID)
        userObject.put(Constants.Socket.RECEIVER_ID, "")
        userObject.put(Constants.Socket.GROUP_ID, userGroupId.value)
        userObject.put(Constants.Socket.ROOM_ID, roomId)
        Log.d("TAG", "roomDisconnectSingleUser: $roomId")
        Log.e("Socket roomDisconnect ", userObject.toString())
        return userObject
    }

    private fun roomDisconnectSingleUser(navigate: (NavigationAction) -> Unit): JSONObject {
        val userObject = JSONObject()
        userObject.put(Constants.Socket.SENDER_ID, senderID)
        userObject.put(Constants.Socket.RECEIVER_ID, if (receiverId.value.isNotEmpty()) receiverId.value else memberUserId.value)
        userObject.put(Constants.Socket.GROUP_ID, "")
        userObject.put(Constants.Socket.ROOM_ID, roomId)
        navigate(NavigationAction.PopIntent)
        Log.e("Socket roomDisconnect ", userObject.toString())
        return userObject
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
        msgObject.put(Constants.Socket.RECEIVER_ID, if (type.value == 2) receiverId.value else "")
        msgObject.put(Constants.Socket.IMAGE, image)
        msgObject.put(Constants.Socket.PROFILE, getUserData(coroutineScope)?.profile?.profileImage)
        msgObject.put(Constants.Socket.GROUP_ID, if (type.value == 1) userGroupId.value else "")
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

    private fun memberSendImage(
        message: String,
        messageType: Int,
        image: String,
        sendMessageChatId: String,
        coroutineScope: CoroutineScope,
    ): JSONObject {
        val msgObject = JSONObject()
        msgObject.put(Constants.Socket.MESSAGE, message)
        msgObject.put(Constants.Socket.SENDER_ID, senderID)
        msgObject.put(
            Constants.Socket.RECEIVER_ID,
            if (memberChatGroupId.value.isNullOrEmpty()) userId.value else memberChatGroupId.value
        )
        msgObject.put(Constants.Socket.IMAGE, image)
        msgObject.put(Constants.Socket.PROFILE, getUserData(coroutineScope)?.profile?.profileImage)
        msgObject.put(
            Constants.Socket.GROUP_ID,
            if (memberChatGroupId.value.isNullOrEmpty()) userId.value else memberChatGroupId.value
        )
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

    private fun setMessageDataToFlow(
        messageData: String,
        coroutineScope: CoroutineScope,
        context: Context,
        chatGroupId: String,
        chatId: String,
        chatGroupId2: String,
        mainId: String,
        subId: String,
    ) {
        this.chatGroupId2.value = chatGroupId2
        messageChatGroupId.value = chatGroupId
        memberChatId.value = chatId
        if (messageData.isNotEmpty()) {
            val data: MessageTabResponse =
                Gson().fromJson(messageData, MessageTabResponse::class.java)
            type.value = data.type
            receiverId.value = data.receiverId
            userGroupId.value = data.id
            selectedUsers = data.userId.toMutableList()
            initSocket()
            callKinshipGroupChatListAPI(
                coroutineScope = coroutineScope,
                userGroupId.value,
                2,
                0,
                "",
                page = currentPage,
                context = context
            )
        } else {
            initSocket()
            if (screenName.value == Constants.AppScreen.NOTIFICATION_SCREEN) {
                callUserNotificationRedirectAPICall(
                    coroutineScope = coroutineScope,
                    context = context,
                    mainId = mainId,
                    subId = subId
                )
            } else {
                callKinshipGroupChatListAPI(
                    coroutineScope = coroutineScope,
                    if (chatGroupId2.isNullOrEmpty()) chatId else chatGroupId2,
                    2,
                    0,
                    "",
                    page = currentPage,
                    context
                )
            }
        }
    }

    private fun callUserNotificationRedirectAPICall(
        coroutineScope: CoroutineScope,
        context: Context,
        mainId: String,
        subId: String,
    ) {
        Logger.e("myCommunities: API called")
        coroutineScope.launch {
            apiRepository.userKinshipGroupNotificationRedirect(
                mainId = mainId,
                subId = subId,
                type = 2
            ).collect {
                when (it) {
                    is NetworkResult.Error -> {
                        showLoader.value = false
                        showErrorMessage(
                            context = context,
                            errorMsg = it.message ?: "Something went wrong!"
                        )
                    }

                    is NetworkResult.Loading -> {
                        showLoader.value = true
                    }

                    is NetworkResult.Success -> {
                        showLoader.value = false
                        _apiUserGroupUserListResultFlow.value = it.data?.data ?: emptyList()
                        notificationProfileData.value = it.data?.grpObj ?: grpObj()

                    }

                    is NetworkResult.UnAuthenticated -> {
                        showLoader.value = false
                        showWarningMessage(
                            context = context,
                            errorMsg = it.message ?: "UnAuthenticated"
                        )
                    }
                }
            }
        }
    }

    private fun showErrorMessage(context: Context, errorMsg: String) {
        Toasty.error(context, errorMsg, Toast.LENGTH_SHORT, false).show()
    }

    private fun showWarningMessage(context: Context, errorMsg: String) {
        Toasty.warning(context, errorMsg, Toast.LENGTH_SHORT, false).show()
    }

    private fun setMemberDataToFlow(
        memberData: String,
        chatId: String,
        coroutineScope: CoroutineScope,
        context: Context
    ) {
        if (memberData.isNotEmpty()) {
            val data: GroupMember =
                Gson().fromJson(memberData, GroupMember::class.java)
            memberChatGroupId.value = chatId
            userId.value = data.chatGroupId
            memberUserId.value=data.userId
            chatGroupId.value = data._id
            Log.d("TAG", "chatGroupId: ${chatGroupId.value}")
            initSocket()
            callKinshipGroupChatListAPI(
                coroutineScope = coroutineScope,
                if (data.chatGroupId.isNotEmpty()) data.chatGroupId else chatId,
                2,
                0,
                "",
                page = currentPage,
                context
            )
        }
    }

    override fun onNewMessageReceived(data: JSONObject) {
        try {
            data.let {
                SocketClass.loggerE("message response: $data")
                val messageType = it.optInt(Constants.Socket.MESSAGE_TYPE)
                val messageId = it.optString(Constants.Socket.MESSAGE_ID, "")
                val profile = it.optString(Constants.Socket.PROFILE, "")
                val groupId = it.optString(Constants.Socket.GROUP_ID, "")
                val messageContent = it.optString(Constants.Socket.MESSAGE, "")
                val senderId = it.optString(Constants.Socket.SENDER_ID, "")
                val receiverId = it.optString(Constants.Socket.RECEIVER_ID, "")
                val senderName = it.optString(Constants.Socket.SENDER_NAME, "")
                val createdAt = it.optLong(Constants.Socket.CREATED_AT, 0L)
                val imageUrl = it.optString(Constants.Socket.IMAGE, "")
                val message = KinshipGroupChatListData(
                    id = messageId,
                    message = messageContent,
                    profileImage = profile,
                    groupId = groupId,
                    image = imageUrl,
                    type = messageType,
                    senderId = senderId,
                    receiverId = receiverId,
                    name = if (senderID == senderId) "You" else senderName,
                    createdAt = createdAt
                )
                if (screenName.value != Constants.AppScreen.NOTIFICATION_SCREEN) {
                    Log.d(TAG, "onNewMessageReceived: ${userGroupId.value}")
                    if (!tempMsgList.contains(message) && (selectedUsers.size > 2 && it.optString(Constants.Socket.GROUP_ID, "") == userGroupId.value)) {
                        tempMsgList.add(0, message)
                        msgListFlow.value = tempMsgList
                    }else if (!tempMsgList.contains(message) && selectedUsers.size <= 2 && message.receiverId != "") {
                        tempMsgList.add(0, message)
                        msgListFlow.value = tempMsgList
                    }else if (!tempMsgList.contains(message) && this.senderID == message.senderId) {
                        tempMsgList.add(0, message)
                        msgListFlow.value = tempMsgList
                    }
                    // tempMsgList.add(0, message)
                    // msgListFlow.value = tempMsgList
                }
                //  msgListFlow.value = tempMsgList
            }
        } catch (e: JSONException) {
            SocketClass.loggerE("JSONException: ${e.message}")
        } catch (e: Exception) {
            SocketClass.loggerE("Exception: ${e.message}")
        }
    }

    private fun callUserGroupMemberList(coroutineScope: CoroutineScope, context: Context) {

        coroutineScope.launch {
            showLoader.value = true
            apiRepository.groupMemberList(userGroupId.value).collect {
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
                        apiUserMemberList.value = it.data?.data ?: emptyList()
                        showLoader.value = false
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
                page,
            ).collect { result ->
                when (result) {
                    is NetworkResult.Error -> {
                        isLoader.value = false
                        showLoader.value = false
                    }

                    is NetworkResult.Loading -> {
                        if (page == 1) showLoader.value = true else isLoader.value = true
                    }

                    is NetworkResult.Success -> {
                        isLoader.value = false
                        showLoader.value = false
                        noDataFoundText.value = false
                        val newData = result.data?.data ?: emptyList()
                        _apiUserGroupUserListResultFlow.update { it + newData }
                        Log.d("TAG", "result: ${result.data?.data}")
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
                currentPage++
            }
        }
    }

    private fun initializeState(coroutineScope: CoroutineScope) {
        runBlocking {
            accessToken = getAccessToken(coroutineScope)
            groupId = getCreateGroupData(coroutineScope)?.chatGroupId.orEmpty()
            senderID = getUserData(coroutineScope)?._id.orEmpty()
            accessToken = getAccessToken(coroutineScope)
            name = getUserFullName(coroutineScope)
            image = getUserData(coroutineScope)?.profile?.profileImage.orEmpty()

        }
    }

    private fun initSocketListener(context: Context) {
        (context.requireActivity() as ContainerActivity).initSocketListener(this)
    }

    private fun initSocket() {
        try {
            runBlocking {
                SocketClass.getSocket(accessToken)?.let {
                    //if (!it.connected()) {
                    SocketClass.connectSocket(accessToken)
                    //}
                    if (type.value == 1) {
                        it.emit(Constants.Socket.CREATE_ROOM, createGroupRoom())
                    } else {
                        if (messageData.value.isNotEmpty()) {
                            it.emit(Constants.Socket.CREATE_ROOM, createSingleRoom())
                        } else if (!oneTimeRoomConnect) {
                            it.emit(Constants.Socket.CREATE_ROOM, memberCreateRoom())
                            oneTimeRoomConnect=true

                        }else{
                            // TODO: noting else  
                        }
                    }

                }
            }
        } catch (e: Exception) {
            Log.e("TAG", "initSocket Activity ==: ${e.message}")
        }
    }

    private fun memberCreateRoom(): JSONObject {
        val userObject = JSONObject()
        userObject.put(Constants.Socket.SENDER_ID, senderID)
        userObject.put(
            Constants.Socket.RECEIVER_ID, chatGroupId.value
        )
        userObject.put(Constants.Socket.GROUP_ID, "")
        Log.e("Socket createRoom ", userObject.toString())
        return userObject
    }

    override fun onRoomConnected(roomID: String) {
        SocketClass.loggerE("onRoom Connected: roomId: $roomID")
        this.roomId = roomID
    }

    private fun createSingleRoom(): JSONObject {
        val userObject = JSONObject()
        userObject.put(Constants.Socket.SENDER_ID, senderID)
        userObject.put(Constants.Socket.RECEIVER_ID, receiverId.value)
        userObject.put(Constants.Socket.GROUP_ID, "")
        Log.e("Socket createRoom ", userObject.toString())
        return userObject
    }

    private fun createGroupRoom(): JSONObject {
        val userObject = JSONObject()
        userObject.put(Constants.Socket.SENDER_ID, senderID)
        userObject.put(Constants.Socket.RECEIVER_ID, "")
        userObject.put(Constants.Socket.GROUP_ID, userGroupId.value)
        Log.e("Socket createRoom ", userObject.toString())
        return userObject
    }

    private fun sendMsg(msg: String, type: Int, coroutineScope: CoroutineScope, context: Context) {
        if (msg.isNotEmpty()) {
            try {
                SocketClass.getSocket(accessToken)?.let {
                    if (!it.connected()) {
                        SocketClass.connectSocket(accessToken)
                    }
                    if (type == 1 || type == 3) {
                        if (messageData.value.isNotEmpty()) {
                            it.emit(
                                Constants.Socket.SEND_MESSAGE,
                                sendMessage(
                                    message = msg,
                                    messageType = type,
                                    coroutineScope
                                ),
                            )

                        } else {
                            it.emit(
                                Constants.Socket.SEND_MESSAGE,
                                memberSendMessage(
                                    message = msg,
                                    messageType = type,
                                    coroutineScope
                                ),
                            )
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e("TAG", "sendMsg: $e")
            }
            val tempTime: Long = Date().formatDateTimeToUTCTimeStamp()
            val message = KinshipGroupChatListData(
                senderId = senderID,
                message = msg,
                receiverId = "",
                name = "You",
                image = msg,
                profileImage = getUserData(coroutineScope)?.profile?.profileImage,
                createdAt = tempTime,
                type = type
            )
            if (message.name != "You") {
                tempMsgList.add(0, message)
                msgListFlow.value = tempMsgList
            }
            // tempMsgList.add(0, message)
        }
    }

    private fun memberSendMessage(
        message: String,
        messageType: Int,
        coroutineScope: CoroutineScope
    ): JSONObject {
        val msgObject = JSONObject()
        msgObject.put(Constants.Socket.MESSAGE, message)
        msgObject.put(Constants.Socket.SENDER_ID, senderID)
        msgObject.put(
            Constants.Socket.SENDER_NAME,
            getUserData(coroutineScope)?.profile?.firstName.plus(" ")
                .plus(getUserData(coroutineScope)?.profile?.lastName),
        )
        msgObject.put(
            Constants.Socket.RECEIVER_ID,
            if (chatGroupId.value.isNullOrEmpty()) userId.value else chatGroupId.value
        )
        msgObject.put(
            Constants.Socket.GROUP_ID,
            ""
        )
        msgObject.put(Constants.Socket.MESSAGE_TYPE, messageType)
        msgObject.put(Constants.Socket.ROOM_ID, roomId)
        Log.e("TAG", "sendMessage: $msgObject")
        return msgObject
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
        msgObject.put(Constants.Socket.RECEIVER_ID, if (type.value == 2) receiverId.value else "")
        msgObject.put(Constants.Socket.GROUP_ID, if (type.value == 1) userGroupId.value else "")
        msgObject.put(Constants.Socket.MESSAGE_TYPE, messageType)
        msgObject.put(Constants.Socket.ROOM_ID, roomId)
        Log.e("TAG", "sendMessage: $msgObject")
        return msgObject
    }

    private fun getUserFullName(coroutineScope: CoroutineScope): String {
        val userData = getUserData(coroutineScope)
        return userData?.profile?.firstName.orEmpty() + " " + userData?.profile?.lastName.orEmpty()
    }

    private fun getCreateGroupData(coroutineScope: CoroutineScope): CreateGroup? {
        var data: CreateGroup? = null
        coroutineScope.launch { data = appPreferenceDataStore.getCreateGroupData() }
        return data
    }

    private fun getUserData(coroutineScope: CoroutineScope): UserAuthResponseData? {
        var data: UserAuthResponseData? = null
        coroutineScope.launch { data = appPreferenceDataStore.getUserData() }
        return data
    }

    private fun getAccessToken(coroutineScope: CoroutineScope): String {
        var data = ""
        coroutineScope.launch {
            data = appPreferenceDataStore.getUserData()?.auth?.accessToken.toString()
        }
        return data
    }

    private fun clearUnUsedUseState() {
        launchCamera.value = false
    }


}