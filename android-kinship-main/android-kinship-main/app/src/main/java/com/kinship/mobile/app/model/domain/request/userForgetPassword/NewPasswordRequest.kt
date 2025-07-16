package com.kinship.mobile.app.model.domain.request.userForgetPassword

import com.google.gson.annotations.SerializedName

data class NewPasswordRequest(
    @SerializedName("step") val step: Int = 0,
    @SerializedName("password") val password: String = "",
    @SerializedName("confirmPassword") val confirmPassword: String = "",
)
