package com.localhelp.app.ui.screens.myjobs

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun JobAcceptSuccessScreen(
    onNavigateHome: () -> Unit
) {
    Scaffold { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Filled.CheckCircle,
                contentDescription = "Success",
                tint = Color(0xFF059669),
                modifier = Modifier.size(100.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Nhận việc thành công!",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Chủ nhà sẽ sớm nhận được thông báo. Bạn có thể theo dõi tiến độ trong phần Việc đã nhận.",
                textAlign = TextAlign.Center,
                color = Color.Gray,
                fontSize = 14.sp
            )

            Spacer(modifier = Modifier.height(40.dp))

            Button(
                onClick = onNavigateHome,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFE04F43)
                ),
                shape = MaterialTheme.shapes.medium
            ) {
                Text(
                    text = "Về trang chủ",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}