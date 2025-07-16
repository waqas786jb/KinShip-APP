package com.kinship.mobile.app.model.domain.response.hobbiesData

import com.google.gson.annotations.SerializedName

data class HobbiesData(
    @SerializedName("__v") val __v: Int,
    @SerializedName("_id") val _id: String,
    @SerializedName("name") val name: String
)
