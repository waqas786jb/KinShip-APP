package com.kinship.mobile.app.model.domain.request.adoptedRequest

import com.google.gson.annotations.SerializedName

data class AdoptedRequest(
    val childHasSpecialNeed: Int=0,
    @SerializedName("babyBornDate") val babyBornDate: List<String> = emptyList(),
    val singleGender: Int=0,
    val kinshipReason: Int=0,
    val singleOrMultiplePregnancy: Int=0,
    val step: Int=0,
)
