package com.kinship.mobile.app.model.domain.response.conceive

import com.google.gson.annotations.SerializedName

data class ConceiveResponseData(
    @SerializedName("_id") val _id: String="",
    @SerializedName("email") val email: String="",
    @SerializedName("isProfileCompleted") val isProfileCompleted: Boolean=false,
    @SerializedName("isVerify") val isVerify: Boolean=false,
    @SerializedName("profile") val profile: Profile
)
data class Profile(
    @SerializedName("__v") val __v: Int=0,
    @SerializedName("_id") val _id: String="",
    @SerializedName("babyBornDate") val babyBornDate: List<Any> = emptyList(),
    @SerializedName("created_at") val created_at: String="",
    @SerializedName("singleOrMultipleBirth") val singleOrMultipleBirth: String="",
    @SerializedName("hobbies") val hobbies: List<Any>,
    @SerializedName("howLongYouAreTrying") val howLongYouAreTrying: Int=0,
    @SerializedName("howYouTrying") val howYouTrying: Int=0,
    @SerializedName("isActive") val isActive: Boolean=false,
    @SerializedName("isFull") val isFull: Boolean=false,
    @SerializedName("kinshipReason") val kinshipReason: Int=0,
    @SerializedName("step") val step: Int=0,
    @SerializedName("updated_at") val updated_at: String="",
    @SerializedName("userId") val userId: String=""
)

