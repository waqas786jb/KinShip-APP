package com.kinship.mobile.app.model.domain.request.signUp

import com.google.gson.annotations.SerializedName

data class SignInRequest(
        @SerializedName("email") var email: String = "",
        @SerializedName("password") var password: String = "",
    @SerializedName("confirmPassword") var confirmPassword:String = ""
)