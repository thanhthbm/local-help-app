package com.localhelp.app.ui.screens.resetpassword

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.localhelp.app.ui.common.login.CustomLoginTextField

/**
 * Màn hình bước 2 của luồng khôi phục mật khẩu.
 *
 * Người dùng nhập OTP để backend xác thực và trả về resetToken.
 *
 * @param viewModel ViewModel quản lý email, OTP và trạng thái xác thực.
 * @param onOtpVerified Callback điều hướng sang màn nhập mật khẩu mới.
 * @param onBack Callback quay lại màn trước.
 */
@Composable
fun OtpVerificationScreen(
    viewModel: ForgotPasswordViewModel,
    onOtpVerified: () -> Unit,
    onBack: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize().padding(24.dp)) {
            IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, null) }
            Text("Xác thực OTP", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
            Text("Vui lòng nhập mã OTP đã được gửi đến email ${viewModel.email}", modifier = Modifier.padding(vertical = 16.dp))

            CustomLoginTextField(
                value = viewModel.otp,
                onValueChange = { viewModel.otp = it },
                placeholder = "Nhập mã OTP"
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (viewModel.errorMsg != null) {
                Text(
                    text = viewModel.errorMsg!!,
                    color = Color.Red,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            // Xác thực OTP; thành công thì cho phép người dùng nhập mật khẩu mới.
            Button(
                onClick = {
                    viewModel.verifyOtp { success ->
                        if (success) {
                            onOtpVerified()
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFED7D68)),
                enabled = !viewModel.isLoading
            ) {
                Text("Xác nhận", color = Color.White)
            }
        }

        if (viewModel.isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center),
                color = Color(0xFFED7D68)
            )
        }
    }
}
