package com.localhelp.app.ui.common.createjob

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.localhelp.app.model.response.CategoryResponse
import com.localhelp.app.ui.screens.createjob.PrimaryOrange

@Composable
fun CategoryRow(categories: List<CategoryResponse>, selectedId: Long?, onSelect: (Long) -> Unit) {
    if (categories.isEmpty()) {
        CircularProgressIndicator(modifier = Modifier.padding(16.dp), color = PrimaryOrange)
        return
    }

    LazyRow(horizontalArrangement = Arrangement.spacedBy(28.dp)) {
        items(categories) { category ->
            val isSelected = category.id == selectedId

            // Xử lý an toàn khi parse mã màu từ API (Vd: #ED7D68)
            val categoryColor = try {
                Color(android.graphics.Color.parseColor(category.colorCode))
            } catch (e: Exception) { PrimaryOrange }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.clickable { onSelect(category.id) }
            ) {
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(if (isSelected) categoryColor else Color(0xFFF5F5F5)),
                    contentAlignment = Alignment.Center
                ) {
                    // Load ảnh thật từ URL thay vì icon tĩnh
                    AsyncImage(
                        model = category.iconUrl,
                        contentDescription = category.name,
                        modifier = Modifier.size(28.dp),
                        // Đổi màu icon thành Trắng (nếu được chọn) và Xám (nếu không)
                        colorFilter = if (isSelected) ColorFilter.tint(Color.White) else ColorFilter.tint(Color.Gray)
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = category.name,
                    fontSize = 13.sp,
                    color = if (isSelected) categoryColor else Color.Black,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                )
            }
        }
    }
}