package com.kinship.mobile.app.model.domain.request.contactSupport

import com.google.gson.annotations.SerializedName

data class ContactSupportRequest(
    @SerializedName("reason") val reason: String = "",
    @SerializedName("description") val description: String = "",
)
