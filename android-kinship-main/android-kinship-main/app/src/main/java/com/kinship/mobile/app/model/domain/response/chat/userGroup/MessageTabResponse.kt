package com.kinship.mobile.app.model.domain.response.chat.userGroup

import com.google.gson.annotations.SerializedName

class MessageTabResponse(
    @SerializedName("__v") val v: Int=0,
    @SerializedName("_id") var id: String="",
    @SerializedName("count") val count: Int=0,
    @SerializedName("createdAt") val createdAt: String="",
    @SerializedName("message") val message: String="",
    @SerializedName("name") val name: String="",
    @SerializedName("profileImage") val profileImage: String="",
    @SerializedName("receiverId") var receiverId: String="",
    @SerializedName("type") var type: Int=0,
    @SerializedName("updatedAt") val updatedAt: String="",
    @SerializedName("userId") val userId: List<String>
)
