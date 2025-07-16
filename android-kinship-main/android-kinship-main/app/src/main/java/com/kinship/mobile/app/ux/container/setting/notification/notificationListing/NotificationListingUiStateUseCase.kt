package com.kinship.mobile.app.ux.container.setting.notification.notificationListing
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.google.gson.Gson
import com.kinship.mobile.app.data.source.Constants
import com.kinship.mobile.app.data.source.local.datastore.AppPreferenceDataStore
import com.kinship.mobile.app.data.source.remote.helper.NetworkResult
import com.kinship.mobile.app.data.source.remote.repository.ApiRepository
import com.kinship.mobile.app.model.domain.response.ApiResponseNew
import com.kinship.mobile.app.model.domain.response.group.CreateGroup
import com.kinship.mobile.app.model.domain.response.notification.NotificationListResponse
import com.kinship.mobile.app.model.domain.response.signUp.UserAuthResponseData
import com.kinship.mobile.app.navigation.NavigationAction
import com.kinship.mobile.app.utils.ext.requireActivity
import com.kinship.mobile.app.utils.socket.OnSocketEventsListener
import com.kinship.mobile.app.utils.socket.SocketClass
import com.kinship.mobile.app.utils.socket.SocketClass.loggerE
import com.kinship.mobile.app.ux.container.ContainerActivity
import com.kinship.mobile.app.ux.container.chat.GroupChatRoute
import com.kinship.mobile.app.ux.container.communityFeature.comment.CommentRoute
import com.kinship.mobile.app.ux.container.communityFeature.communityPost.CommunityPostRoute
import com.kinship.mobile.app.ux.container.singleUserChat.SingleGroupChatRoute
import com.kinship.mobile.app.ux.main.MainActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.json.JSONObject
import javax.inject.Inject

class NotificationListingUiStateUseCase
@Inject constructor(
    private val apiRepository: ApiRepository,
    private val appPreferenceDataStore: AppPreferenceDataStore
): OnSocketEventsListener {
    private val apiNotificationResultFlow =
        MutableStateFlow<NetworkResult<ApiResponseNew<NotificationListResponse>>?>(null)
    private val showLoader = MutableStateFlow(false)
    private val isLoading = MutableStateFlow(false)
    private val noDataFound = MutableStateFlow(false)
    private val notificationList = MutableStateFlow<PagingData<NotificationListResponse>>(PagingData.empty())
    private var senderID: String = ""
    private var receiverID: String = ""
    private var groupId: String = ""
    private var roomId: String = ""
    private var accessToken = ""
    private val isAllEventsRefreshing = MutableStateFlow(false)
    operator fun invoke(
        context: Context,
        @Suppress("UnusedPrivateProperty")
        coroutineScope: CoroutineScope,
        navigate: (NavigationAction) -> Unit,
    ): NotificationListingUiState {
        callNotificationListAPI(coroutineScope = coroutineScope)
        senderID = getUserData(coroutineScope)?._id.orEmpty()
        groupId = getCreateGroupData(coroutineScope)?.chatGroupId.orEmpty()
        accessToken = getAccessToken(coroutineScope)
        initSocket()

        return NotificationListingUiState(
            clearAllApiResultFlow = { clearAllAPIResultFlow() },
            onBackClick = {
                SocketClass.getSocket(accessToken)
                    ?.emit(Constants.Socket.ROOM_DISCONNECT, roomDisconnect())
                navigate(NavigationAction.PopIntent)
                          },
            noDataFound = noDataFound,
            showLoader = showLoader,
            notificationList = notificationList,
            navigateToNotificationItem = { notificationData ->
                when (notificationData.type) {
                    1 -> {
                        navigate(
                            NavigationAction.Navigate(
                                GroupChatRoute.createRoute(
                                    messageId = notificationData.mainId?:"",
                                    screenName = Constants.AppScreen.NOTIFICATION_SCREEN,
                                    subId = notificationData.subId?:""
                                )
                            )
                        )
                    }
                    2 -> {
                        navigate(
                            NavigationAction.Navigate(
                                SingleGroupChatRoute.createRoute(mainId = notificationData.mainId?:"", subId = notificationData.subId?:"", screenName = Constants.AppScreen.NOTIFICATION_SCREEN, single = notificationData.single?:false)
                            )
                        )
                    }
                    3 -> {
                        navigateToEventDetailsScreen(
                            context = context,
                            navigate = navigate,
                            screenName = Constants.AppScreen.EVENT_SCREEN,
                            mainId = notificationData.mainId ?: "",
                            type = notificationData.type
                        )
                        Log.d("TAG", "mainId: ${notificationData.mainId}")
                    }
                    4 -> {
                        navigateToEventDetailsScreen(
                            context = context,
                            navigate = navigate,
                            screenName = Constants.AppScreen.EVENT_SCREEN,
                            mainId = notificationData.mainId ?: "",
                            type = notificationData.type
                        )
                    }
                    5 -> {
                        navigate(
                            NavigationAction.Navigate(
                                CommunityPostRoute.createRoute(
                                    communityName = notificationData.communityName ?: "",
                                    communityId = notificationData.mainId ?: "",
                                    joinCommunity = true,
                                    screenName = Constants.AppScreen.NOTIFICATION_SCREEN,
                                    notificationType = Constants.Notification.CREATE_POST_IN_COMMUNITY_POST
                                )
                            )
                        )
                    }

                    6 -> {
                        navigate(
                            NavigationAction.Navigate(
                                CommunityPostRoute.createRoute(
                                    communityName = notificationData.communityName ?: "",
                                    communityId = notificationData.mainId ?: "",
                                    joinCommunity = true,
                                    screenName = Constants.AppScreen.NOTIFICATION_SCREEN,
                                    notificationType = Constants.Notification.LIKE_DISLIKE_POST
                                )
                            )
                        )
                    }
                    7 -> {
                        val data = Gson().toJson(notificationData)
                        navigate(
                            NavigationAction.Navigate(
                                CommentRoute.createRoute(
                                    communityPost = data,
                                    screenName = Constants.AppScreen.NOTIFICATION_SCREEN
                                )
                            )
                        )
                    }
                }
            },
            isLoading=isLoading,
            initSocketListener = { initSocketListener(it) },
            notificationListingAPICall = {
                callNotificationListAPI(coroutineScope = coroutineScope)
            },
            isAllEventsRefreshing = isAllEventsRefreshing
        )
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
    private fun initSocketListener(context: Context) {
        (context.requireActivity() as ContainerActivity).initSocketListener(this)
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

    private fun createRoom(): JSONObject {
        val userObject = JSONObject()
        userObject.put(Constants.Socket.SENDER_ID, senderID)
        userObject.put(Constants.Socket.RECEIVER_ID, "")
        userObject.put(Constants.Socket.GROUP_ID, groupId)
        Log.e("Socket createRoom ", userObject.toString())
        return userObject
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
    override fun onRoomConnected(roomID: String) {
        loggerE("onRoom Connected: roomId: $roomID")
        this.roomId = roomID
        SocketClass.getSocket(accessToken)
            ?.emit(Constants.Socket.ROOM_DISCONNECT, roomDisconnect())
    }

    private fun navigateToEventDetailsScreen(
        context: Context,
        navigate: (NavigationAction) -> Unit,
        screenName: String,
        mainId: String,
        type:Int,
    ) {
        val bundle = Bundle()
        bundle.putString(Constants.BundleKey.MAIN_ID, mainId)
        bundle.putInt(Constants.BundleKey.TYPE, type)
        val intent = Intent(context, MainActivity::class.java)
        intent.putExtra(Constants.BundleKey.EXTRA_BUNDLE, bundle)

        intent.putExtra(Constants.IntentData.SCREEN_NAME, screenName)
        navigate(NavigationAction.NavigateIntent(intent = intent, finishCurrentActivity = false))
    }
    private fun callNotificationListAPI(
        coroutineScope: CoroutineScope,
        ) {
        coroutineScope.launch {
            apiRepository.getNotificationList().cachedIn(coroutineScope).collect {
                notificationList.value=it
            }
        }
    }
    private fun clearAllAPIResultFlow() {
        apiNotificationResultFlow.value = null
    }

}