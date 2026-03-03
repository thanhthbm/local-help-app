package com.localhelp.app.ui.common.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

@Composable
fun CategorySection() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        CategoryItem(
            title = "Tất cả",
            icon = Icons.Default.GridView,
            containerColor = Color(0xFFED7D68),
            contentColor = Color.White
        )
        CategoryItem(
            title = "Dọn dẹp",
            icon = Icons.Default.CleaningServices,
            containerColor = Color.White,
            contentColor = Color(0xFF4CAF50)
        )
        CategoryItem(
            title = "Giao hàng",
            icon = Icons.Default.LocalShipping,
            containerColor = Color.White,
            contentColor = Color(0xFF2196F3)
        )
    }
}

@Composable
fun CategoryItem(
    title: String,
    icon: ImageVector,
    containerColor: Color,
    contentColor: Color
){
    Surface(
        shape = RoundedCornerShape(24.dp),
        color = containerColor,
        border = if (containerColor == Color.White) BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.5f)) else null,
        modifier = Modifier.height(40.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(icon, contentDescription = null,
                tint = contentColor,
                modifier = Modifier.size(18.dp)
            )

            Spacer(modifier = Modifier.width(8.dp))

            Text(text = title,
                color = if (containerColor == Color.White) Color.Black else Color.White,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}