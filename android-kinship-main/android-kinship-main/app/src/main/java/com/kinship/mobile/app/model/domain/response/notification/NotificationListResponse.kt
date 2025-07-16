package com.kinship.mobile.app.model.domain.response.notification


import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class NotificationListResponse(
    @SerializedName("firstName") val firstName: String? = null,
    @SerializedName("_id") val id: String? = null,
    @SerializedName("lastName") val lastName: String? = null,
    @SerializedName("message") val message: String? = null,
    @SerializedName("profileImage") val profileImage: String? = null,
    @SerializedName("receiverId") val receiverId: String? = null,
    @SerializedName("senderId") val senderId: String? = null,
    @SerializedName("communityName") val communityName: String? = null,
    @SerializedName("createdAt") val createdAt: Long? = null,
    @SerializedName("updatedAt") val updatedAt: Long? = null,
    @SerializedName("mainId") val mainId: String? = null,
    @SerializedName("subId") val subId: String? = null,
    @SerializedName("type") val type: Int? = null,
    @SerializedName("single") val single:Boolean?=null
) : Serializable