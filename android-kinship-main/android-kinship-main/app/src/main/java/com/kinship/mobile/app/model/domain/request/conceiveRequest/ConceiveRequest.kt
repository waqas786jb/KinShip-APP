package com.kinship.mobile.app.model.domain.request.conceiveRequest

import com.google.gson.annotations.SerializedName

data class ConceiveRequest(
    @SerializedName("step") var step: Int = 0,
    @SerializedName("kinshipReason") var kinshipReason: Int = 0,
    @SerializedName("howLongYouAreTrying") var howLongYouAreTrying: Int = 0,
    @SerializedName("howYouTrying") var howYouTrying: Int = 0,
)
