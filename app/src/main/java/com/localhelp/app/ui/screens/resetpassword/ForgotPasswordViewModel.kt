package com.localhelp.app.ui.screens.resetpassword

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.localhelp.app.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ForgotPasswordViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {
    var email by mutableStateOf("")
    var otp by mutableStateOf("")
    var newPassword by mutableStateOf("")
    var confirmPassword by mutableStateOf("")
    var isLoading by mutableStateOf(false)
    var errorMsg by mutableStateOf<String?>(null)

    private var resetToken: String? = null

    fun setOobCode(code: String) {
        resetToken = code
    }

    fun sendOtp(onResult: (Boolean) -> Unit) {
        if (email.isBlank()) {
            errorMsg = "Vui lòng nhập email"
            return
        }
        isLoading = true
        viewModelScope.launch {
            authRepository.sendOtp(email)
                .onSuccess {
                    isLoading = false
                    onResult(true)
                }
                .onFailure {
                    isLoading = false
                    errorMsg = it.message
                    onResult(false)
                }
        }
    }

    fun verifyOtp(onResult: (Boolean) -> Unit) {
        if (otp.length < 6) {
            errorMsg = "Vui lòng nhập mã OTP"
            return
        }
        isLoading = true
        viewModelScope.launch {
            authRepository.verifyOtp(email, otp)
                .onSuccess { token ->
                    resetToken = token
                    isLoading = false
                    onResult(true)
                }
                .onFailure {
                    isLoading = false
                    errorMsg = it.message
                    onResult(false)
                }
        }
    }

    fun resetPassword(onSuccess: () -> Unit) {
        val token = resetToken
        if (token == null) {
            errorMsg = "Phiên xác thực đã hết hạn"
            return
        }

        if (newPassword != confirmPassword) {
            errorMsg = "Mật khẩu không khớp"
            return
        }

        isLoading = true
        viewModelScope.launch {
            authRepository.resetPassword(email, token, newPassword)
                .onSuccess {
                    isLoading = false
                    onSuccess()
                }
                .onFailure {
                    isLoading = false
                    errorMsg = it.message
                }
        }
    }
}
