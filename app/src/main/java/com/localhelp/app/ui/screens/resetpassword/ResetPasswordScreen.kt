package com.localhelp.app.ui.screens.resetpassword

import android.annotation.SuppressLint
import android.app.Activity
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
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.localhelp.app.ui.common.login.CustomLoginTextField

@SuppressLint("ContextCastToActivity")
@Composable
fun ResetPasswordScreen(
    viewModel: ForgotPasswordViewModel,
    onBack: () -> Unit
) {
    val context = LocalContext.current

    Box(modifier = Modifier.fillMaxSize()) {
        Column (modifier = Modifier.fillMaxSize().padding(24.dp)) {
            IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, null) }
            Text("Khôi phục mật khẩu", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
            Text("Nhập email của bạn để nhận liên kết xác thực", modifier = Modifier.padding(vertical = 16.dp))

            CustomLoginTextField(
                value = viewModel.email,
                onValueChange = { viewModel.email = it },
                placeholder = "Nhập email"
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

            Button(
                onClick = {
                    viewModel.sendResetEmail { success ->
                        if(success){
                            Toast.makeText(context, "Vui lòng kiểm tra email của bạn để đặt lại mật khẩu", Toast.LENGTH_LONG).show()
                        } else {
                            Toast.makeText(context, "Gửi email thất bại: ${viewModel.errorMsg}", Toast.LENGTH_SHORT).show()
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFED7D68)),
                enabled = !viewModel.isLoading
            ) {
                Text("Gửi liên kết đặt lại mật khẩu", color = Color.White)
            }

            OutlinedButton (
                onClick = onBack,
                modifier = Modifier.fillMaxWidth().padding(top = 12.dp).height(56.dp)
            ) {
                Text("Quay lại", color = Color.Black)
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