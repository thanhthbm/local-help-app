package com.localhelp.app.ui.screens.resetpassword

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.localhelp.app.ui.screens.login.CustomLoginTextField

@Composable
fun NewPasswordScreen(
    viewModel: ForgotPasswordViewModel,
    onSuccess: () -> Unit
) {
    Column(modifier = Modifier
        .fillMaxSize()
        .padding(24.dp)) {
        IconButton(onClick = { /* Back */ }) { Icon(Icons.Default.Close, null) }
        Text(
            "Đổi mật khẩu mới",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(32.dp))

        CustomLoginTextField(
            value = viewModel.newPassword,
            onValueChange = { viewModel.newPassword = it },
            placeholder = "Mật khẩu mới", isPassword = true
        )

        Spacer(modifier = Modifier.height(16.dp))
        CustomLoginTextField(
            value = viewModel.confirmPassword,
            onValueChange = { viewModel.confirmPassword = it },
            placeholder = "Xác nhận mật khẩu",
            isPassword = true
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = { viewModel.resetPassword(onSuccess) },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFED7D68))
        ) {
            Text("Đổi mật khẩu", color = Color.White)
        }
    }
}