package com.kinship.mobile.app.ux.container.memeberSingleChat

import android.annotation.SuppressLint
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
import com.kinship.mobile.app.R
import com.kinship.mobile.app.data.source.Constants
import com.kinship.mobile.app.data.source.local.datastore.AppPreferenceDataStore
import com.kinship.mobile.app.data.source.remote.helper.NetworkResult
import com.kinship.mobile.app.data.source.remote.repository.ApiRepository
import com.kinship.mobile.app.model.domain.response.chat.KinshipGroupChatListData
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
import com.kinship.mobile.app.ux.container.memberChatActivity.MemberChatActivity
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

class GetMemberSingleChatUiStateUseCase
@Inject constructor(
    private val apiRepository: ApiRepository,
    private val appPreferenceDataStore: AppPreferenceDataStore
) : OnSocketEventsListener,AppActivity(){
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
    private val _apiUserGroupUserListResultFlow =
        MutableStateFlow<List<KinshipGroupChatListData>>(emptyList())
    private val apiUserGroupUserListResultFlow: StateFlow<List<KinshipGroupChatListData>> =
        _apiUserGroupUserListResultFlow
    private val apiUserMemberList = MutableStateFlow<List<UserGroupMember>>(emptyList())
    private val msgListFlow = MutableStateFlow<List<KinshipGroupChatListData>>(emptyList())

    private val showLoader = MutableStateFlow(false)
    private var sendMessageChatId = ""
    private var userGroupId = MutableStateFlow("")
    private var chatGroupId = MutableStateFlow("")
    private var memberChatGroupId = MutableStateFlow("")

    private var currentPage = 1
    private val isLoader = MutableStateFlow(false)
    private val noDataFoundText = MutableStateFlow(false)
    private lateinit var context: Context

    operator fun invoke(
        context: Context,
        coroutineScope: CoroutineScope,
        navigate: (NavigationAction) -> Unit,
    ): MemberSingleChatUiState {
        Log.d("TAG", "roomId: ${this.roomId}")

        initializeState(coroutineScope)
        return MemberSingleChatUiState(
            msgValue = msgValue,
            onMsgValueChange = { msgValue.value = it },
            profilePicFlow = profileImgFlow,
            onProfileImgPick = { profileImgFlow.value = it; launchCamera.value = false },
            openPickImgDialog = openPickImgDialog,
            onOpenORDismissDialog = { openPickImgDialog.value = it },
            captureUri = captureUri,
            initSocketListener = {initSocketListener(it)},
            onClearUnUsedUseState = { clearUnUsedUseState() },
            msgListFlow = msgListFlow,
            showLoader = showLoader,
            launchCamera = launchCamera,
            onBackClick = {
                navigate(NavigationAction.PopIntent)
            },
            getContext = {
                this.context=it
            },
            onClickOfCamera = { createUriForCaptureImg(it) },
            apiSingleListResultFlow = apiSingleUserListResultFlow,
            apiUserGroupListResultFlow = apiUserGroupUserListResultFlow,
            sendMemberData = { memberData, chatId ->
                setMemberDataToFlow(memberData, coroutineScope, context, chatId)
            },
            apiMemberList = apiUserMemberList,
            sendMessageAPICall = { callSendImageAPI(coroutineScope, context) },
            onMessageSend = { msg, type ->
                sendMsg(msg, type, coroutineScope, context)
            },
            onLoadSingleGroupNextPage = {
                Toast.makeText(context, "memberClick", Toast.LENGTH_SHORT).show()
                callKinshipGroupChatListAPI(
                    coroutineScope = coroutineScope,
                    chatGroupId.value.ifEmpty { memberChatGroupId.value },
                    2,
                    0,
                    "",
                    page = currentPage,
                    context

                )
            },
            isLoading = isLoader,
            showEventNoFoundText = noDataFoundText,
        )
    }

    private fun initSocketListener(context: Context) {
        (context.requireActivity() as MemberChatActivity).initSocketListener(this)
    }

    private fun callSendImageAPI(
        coroutineScope: CoroutineScope,
        context: Context,
    ) {
        if (profileImgFlow.value.isBlank()) return
        val map: HashMap<String, RequestBody> = hashMapOf()
        map[Constants.SendImage.GROUP_ID] =
            userGroupId.value.toRequestBody("multipart/form-data".toMediaTypeOrNull())
        map[Constants.SendImage.RECEIVER_ID] =
            "".toRequestBody("multipart/form-data".toMediaTypeOrNull())
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
        coroutineScope.launch {
            apiRepository.sendMessage(map, profileImage).collect {
                when (it) {
                    is NetworkResult.Error -> {
                        showLoader.value = false
                        Toasty.error(context, it.toString(), Toast.LENGTH_SHORT, false).show()
                    }

                    is NetworkResult.Loading -> {
                        showLoader.value = true
                    }

                    is NetworkResult.Success -> {
                        clearUnUsedUseState()
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
                                tempMsgList.clear()
                                msgListFlow.value = tempMsgList

                            }
                        } catch (e: Exception) {
                            // Handle the exception appropriately
                            Log.e("SendImage", "Error sending image: ${e.message}")
                        }
                    }

                    is NetworkResult.UnAuthenticated -> {
                        showLoader.value = false
                        Toasty.error(context, it.toString(), Toast.LENGTH_SHORT, false).show()
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
        msgObject.put(
            Constants.Socket.RECEIVER_ID,
            chatGroupId.value.ifEmpty { memberChatGroupId.value }
        )
        msgObject.put(Constants.Socket.IMAGE, image)
        msgObject.put(
            Constants.Socket.GROUP_ID,
            chatGroupId.value.ifEmpty { memberChatGroupId.value }
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

    private fun setMemberDataToFlow(
        memberData: String,
        coroutineScope: CoroutineScope,
        context: Context,
        chatId: String,

        ) {
        val data: GroupMember =
            Gson().fromJson(memberData, GroupMember::class.java)
        chatGroupId.value = data.chatGroupId
        memberChatGroupId.value = chatId
        initSocket()
        callKinshipGroupChatListAPI(
            coroutineScope = coroutineScope,
            chatGroupId.value.ifEmpty { memberChatGroupId.value },
            2,
            0,
            "",
            page = currentPage,
            context

        )
    }

    override fun onNewMessageReceived(data: JSONObject) {
        try {
            data.let {
                SocketClass.loggerE("message response: $data")
                val messageType = it.optInt(Constants.Socket.MESSAGE_TYPE)
                val messageId = it.optString(Constants.Socket.MESSAGE_ID, "")
                val messageContent = it.optString(Constants.Socket.MESSAGE, "")
                val senderId = it.optString(Constants.Socket.SENDER_ID, "")
                val receiverId = it.optString(Constants.Socket.RECEIVER_ID, "")
                val senderName = it.optString(Constants.Socket.SENDER_NAME, "")
                val createdAt = it.optLong(Constants.Socket.CREATED_AT, 0L)
                val imageUrl = it.optString(Constants.Socket.IMAGE, "")
                val message = KinshipGroupChatListData(
                    id = messageId,
                    message = messageContent,
                    profileImage = image,
                    image = imageUrl,
                    type = messageType,
                    senderId = senderId,
                    receiverId = receiverId,
                    name = if (senderID == senderId) "You" else senderName,
                    createdAt = createdAt
                )
                tempMsgList.add(0, message)
                msgListFlow.value = tempMsgList
            }
        } catch (e: JSONException) {
            SocketClass.loggerE("JSONException: ${e.message}")
        } catch (e: Exception) {
            SocketClass.loggerE("Exception: ${e.message}")
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
                        _apiUserGroupUserListResultFlow.update { it + newData }

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

    private fun initSocket() {
        try {
            runBlocking {
                SocketClass.getSocket(accessToken)?.let {
                    if (!it.connected()) {
                        SocketClass.connectSocket(accessToken)
                    }
                    it.emit(Constants.Socket.CREATE_ROOM, createRoom())
                }
            }
        } catch (e: Exception) {
            Log.e("TAG", "initSocket Activity ==: ${e.message}")
        }
    }



   override fun onRoomConnected(roomId: String) {
        SocketClass.loggerE("onRoom Connected: roomId: $roomId")
        Log.d("TAG", "onRoomConnected: $roomId")
        this.roomId = roomId
    }

    private fun createRoom(): JSONObject {
        val userObject = JSONObject()
        userObject.put(Constants.Socket.SENDER_ID, senderID)
        userObject.put(
            Constants.Socket.RECEIVER_ID,
            chatGroupId.value.ifEmpty { memberChatGroupId.value }
        )
        userObject.put(Constants.Socket.GROUP_ID, "")
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
                    it.emit(
                        Constants.Socket.SEND_MESSAGE,
                        sendMessage(
                            message = msg,
                            messageType = type,
                            coroutineScope
                        ),
                    )
                }

            } catch (e: Exception) {
                Log.e("SocketError", "Socket is null. Unable to send the message.")
            }
            val tempTime: Long = Date().formatDateTimeToUTCTimeStamp()
            val message = KinshipGroupChatListData(
                senderId = senderID,
                message = msg,
                receiverId = "",
                name = context.getString(R.string.you),
                image = msg,
                profileImage = getUserData(coroutineScope)?.profile?.profileImage,
                createdAt = tempTime,
                type = type

            )
            tempMsgList.add(0, message)
            msgListFlow.value = tempMsgList
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
        msgObject.put(
            Constants.Socket.SENDER_NAME,
            getUserData(coroutineScope)?.profile?.firstName.plus(" ")
                .plus(getUserData(coroutineScope)?.profile?.lastName),
        )
        msgObject.put(
            Constants.Socket.RECEIVER_ID,
            chatGroupId.value.ifEmpty { memberChatGroupId.value }
        )
        msgObject.put(
            Constants.Socket.GROUP_ID,
            chatGroupId.value.ifEmpty { memberChatGroupId.value }
        )
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