package com.kinship.mobile.app.ux.container.newMessage
import androidx.paging.PagingData
import com.kinship.mobile.app.model.domain.response.chat.userGroup.MessageTabResponse
import com.kinship.mobile.app.model.domain.response.group.GroupMember
import com.kinship.mobile.app.model.domain.response.userStaticData.TempUserGroupData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

data class MessageNewMessageUiState(
    val clearAllApiResultFlow: () -> Unit = {},
    val apiUserGroupListResultFlow: StateFlow<PagingData<MessageTabResponse>> = MutableStateFlow(
        PagingData.empty()
    ),
    var userId:String="",
    val memberList: List<GroupMember> = emptyList(),
    val onBackClick: () -> Unit = {},
    val showLoader: StateFlow<Boolean> = MutableStateFlow(false),
    val addMemberGroupAPICall: () -> Unit = {},
    val onUserList: (TempUserGroupData) -> Unit = {},
    val onCallMessageAPICall:() -> Unit = {}


    )