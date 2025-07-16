package com.kinship.mobile.app.ux.main.home.member

import android.content.Context
import com.kinship.mobile.app.model.domain.response.group.GroupMember
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class MemberUiState(
    //data
    val memberList: List<GroupMember> = emptyList(),
    val kinshipName: String = "",
    val memberCount: String = "",
    val onProfileClickButton: (GroupMember) -> Unit = {},
    val onBackClick: () -> Unit = {},
    val navigateToSingleGroupChat:(GroupMember) -> Unit = {},
    val showLoader: StateFlow<Boolean> = MutableStateFlow(false),
    var userId:String="",
    val initSocketListener: (Context) -> Unit = {},
    val groupMemberList: MutableStateFlow<List<GroupMember>> = MutableStateFlow(emptyList()),
    val groupMemberAPICall:()->Unit={},

    )