package com.localhelp.app.data.local

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
        tokenManager.saveToken(token)
        _currentUser.value = user
    }

    fun updateProfile(user: UserResponse) {
        _currentUser.value = user
    }

    fun logout(){
        FirebaseAuth.getInstance().signOut()
        tokenManager.clearToken()
        _currentUser.value = null
    }
}