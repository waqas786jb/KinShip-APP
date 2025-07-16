package com.kinship.mobile.app.model.domain.response.notification


import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class NotificationSettingResponse(
    @SerializedName("_id")
    val id: String,
    @SerializedName("allNewPosts")
    val allNewPosts: Boolean=true,
    @SerializedName("directMessage")
    val directMessage: Boolean=true,
    @SerializedName("newEvents")
    val newEvents: Boolean=true
) : Serializable