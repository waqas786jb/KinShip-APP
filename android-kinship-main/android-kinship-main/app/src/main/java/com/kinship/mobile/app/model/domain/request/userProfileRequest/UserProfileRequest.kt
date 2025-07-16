package com.kinship.mobile.app.model.domain.request.userProfileRequest

import com.google.gson.annotations.SerializedName

data class UserProfileRequest(
    @SerializedName("dateOfBirth") val dateOfBirth: String = "",
    @SerializedName("city") val city: String = "",

    @SerializedName("firstName") val firstName: String = "",
    @SerializedName("hobbies") val hobbies: List<String> = emptyList(),
    @SerializedName("lastName") val lastName: String = "",
    @SerializedName("lat") val lat: String = "",
    @SerializedName("countrycode") val countrycode: String = "",
    @SerializedName("long") val long: String = "",
    @SerializedName("phoneNumber") val phoneNumber: String = "",
    @SerializedName("step") val step: Int = 0,
    @SerializedName("zipcode") val zipcode: String = ""


)
