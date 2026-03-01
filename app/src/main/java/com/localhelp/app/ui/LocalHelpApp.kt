package com.localhelp.app.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.localhelp.app.ui.screens.login.LoginScreen
import com.localhelp.app.ui.screens.login.LoginViewModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue

@Composable
fun LocalHelpApp() {
    // Quản lý trạng thái đăng nhập trong ứng dụng
    var isLoggedIn by remember { mutableStateOf(false) }

    // Khởi tạo LoginViewModel
    val loginViewModel: LoginViewModel = viewModel()

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        if (!isLoggedIn) {
            // Hiển thị màn hình Login đã code
            LoginScreen(
                viewModel = loginViewModel,
                onLoginSuccess = {
                    isLoggedIn = true
                }
            )
        } else {
            // Màn hình tạm thời để Test sau khi Login thành công
            SuccessTestScreen(onLogout = { isLoggedIn = false })
        }
    }
}

@Composable
fun SuccessTestScreen(onLogout: () -> Unit) {
    Column (
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "🎉 ĐĂNG NHẬP THÀNH CÔNG!",
            style = MaterialTheme.typography.headlineMedium,
            color = Color(0xFF4CAF50), // Màu xanh lá
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = "Dữ liệu đã được đồng bộ với Backend Spring Boot.")

        Spacer(modifier = Modifier.height(32.dp))

        Button(onClick = onLogout) {
            Text("Đăng xuất để test lại")
        }
    }
}