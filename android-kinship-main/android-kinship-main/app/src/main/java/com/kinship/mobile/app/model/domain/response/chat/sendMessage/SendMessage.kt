package com.kinship.mobile.app.model.domain.response.chat.sendMessage

data class SendMessage(
    val __v: Int,
    val _id: String,
    val createdAt: String,
    val groupId: String,
    val grpSeenAt: List<GrpSeenAt>,
    val image: String,
    val isLiked: Boolean,
    val isLikedArray: List<Any>,
    val message: String,
    val receiverId: Any,
    val seenAt: Boolean,
    val senderId: String,
    val type: Int,
    val updatedAt: String,
    val userId: List<String>
)
data class GrpSeenAt(
    val _id: String,
    val id: String,
    val seen: Boolean
)
