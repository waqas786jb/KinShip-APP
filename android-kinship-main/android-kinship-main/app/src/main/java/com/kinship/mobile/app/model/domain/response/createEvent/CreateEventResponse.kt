package com.kinship.mobile.app.model.domain.response.createEvent

import com.google.gson.annotations.SerializedName

data class CreateEventResponse(
    @SerializedName("_id") val _id: String,
    @SerializedName("endTime") val endTime: Long,
    @SerializedName("eventDate") val eventDate: Long,
    @SerializedName("eventDescription") val eventDescription: String,
    @SerializedName("eventName") val eventName: String,
    @SerializedName("isAllDay") val isAllDay: Int,
    @SerializedName("lat") val lat: String,
    @SerializedName("link")  val link: String,
    @SerializedName("location") val location: String,
    @SerializedName("long")  val long: String,
    @SerializedName("photo")  val photo: String,
    @SerializedName("")  val startTime: Long,
    @SerializedName("startTime") val userId: String
)
