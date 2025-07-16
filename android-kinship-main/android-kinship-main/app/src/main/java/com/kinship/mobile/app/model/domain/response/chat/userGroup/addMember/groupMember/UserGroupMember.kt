package com.kinship.mobile.app.model.domain.response.chat.userGroup.addMember.groupMember

import com.google.gson.annotations.SerializedName

data class UserGroupMember(
    @SerializedName("name") val name: String,
    @SerializedName("profileImage") val profileImage: String
)
