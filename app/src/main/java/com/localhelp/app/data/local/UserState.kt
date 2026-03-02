package com.localhelp.app.data.local

import androidx.compose.runtime.compositionLocalOf
import com.localhelp.app.model.response.UserResponse

val LocalUser = compositionLocalOf <UserResponse?>{
    error("No UserProvider found!")
}

data class UserState(
    val user: UserResponse? = null,
    val token: String? = null
)