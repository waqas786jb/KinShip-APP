package com.kinship.mobile.app.model.domain.response.chat.userGroup.addMember

import com.google.gson.annotations.SerializedName

data class AddGroupMember(
    @SerializedName("__v") val v: Int,
    @SerializedName("_id") val id: String,
    @SerializedName("createdAt") val createdAt: String,
    @SerializedName("updatedAt") val updatedAt: String,
    @SerializedName("userId") val userId: List<String>
)
