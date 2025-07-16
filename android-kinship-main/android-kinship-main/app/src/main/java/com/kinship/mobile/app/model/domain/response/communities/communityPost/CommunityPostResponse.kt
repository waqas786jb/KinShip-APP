package com.kinship.mobile.app.model.domain.response.communities.communityPost

import com.google.gson.annotations.SerializedName

data class CommunityPostResponse(
    @SerializedName("_id") val id: String?=null,
    @SerializedName("commentCount") var commentCount: Int?=null,
    @SerializedName("createdAt") val createdAt: Long?=null,
    @SerializedName("file") val file: String?=null,
    @SerializedName("text") val text: String?=null,
    @SerializedName("firstName") val firstName: String?=null,
    @SerializedName("isLiked") var isLiked: Boolean?=null,
    @SerializedName("lastName") val lastName: String?=null,
    @SerializedName("like") var like: Int?=null,
    @SerializedName("message") val message: String?=null,
    @SerializedName("profileImage") val profileImage: String?=null,
    @SerializedName("updatedAt") val updatedAt: Long?=null,
    @SerializedName("userId") val userId: String?=null,
    val joinCommunity:Boolean=false


){
    val commentText: String
        get() = message ?:text?:""
}

