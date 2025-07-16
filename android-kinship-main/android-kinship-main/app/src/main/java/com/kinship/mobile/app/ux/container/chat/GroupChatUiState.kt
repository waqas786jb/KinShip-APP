package com.kinship.mobile.app.ux.container.chat
import android.content.Context
import android.net.Uri
import com.kinship.mobile.app.data.source.remote.helper.NetworkResult
import com.kinship.mobile.app.model.domain.response.ApiResponse
import com.kinship.mobile.app.model.domain.response.chat.KinshipGroupChatListData
import com.kinship.mobile.app.model.domain.response.chat.isLIkeDislike.IsLikeDislikeResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class GroupChatUiState(
    //data
    val msgValue: StateFlow<String> = MutableStateFlow(""),
    val onMsgValueChange: (String) -> Unit = {},
    val memberCount: String = "",
    var messageId:String="",

    //Click
    val initSocketListener: (Context) -> Unit = {},
    val userFlagAPICall: ()-> Unit = {},
    val onMemberClickButton: () -> Unit = {},
    val showLoader: StateFlow<Boolean> = MutableStateFlow(false),
    val onGalleryClickButton: () -> Unit = {},
    val onLinkButtonButton: () -> Unit = {},
    val onSearchMessageButton: () -> Unit = {},
    val onLikeMessageButton: () -> Unit = {},
    val isFlag: StateFlow<Boolean> = MutableStateFlow(false),
    val onIsLikeAndDislikeAPICall: (String) -> Unit = {},
    val kinshipNameEdit: StateFlow<String> = MutableStateFlow(""),
    val kinshipName: StateFlow<String> = MutableStateFlow(""),
    val onKinshipNameValueChange: (String) -> Unit = {},
    val kinshipNameEditAPICall:() -> Unit ={},
    val clearKinshipNameEditState:() -> Unit ={},
    val openKinshipEditNameDialog: StateFlow<Boolean> = MutableStateFlow(false),
    val onKinshipEditNameDialog: (Boolean) -> Unit = {},
    val hideSendMessageView: StateFlow<Boolean> = MutableStateFlow(true),
    val onHideSendMessageView: (Boolean) -> Unit = {},
    val onIsFlagDialog: (Boolean) -> Unit = {},
    val captureUri: StateFlow<Uri?> = MutableStateFlow(null),
    val onClearUnUsedUseState: () -> Unit = {},
    val onClickOfCamera: (Context) -> Unit = {},
    val launchCamera: StateFlow<Boolean> = MutableStateFlow(false),
    val profilePicFlow: StateFlow<String> = MutableStateFlow(""),
    val onProfileImgPick: (String) -> Unit = {},
    val senderName: (String) -> Unit = {},
    val refresh: () -> Unit = {},
    val sendMessageAPICall: () -> Unit = {},
    val onBackClick: () -> Unit = {},
    val onNotificationBackClick: () -> Unit = {},
    val messageMainID: StateFlow<String> = MutableStateFlow(""),
    val onMessageSend: (String,  Int) -> Unit = { _: String, _: Int -> },
    val apiResultFlow: StateFlow<NetworkResult<ApiResponse<IsLikeDislikeResponse>>?> = MutableStateFlow(null),
    val apiChatListResultFlow: StateFlow<List<KinshipGroupChatListData>> = MutableStateFlow(emptyList()),
    var navigateToSearchScreen:()-> Unit={},
    val clearAllApiResultFlow: () -> Unit = {},
    val apiSearchChat: MutableStateFlow<List<KinshipGroupChatListData>> = MutableStateFlow(emptyList()),
    val msgListFlow: StateFlow<List<KinshipGroupChatListData>> = MutableStateFlow(emptyList()),
    val kinshipReason: Int = 0,
    val isLoading: StateFlow<Boolean> = MutableStateFlow(false),
    val onLoadNextPage: () -> Unit={},
    val screenName: StateFlow<String> = MutableStateFlow(""),
    val tempAccessToken: StateFlow<String> = MutableStateFlow(""),

)