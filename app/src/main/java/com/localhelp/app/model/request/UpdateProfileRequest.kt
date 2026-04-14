package com.localhelp.app.model.request

import com.localhelp.app.model.constant.GenderEnum

data class UpdateProfileRequest(
    val fullName: String?,
    val gender: GenderEnum?,
    val phone: String?,
    val bio: String?
)
