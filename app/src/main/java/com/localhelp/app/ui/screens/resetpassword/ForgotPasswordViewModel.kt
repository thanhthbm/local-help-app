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

/**
 * ViewModel quản lý toàn bộ luồng khôi phục mật khẩu.
 *
 * Luồng gồm 3 bước: gửi OTP qua email, xác thực OTP để lấy resetToken và đặt
 * mật khẩu mới bằng resetToken đã xác thực.
 */
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

    /**
     * Lưu token reset khi màn đặt mật khẩu mới được mở từ deep link/email.
     *
     * @param code Token reset nhận được từ đường dẫn xác thực.
     */
    fun setOobCode(code: String) {
        resetToken = code
    }

    /**
     * Gửi OTP khôi phục mật khẩu đến email đã nhập.
     *
     * @param onResult Callback trả true nếu gửi OTP thành công.
     */
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

    /**
     * Xác thực OTP và lưu resetToken cho bước đặt mật khẩu mới.
     *
     * @param onResult Callback trả true nếu OTP hợp lệ.
     */
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

    /**
     * Đặt lại mật khẩu sau khi OTP đã được xác thực.
     *
     * @param onSuccess Callback chạy khi backend đổi mật khẩu thành công.
     */
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
