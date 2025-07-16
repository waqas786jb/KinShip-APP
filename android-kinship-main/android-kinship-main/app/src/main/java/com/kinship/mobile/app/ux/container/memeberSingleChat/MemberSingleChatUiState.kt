package com.kinship.mobile.app.ux.container.memeberSingleChat

import android.content.Context
import android.net.Uri
import androidx.paging.PagingData
import com.kinship.mobile.app.model.domain.response.chat.KinshipGroupChatListData
import com.kinship.mobile.app.model.domain.response.chat.userGroup.addMember.groupMember.UserGroupMember
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

data class MemberSingleChatUiState(
    //send message
    val msgValue: StateFlow<String> = MutableStateFlow(""),
    val onMsgValueChange: (String) -> Unit = {},
    val profilePicFlow: StateFlow<String> = MutableStateFlow(""),
    val onProfileImgPick: (String) -> Unit = {},
    val openPickImgDialog: StateFlow<Boolean> = MutableStateFlow(false),
    val onOpenORDismissDialog: (Boolean) -> Unit = {},
    val captureUri: StateFlow<Uri?> = MutableStateFlow(null),
    val onClearUnUsedUseState: () -> Unit = {},
    val onClickOfCamera: (Context) -> Unit = {},
    val onMessageSend: (String,  Int) -> Unit = { _: String, _: Int -> },
    val sendMessageAPICall: () -> Unit = {},
    val userMemberListAPICall: () -> Unit = {},
    val messageResponse:(String)->Unit={},
    val type:(Int)->Unit={},
    val receiverId:(String)->Unit={},
    val onBackClick: () -> Unit = {},
    val userGroupId:(String)->Unit={},
    val getContext:(Context)->Unit={},
    val initSocketListener: (Context) -> Unit = {},
    val sendMessageData: (String) -> Unit = {},
    val sendMemberData: (String,String) -> Unit = {_:String, _:String ->},
    val apiMemberList: MutableStateFlow<List<UserGroupMember>> = MutableStateFlow(emptyList()),

    val launchCamera: StateFlow<Boolean> = MutableStateFlow(false),
    val showLoader: StateFlow<Boolean> = MutableStateFlow(false),
    val apiSingleListResultFlow: StateFlow<PagingData<KinshipGroupChatListData>> = MutableStateFlow(
        PagingData.empty()
    ),
    val apiUserGroupListResultFlow: StateFlow<List<KinshipGroupChatListData>> = MutableStateFlow(emptyList()),
    val msgListFlow: StateFlow<List<KinshipGroupChatListData>> = MutableStateFlow(emptyList()),

    val isLoading: StateFlow<Boolean> = MutableStateFlow(false),

    val onLoadSingleGroupNextPage: () -> Unit={},
    val showEventNoFoundText: StateFlow<Boolean> = MutableStateFlow(false),
)