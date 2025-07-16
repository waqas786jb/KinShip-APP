package com.kinship.mobile.app.model.domain.response.message
import com.google.gson.annotations.SerializedName

data class MessageResponse (
    @SerializedName("message") var message:String="",
    @SerializedName("url") var url:String="",

)
