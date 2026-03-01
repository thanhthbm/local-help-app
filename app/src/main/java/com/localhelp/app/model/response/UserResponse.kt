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

    val reputationScore: Double,
    val role: UserRole,
    val status: UserStatus,
    val createdAt: String
)