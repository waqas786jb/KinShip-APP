package com.kinship.mobile.app.model.domain.request.community


import com.google.gson.annotations.SerializedName

data class NewSuggestionReq(
    @SerializedName("name") val name: String,
    @SerializedName("city") val city: String,
    @SerializedName("idea") val idea: String
)