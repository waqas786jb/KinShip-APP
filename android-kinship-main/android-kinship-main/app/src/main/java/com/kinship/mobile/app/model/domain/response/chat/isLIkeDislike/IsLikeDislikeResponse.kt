package com.kinship.mobile.app.model.domain.response.chat.isLIkeDislike

data class IsLikeDislikeResponse(
    val _id: String,
    val isLiked: Boolean,
    val isLikedArray: List<IsLikedArray>
)

data class IsLikedArray(
    val _id: String,
    val id: String,
    val isLiked: Boolean
)
