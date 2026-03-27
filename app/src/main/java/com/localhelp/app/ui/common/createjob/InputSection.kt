package com.localhelp.app.ui.common.createjob

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun InputSection(title: String, content: @Composable () -> Unit) {
    Column {
        Text(title, fontWeight = FontWeight.Medium, fontSize = 16.sp, color = Color.Black)
        Spacer(modifier = Modifier.height(8.dp))
        content()
    }
}