package com.localhelp.app.model.response

data class UserSummary(
    val id: Long,
    val fullName: String?,
    val avatarUrl: String?,
    val reputationScore: Double
)
