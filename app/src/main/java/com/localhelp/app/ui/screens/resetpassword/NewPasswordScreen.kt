package com.localhelp.app.ui.screens.resetpassword

import android.widget.Toast
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.localhelp.app.ui.common.login.CustomLoginTextField

@Composable
// Bước 3 của use case đổi mật khẩu: nhập mật khẩu mới và xác nhận để hoàn tất reset.
fun NewPasswordScreen(
    viewModel: ForgotPasswordViewModel,
    onSuccess: () -> Unit
) {
    val context = LocalContext.current

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)) {
            IconButton(onClick = { /* Thường thì quay lại Login hoặc Reset Email */ }) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
            }
            Text(
                "Đổi mật khẩu mới",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(32.dp))

            CustomLoginTextField(
                value = viewModel.newPassword,
                onValueChange = { viewModel.newPassword = it },
                placeholder = "Mật khẩu mới",
                isPassword = true
            )

            Spacer(modifier = Modifier.height(16.dp))
            CustomLoginTextField(
                value = viewModel.confirmPassword,
                onValueChange = { viewModel.confirmPassword = it },
                placeholder = "Xác nhận mật khẩu",
                isPassword = true
            )

            if (viewModel.errorMsg != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = viewModel.errorMsg!!,
                    color = Color.Red,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Gửi mật khẩu mới kèm resetToken đã xác thực ở bước OTP.
            Button(
                onClick = {
                    viewModel.resetPassword {
                        Toast.makeText(context, "Đổi mật khẩu thành công!", Toast.LENGTH_SHORT).show()
                        onSuccess()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFED7D68)),
                enabled = !viewModel.isLoading
            ) {
                Text("Đổi mật khẩu", color = Color.White)
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
