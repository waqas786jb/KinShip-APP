package com.kinship.mobile.app.model.domain.response.chat


import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class KinshipGroupChatListData(
    @SerializedName("createdAt")
    val createdAt: Long? = null,
    @SerializedName("groupId")
    val groupId: String? = null,
    @SerializedName("grpSeenAt")
    val grpSeenAt: ArrayList<GroupSeenAt>? = null,
    @SerializedName("_id")
    val id: String? = null,
    @SerializedName("isLiked")
    val isLiked: Boolean? = null,
    @SerializedName("isLikedArray")
    val isLikedArray: ArrayList<Any>? = null,
    @SerializedName("message")
    val message: String? = null,
    @SerializedName("isgrpLiked")
    var isgrpLiked:Boolean?=null,
    @SerializedName("receiverId")
    val receiverId: String? = "",
    @SerializedName("seenAt")
    val seenAt: Boolean? = null,
    @SerializedName("senderId")
    val senderId: String? = null,
    @SerializedName("type")

    val type: Int? = null,
    @SerializedName("updatedAt")
    val updatedAt: Long? = null,
    @SerializedName("userId")
    val userId: ArrayList<String>? = null,
    @SerializedName("profileImage")
    val profileImage: String? = null,
    @SerializedName("name")
    val name: String? = null,
    @SerializedName("image")
    val image: String? = null

) : Serializable

data class GroupSeenAt(
    @SerializedName("id")
    val id: String? = null,
    @SerializedName("_id")
    val _id: String? = null,
    @SerializedName("seen")
    val seen: Boolean? = null
) : Serializable

data class grpObj(
    @SerializedName("name")
    val name: String? = null,
    @SerializedName("profileImage")
    val profileImage: String? = null,
    @SerializedName("userId")
    val userId: ArrayList<String>? = null,
) : Serializable