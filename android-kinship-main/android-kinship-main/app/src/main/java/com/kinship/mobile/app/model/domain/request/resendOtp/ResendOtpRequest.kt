package com.kinship.mobile.app.model.domain.request.resendOtp

import com.google.gson.annotations.SerializedName

data class ResendOtpRequest(
    @SerializedName("email") var email: String = "",
)

data class LogOutRequest(
    @SerializedName("deviceId") var deviceId: String = "",
)

data class ChangePasswordRequest(
    @SerializedName("oldPassword") var oldPassword: String = "",
    @SerializedName("password") var password: String = "",
    @SerializedName("confirmPassword") var confirmPassword: String = ""
)

data class RegisterForPushRequest(
    @SerializedName("token") var token: String = "",
    @SerializedName("deviceId") var deviceId: String = "",
    @SerializedName("platform") var platform: String = ""
)
data class EventStatusRequest(
    @SerializedName("eventId") var eventId: String = "",
    @SerializedName("status") var status: Int = 0
)
data class RefreshTokenRequest(
    @SerializedName("token") var token: String = "",
)
data class IsLikeDislikeRequest(
    @SerializedName("messageId") var messageId: String = "",
)