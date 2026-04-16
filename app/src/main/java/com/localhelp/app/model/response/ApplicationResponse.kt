package com.localhelp.app.model.response

data class ApplicationResponse(
    val applicationId: Long,
    val helperId: Long,
    val helperName: String,
    val helperAvatar: String?,
    val helperRating: Double,
    val status: String,
    val appliedAt: String
)