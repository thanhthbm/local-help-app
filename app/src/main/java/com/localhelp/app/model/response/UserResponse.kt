package com.localhelp.app.model.response

import com.localhelp.app.model.constant.GenderEnum
import com.localhelp.app.model.constant.UserRole
import com.localhelp.app.model.constant.UserStatus

data class UserResponse(
    val id: Long,
    val firebaseUid: String,
    val email: String,

    val fullName: String?,
    val phone: String?,
    val avatarUrl: String?,
    val gender: GenderEnum?,
    val bio: String?,

    val reputationScore: Double,
    val role: UserRole,
    val status: UserStatus,
    val createdAt: String,

    val completedJobs: Int = 0,
    val totalReviews: Int = 0,
    val averageRating: Double = 0.0,
    val responseRate: Double = 0.0
)