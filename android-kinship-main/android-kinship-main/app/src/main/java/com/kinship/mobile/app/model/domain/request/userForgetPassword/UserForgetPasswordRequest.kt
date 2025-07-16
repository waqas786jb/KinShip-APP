package com.kinship.mobile.app.model.domain.request.userForgetPassword

import com.google.gson.annotations.SerializedName

data class UserForgetPasswordRequest(
    @SerializedName("step") val step: Int = 0,


)
