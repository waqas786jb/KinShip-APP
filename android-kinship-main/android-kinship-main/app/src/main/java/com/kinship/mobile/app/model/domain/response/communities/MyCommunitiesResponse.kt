package com.kinship.mobile.app.model.domain.response.communities


import com.google.gson.annotations.SerializedName

data class MyCommunitiesResponse(
    @SerializedName("_id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("members") val members: Int,
    @SerializedName("unseenCount") val unseenCount: Int
)