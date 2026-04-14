package com.localhelp.app.ui.common.myjobs

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.localhelp.app.model.constant.JobStatus
import com.localhelp.app.model.response.JobResponse
import com.localhelp.app.utils.FormatterUtils
import java.text.DecimalFormat

@Composable
fun JobStatusActionRow(
    job: JobResponse
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        val orangePrimary = Color(0xFFED7D68)
        
        when (job.status) {
            JobStatus.OPEN -> {
                // Đang tìm người (Màu vàng)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Đang tìm người",
                        color = Color(0xFFF5B041),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .background(Color(0xFFFEF9E7), RoundedCornerShape(8.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
            JobStatus.IN_PROGRESS -> {
                // Đang thực hiện (Màu xanh ngọc)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Đang thực hiện",
                        color = Color(0xFF45B39D),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .background(Color(0xFFE8F8F5), RoundedCornerShape(8.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
                // Nút nhắn tin
                Surface(
                    color = Color(0xFFFFF0ED),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.clickable { /* Mở màn hình chat */ }
                ) {
                    Text(
                        text = "Nhắn tin",
                        color = orangePrimary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }
            JobStatus.COMPLETED -> {
                // Đã hoàn thành (Màu xanh lá)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Hoàn thành",
                        color = Color(0xFF27AE60),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .background(Color(0xFFE9F7EF), RoundedCornerShape(8.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
                Text(
                    text = "Chi tiết",
                    color = Color.Gray,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.clickable { /* Mở lại Job */ }
                )
            }
            else -> {}
        }
    }
}