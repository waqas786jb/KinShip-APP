package com.kinship.mobile.app.model.domain.response.signUp

import com.google.gson.annotations.SerializedName
import com.kinship.mobile.app.model.domain.response.hobbiesData.HobbiesData
import java.io.Serializable

data class UserAuthResponseData(
    @SerializedName("_id") var _id: String? = "",
    @SerializedName("auth") var auth: Auth? = Auth(),
    @SerializedName("email") var email: String? = "",
    @SerializedName("isProfileCompleted") var isProfileCompleted: Boolean? = false,
    @SerializedName("isVerify") var isVerify: Boolean? = false,
    @SerializedName("profile") var profile: ProfileData? = ProfileData()
) : Serializable

data class Auth(
    @SerializedName("accessToken") val accessToken: String = "",
    @SerializedName("expiresIn") val expiresIn: Int = 0,
    @SerializedName("refreshToken") val refreshToken: String = "",
    @SerializedName("tokenType") val tokenType: String = ""
) : Serializable

data class ProfileData(
    @SerializedName("babyBornDate")
    val babyBornDate: ArrayList<Long> = arrayListOf(),
    @SerializedName("countrycode")
    val countrycode: String = "",
    @SerializedName("created_at")
    val createdAt: String = "",
    @SerializedName("city")
    val city: String = "",
    @SerializedName("bio")
    var bio: String = "",
    @SerializedName("dateOfBirth")
    val dateOfBirth: String = "",
    @SerializedName("editBabyBornDate")
    val editBabyBornDate: ArrayList<Long> = arrayListOf(),
    @SerializedName("firstName")
    val firstName: String = "",
    @SerializedName("profileImage")
    var profileImage: String = "",
    @SerializedName("hobbies")
    val hobbies: List<HobbiesData> = arrayListOf(),
    //  @SerializedName("hobbies")
    //   val hobbies: ArrayList<String> = arrayListOf(),
    @SerializedName("howLongYouAreTrying")
    val howLongYouAreTrying: Int = 0,
    @SerializedName("howYouTrying")
    val howYouTrying: Int = 0,
    @SerializedName("_id")
    val id: String = "",
    val singleOrMultipleBirth: Int = 0,
    @SerializedName("isActive")
    val isActive: Boolean = false,
    @SerializedName("kinshipReason")
    val kinshipReason: Int = 0,
    @SerializedName("singleOrMultiplePregnancy")
    var singleOrMultiplePregnancy: Int = 0,
    @SerializedName("lastName")
    val lastName: String = "",
    @SerializedName("lat")
    val lat: String = "",
    @SerializedName("long")
    val long: String = "",
    @SerializedName("phoneNumber")
    val phoneNumber: String = "",
    @SerializedName("step")
    val step: Int = 0,
    @SerializedName("updated_at")
    val updatedAt: String = "",
    @SerializedName("userId")
    val userId: String = "",
    @SerializedName("__v")
    val v: Int = 0,
    @SerializedName("zipcode")
    val zipcode: Int = 0,
    @SerializedName("newEmail") val newEmail: String = "",
    @SerializedName("editSingleOrmultipleGender")
    var editSingleOrmultipleGender: Int? = null
) : Serializable

data class EditProfileChildDate(
    //var title: String? = null,
    var date: Long? = null
)