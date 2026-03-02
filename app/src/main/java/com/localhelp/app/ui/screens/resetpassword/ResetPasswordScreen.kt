package com.localhelp.app.ui.screens.resetpassword

import android.annotation.SuppressLint
import android.app.Activity
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
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.localhelp.app.ui.screens.login.CustomLoginTextField

@SuppressLint("ContextCastToActivity")
@Composable
fun ResetPasswordScreen(
    viewModel: ForgotPasswordViewModel,
    onOtpSent: () -> Unit,
    onBack: () -> Unit
) {
    Column (modifier = Modifier.fillMaxSize().padding(24.dp)) {
        IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, null) }
        Text("Khôi phục mật khẩu", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Text("Nhập email hoặc số điện thoại của bạn để nhận mã xác thực", modifier = Modifier.padding(vertical = 16.dp))

        CustomLoginTextField(
            value = viewModel.phoneNumber,
            onValueChange = { viewModel.phoneNumber = it },
            placeholder = "Nhập email hoặc số điện thoại"
        )

        Spacer(modifier = Modifier.height(1.dp))

        val activity = LocalContext.current as Activity

        Button(
            onClick = { viewModel.sendOtp(activity, onOtpSent) },
            modifier = Modifier.fillMaxWidth().height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFED7D68))
        ) {
            Text("Gửi mã OTP", color = Color.White, fontWeight = FontWeight.Bold)
        }

        OutlinedButton (
            onClick = onBack,
            modifier = Modifier.fillMaxWidth().padding(top = 12.dp).height(56.dp)
        ) {
            Text("Quay lại", color = Color.Black)
        }
    }
}