package com.kinship.mobile.app.model.domain.request.pregnantRequest

import com.google.gson.annotations.SerializedName

data class PregnantRequest(
    @SerializedName("firstTimeMom") val firstTimeMom: Int = 0,
    @SerializedName("whenIsYourDueDate") val whenIsYourDueDate: String="",
    @SerializedName("kinshipReason") val kinshipReason: Int = 0,
    @SerializedName("multipleGender") val multipleGender: Int = 0,
    @SerializedName("singleGender") val singleGender: Int = 0,
    @SerializedName("singleOrMultiplePregnancy") val singleOrMultiplePregnancy: Int = 0,
   @SerializedName("step") val step: Int = 0,

)
