package com.kinship.mobile.app.model.domain.request.userGroup

import com.google.gson.annotations.SerializedName

data class AddGroupMemberRequest(
    @SerializedName("userId") val userId: List<String> = emptyList(),
)
