package com.kinship.mobile.app.model.domain.request.otpVerfication

import com.google.gson.annotations.SerializedName

data class OtpVerificationRequest(
    @SerializedName("email") var email: String = "",
    @SerializedName("otp") var otp: String = "",
    @SerializedName("type") var type: Int,
)
