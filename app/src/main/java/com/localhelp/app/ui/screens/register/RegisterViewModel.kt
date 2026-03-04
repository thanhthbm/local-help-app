package com.localhelp.app.ui.screens.register

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import com.google.firebase.auth.FirebaseAuth
import com.localhelp.app.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {
    var email by mutableStateOf("")
    var password by mutableStateOf("")
    var confirmPassword by mutableStateOf("")
    var isLoading by mutableStateOf(false)
    var errorMsg by mutableStateOf<String?>(null)

    fun onRegisterClick(onSuccess: () -> Unit) {
        if (password != confirmPassword) {
            errorMsg = "Mật khẩu không khớp"
            return
        }

        if (password.length < 6) {
            errorMsg = "Mật khẩu phải có ít nhất 6 ký tự"
            return
        }

        isLoading = true
        errorMsg = null

        authRepository.registerFirebase(email, password) { result ->
            isLoading = false

            result.onSuccess {
                onSuccess()
            }.onFailure { error ->
                errorMsg = error.localizedMessage ?: "Đăng ký thất bại"
            }
        }
    }

}