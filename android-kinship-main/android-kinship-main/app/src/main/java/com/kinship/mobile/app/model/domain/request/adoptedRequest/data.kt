package com.kinship.mobile.app.model.domain.request.adoptedRequest

data class data(
    val babyBornDate: List<String>,
    val childHasSpecialNeed: Int,
    val countrycode: Int,
    val dateOfBirth: String,
    val firstName: String,
    val firstTimeMom: Int,
    val hobbies: List<String>,
    val howLongYouAreTrying: Int,
    val howYouTrying: Int,
    val kinshipReason: Int,
    val lastName: String,
    val lat: String,
    val long: String,
    val multipleGender: Int,
    val phoneNumber: String,
    val singleGender: Int,
    val singleOrMultipleBirth: Int,
    val singleOrMultiplePregnancy: Int,
    val step: Int,
    val whenIsYourDueDate: String,
    val zipcode: Int
)