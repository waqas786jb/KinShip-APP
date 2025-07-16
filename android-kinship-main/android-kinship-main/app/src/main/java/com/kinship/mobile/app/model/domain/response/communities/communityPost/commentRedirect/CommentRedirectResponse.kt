package com.kinship.mobile.app.model.domain.response.communities.communityPost.commentRedirect

import com.google.gson.annotations.SerializedName
import com.kinship.mobile.app.model.domain.response.communities.communityPost.CommunityPostResponse

data class CommentRedirectResponse(
    @SerializedName("comments") val comments: List<CommunityPostResponse>,
    @SerializedName("post") val post: CommunityPostResponse?=null
)

data class Post(
    @SerializedName("_id") val _id: String?=null,
    @SerializedName("commentCount") val commentCount: Int?=null,
    @SerializedName("communityId") val communityId: String?=null,
    @SerializedName("createdAt") val createdAt: Long?=null,
    @SerializedName("file") val file: String?=null,
    @SerializedName("firstName") val firstName: String?=null,
    @SerializedName("lastName") val lastName: String?=null,
    @SerializedName("message") val message: String?=null,
    @SerializedName("profileImage") val profileImage: String?=null,
    @SerializedName("updatedAt") val updatedAt: Long?=null,
    @SerializedName("userId") val userId: String?=null
)

data class Comment(
    @SerializedName("_id") val _id: String?=null,
    @SerializedName("createdAt")  val createdAt: Long?=null,
    @SerializedName("firstName")  val firstName: String?=null,
    @SerializedName("isLiked") val isLiked: Boolean?=null,
    @SerializedName("lastName") val lastName: String?=null,
    @SerializedName("like") val like: Int?=null,
    @SerializedName("message") val message: String?=null,
    @SerializedName("profileImage") val profileImage: String?=null,
    @SerializedName("updatedAt") val updatedAt: Long?=null,
    @SerializedName("userId") val userId: String?=null
)
