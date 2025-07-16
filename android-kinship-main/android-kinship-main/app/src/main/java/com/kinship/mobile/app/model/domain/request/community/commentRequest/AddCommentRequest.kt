package com.kinship.mobile.app.model.domain.request.community.commentRequest

import com.google.gson.annotations.SerializedName

data class AddCommentRequest(
     @SerializedName("postId") val postId:String?=null,
     @SerializedName("message") val message:String?=null,
)
