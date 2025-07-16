package com.kinship.mobile.app.model.domain.response.group

import com.kinship.mobile.app.model.domain.response.message.MessageResponse

data class CreateGroup(
    val count: Int=0,
    val groupId: String="",
    val groupMembers: List<GroupMember> = emptyList(),
    val groupName: String="",
    //var message:MessageResponse,
    val image: String="",
    val city:String="",
    val isGroupCreated: Boolean=false,
    val chatGroupId: String="",
    val kinshipCount:Int=0,
    val notificationCount:Int=0,
    val messageCount:Boolean=false
)
data class GroupMember(
    val _id: String="",
    val babyBornDate: List<Any> = emptyList(),
    val bio: String="",
    val city: String="",
    val dateOfBirth:String="",
    val firstName: String="",
    val lastName: String="",
    val profileImage: String="",
    val chatGroupId:String="",
    val userId: String=""
)