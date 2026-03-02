package com.localhelp.app.ui.screens.resetpassword

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun OtpVerificationScreen(
    viewModel: ForgotPasswordViewModel,
    onVerified: () -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "Xác thực OTP",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        Text("Mã xác thực đã được gửi đến", color = Color.Gray)
        Text(viewModel.phoneNumber, fontWeight = FontWeight.Bold)

        Row(
            modifier = Modifier.padding(vertical = 32.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Demo 6 ô OTP
            repeat(6) { index -> OtpInputField(value = "", onValueChange = {}) }
        }

        Text("Gửi lại mã sau 00:30s", color = Color.Gray)
        Text(
            "Gửi lại mã",
            color = Color(0xFFED7D68),
            modifier = Modifier.clickable { /* Resend */ })

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = onVerified,
            modifier = Modifier.fillMaxWidth().height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFED7D68))
        ) {
            Text("Xác nhận", color = Color.White)
        }
    }
}