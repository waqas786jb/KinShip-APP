package com.kinship.mobile.app.model.domain.request.kinshipName

import com.google.gson.annotations.SerializedName

data class KinshipNameEditRequest(
   @SerializedName("groupId") val groupId: String,
   @SerializedName("groupName") val groupName: String
)
