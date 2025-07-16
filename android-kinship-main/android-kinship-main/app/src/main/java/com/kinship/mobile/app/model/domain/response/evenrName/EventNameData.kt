package com.kinship.mobile.app.model.domain.response.evenrName

import com.google.gson.annotations.SerializedName

data class EventNameData(
    @SerializedName("firstName") val firstName: String,
    @SerializedName("lastName") val lastName: String,
    @SerializedName("profileImage") val profileImage: String
)
