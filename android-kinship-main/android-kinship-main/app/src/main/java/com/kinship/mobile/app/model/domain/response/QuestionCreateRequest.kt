package com.kinship.mobile.app.model.domain.response

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class QuestionCreateRequest(
    val conceiveValue: Int = 0,
    val pregnantValue: Int = 0,
    val babyValue: Int = 0,
    val adoptedValue: Int = 0,

    )

data class QuesRequest(
    var howLongYouAreTrying: Int? =0,


)

