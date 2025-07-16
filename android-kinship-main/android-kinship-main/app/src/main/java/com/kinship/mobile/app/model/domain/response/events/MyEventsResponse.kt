package com.kinship.mobile.app.model.domain.response.events


import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class MyEventsData(
    @SerializedName("endTime")
    val endTime: Long = 0,
    @SerializedName("eventDescription")
    val eventDescription: String = "",
    @SerializedName("eventName")
    val eventName: String? = "",
    @SerializedName("_id")
    val id: String = "",
    @SerializedName("isAllDay")
    val isAllDay: Int = 0,
    @SerializedName("lat")
    val lat: String = "",
    @SerializedName("link")
    val link: String = "",
    @SerializedName("location")
    val location: String? = "",
    @SerializedName("long")
    val long: String = "",
    @SerializedName("maybe")
    val maybe: Int = 0,
    @SerializedName("no")
    val no: Int = 0,
    @SerializedName("eventDate")
    val eventDate:Long=0,
    @SerializedName("photo")
    val photo: String?=null,
    @SerializedName("startTime")
    val startTime: Long = 0,
    @SerializedName("userId")
    val userId: String = "",
    @SerializedName("yes")
    val yes: Int = 0,
    @SerializedName("firstName")
    val firstName: String = "",
    @SerializedName("lastName")
    val lastName: String = "",
    @SerializedName("created_at")
    val createdAt: Long = 0
) : Serializable