package com.kinship.mobile.app.model.domain.request.versionRequest

import com.google.gson.annotations.SerializedName

data class KinshipVersionRequest(
    @SerializedName("type") val type: String = "",
    @SerializedName("version") val version: String = "",
)
