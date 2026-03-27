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
import androidx.compose.material3.Icon
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
import com.localhelp.app.ui.screens.myjobs.PrimaryOrange
import java.text.DecimalFormat

@Composable
fun JobStatusActionRow(job: JobResponse) {
    val df = DecimalFormat("#,###")

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        when (job.status) {
            JobStatus.OPEN -> {
                // Đang tìm người (Màu vàng)
                Text(
                    text = "Đang tìm người •",
                    color = Color(0xFFF5B041),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .background(Color(0xFFFEF9E7), RoundedCornerShape(12.dp))
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                )
                Text(
                    text = "${df.format(job.price)}đ",
                    color = PrimaryOrange,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }
            JobStatus.IN_PROGRESS -> {
                // Đang thực hiện (Màu xanh ngọc)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Đang thực hiện •",
                        color = Color(0xFF45B39D),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .background(Color(0xFFE8F8F5), RoundedCornerShape(12.dp))
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    AsyncImage(
                        model = "https://i.pravatar.cc/150?img=47",
                        contentDescription = null,
                        modifier = Modifier.size(26.dp).clip(CircleShape)
                    )
                }
                // Nút nhắn tin
                Text(
                    text = "Nhắn tin",
                    color = PrimaryOrange,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .background(Color(0xFFFFF0ED), RoundedCornerShape(8.dp))
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                        .clickable { /* Mở màn hình chat */ }
                )
            }
            JobStatus.COMPLETED -> {
                // Đã hoàn thành (Màu xanh lá)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFF4CAF50), modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Đã đánh giá ${job.creatorRating}",
                        color = Color(0xFF4CAF50),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Text(
                    text = "Xem lại",
                    color = Color.Gray,
                    fontSize = 13.sp,
                    modifier = Modifier.clickable { /* Mở lại Job */ }
                )
            }
            else -> {}
        }
    }
}