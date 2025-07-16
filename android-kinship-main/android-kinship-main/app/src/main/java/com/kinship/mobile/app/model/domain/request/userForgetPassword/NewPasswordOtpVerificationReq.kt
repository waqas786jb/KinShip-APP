package com.kinship.mobile.app.model.domain.request.userForgetPassword

import com.google.gson.annotations.SerializedName

data class NewPasswordOtpVerificationReq(
    @SerializedName("email") val email: String = "",
    @SerializedName("otp") val otp: String = "",
    @SerializedName("type") val type: Int = 0
)
