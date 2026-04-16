package com.localhelp.app.model.response

data class ReviewResponse(
    val id: Long,
    val rating: Int,
    val comment: String,
    val reviewerName: String,
    val reviewerAvatar: String?,
    val createdAt: String
)