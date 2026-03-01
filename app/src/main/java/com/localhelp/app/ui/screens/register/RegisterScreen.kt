package com.localhelp.app.ui.screens.register

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.localhelp.app.ui.screens.login.CustomLoginTextField

@Composable
fun RegisterScreen(
    viewModel: RegisterViewModel,
    onRegisterSuccess: () -> Unit,
    onBackClick: () -> Unit
) {
    Column (
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(horizontal = 24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "Tạo tài khoản mới",
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Bold
            ),
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(40.dp))

        // Email field
        CustomLoginTextField(
            value = viewModel.email,
            onValueChange = { viewModel.email = it },
            placeholder = "Email"
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Password field
        CustomLoginTextField(
            value = viewModel.password,
            onValueChange = { viewModel.password = it },
            placeholder = "Mật khẩu",
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
            Text(
                text = viewModel.errorMsg!!,
                color = Color.Red,
                modifier = Modifier.padding(top = 16.dp),
                style = MaterialTheme.typography.bodyMedium
            )
        }

        Spacer(modifier = Modifier.height(40.dp))

        Button (
            onClick = { viewModel.onRegisterClick { onRegisterSuccess() } },
            enabled = !viewModel.isLoading,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFED7D68) // Màu cam theo thiết kế
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            if (viewModel.isLoading) {
                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
            } else {
                Text(
                    text = "Đăng ký",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = Color.White
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Row() {
            Text(text = "Bạn đã có tài khoản? ", color = Color.Gray)
            Text(
                text = "Đăng nhập ngay",
                color = Color(0xFFED7D68),
                fontWeight = FontWeight.Bold,
                modifier = Modifier.clickable { onBackClick() }
            )
        }
    }
}