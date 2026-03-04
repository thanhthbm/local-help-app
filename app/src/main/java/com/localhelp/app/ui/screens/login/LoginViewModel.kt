package com.localhelp.app.ui.screens.login

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.localhelp.app.data.repository.AuthRepository
import com.localhelp.app.model.response.UserResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    var email by mutableStateOf("")
    var password by mutableStateOf("")
    var isLoading by mutableStateOf(false)
    var loginError by mutableStateOf<String?>(null)

    fun onLoginClick(onSuccess: (UserResponse) -> Unit) {
        if (email.isEmpty() || password.isEmpty()){
            loginError = "Vui lòng nhập đầy đủ thông tin đăng nhập."
            return
        }

        isLoading = true
        loginError = null

        // Bước 1: Gọi Firebase thông qua callback truyền thống
        authRepository.loginFirebase(email, password) { firebaseResult ->
            firebaseResult.onSuccess { token ->

                // Bước 2: Khi đã có token, dùng viewModelScope để gọi API (suspend function)
                viewModelScope.launch {
                    val backendResult = authRepository.syncWithBackend(token)
                    isLoading = false

                    backendResult.onSuccess { userResponse ->
                        onSuccess(userResponse)
                    }.onFailure { error ->
                        loginError = error.message
                    }
                }

            }.onFailure { error ->
                isLoading = false
                loginError = error.message
            }
        }
    }
}