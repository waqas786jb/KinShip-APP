package com.kinship.mobile.app.model.domain.request.babyRequest

import com.google.gson.annotations.SerializedName

data class  BabyRequest(
    @SerializedName("babyBornDate") val babyBornDate: List<String> = emptyList(),
    @SerializedName("childHasSpecialNeed") val childHasSpecialNeed: Int=0,
    @SerializedName("firstTimeMom") val firstTimeMom: Int=0,
    @SerializedName("kinshipReason") val kinshipReason: Int=0,
    @SerializedName("multipleGender") val multipleGender: Int=0,
    @SerializedName("singleGender") val singleGender: Int=0,
    @SerializedName("singleOrMultipleBirth") val singleOrMultipleBirth: Int=0,
    @SerializedName("step") val step: Int=0,
    )
