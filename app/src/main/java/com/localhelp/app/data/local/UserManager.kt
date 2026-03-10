package com.localhelp.app.data.local

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.localhelp.app.model.response.UserResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserManager @Inject constructor(
    private val tokenManager: TokenManager
){
    private val _currentUser = MutableStateFlow<UserResponse?>(null)
    val currentUser: StateFlow<UserResponse?> = _currentUser.asStateFlow()

    fun saveSession(user: UserResponse, token: String) {
        Log.d("DEBUG_USER", "===> Gọi hàm saveSession!")
        Log.d("DEBUG_USER", "Token nhận được: $token")
        Log.d("DEBUG_USER", "Data User nhận được: ID=${user.id}, Name=${user.fullName}, Email=${user.email}")

        tokenManager.saveToken(token)
        _currentUser.value = user

        Log.d("DEBUG_USER", "Đã gán xong _currentUser. Giá trị hiện tại: ${_currentUser.value?.fullName}")
    }

    fun updateProfile(user: UserResponse) {
        Log.d("DEBUG_USER", "===> Gọi hàm updateProfile! Name=${user.fullName}")
        _currentUser.value = user
    }

    fun logout(){
        Log.d("DEBUG_USER", "===> Đã gọi hàm LOGOUT, xóa toàn bộ data!")
        FirebaseAuth.getInstance().signOut()
        tokenManager.clearToken()
        _currentUser.value = null
    }
}