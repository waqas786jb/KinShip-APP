package com.kinship.mobile.app.model.domain.request.signUp.logIn

import com.google.gson.annotations.SerializedName

data class LogInRequest(
    @SerializedName("email") var email:String="",
    @SerializedName("password") var password:String="",
)
