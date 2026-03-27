package com.localhelp.app.ui.common.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.localhelp.app.ui.screens.profile.OrangePrimary

@Composable
fun StatsSection(completedJobs: String, responseRate: String) {
    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        StatCard(modifier = Modifier.weight(1f), icon = Icons.Outlined.CheckCircle, iconTint = OrangePrimary, value = completedJobs, label = "Việc đã xong")
        StatCard(modifier = Modifier.weight(1f), icon = Icons.Outlined.ChatBubbleOutline, iconTint = Color(0xFF4A90E2), value = responseRate, label = "Tỉ lệ phản hồi")
    }
}