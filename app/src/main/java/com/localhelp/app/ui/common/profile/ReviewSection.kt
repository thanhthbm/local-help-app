package com.localhelp.app.ui.common.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarHalf
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.localhelp.app.ui.screens.profile.GrayText
import com.localhelp.app.ui.screens.profile.OrangePrimary

@Composable
fun ReviewsSection(
    averageRating: Double,
    totalReviews: Int,
    reviews: List<com.localhelp.app.model.response.ReviewResponse> = emptyList(),
    isLoading: Boolean = false
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Đánh giá & Nhận xét", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Text("Xem chi tiết >", color = OrangePrimary, fontSize = 14.sp)
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(String.format("%.1f", averageRating), fontSize = 32.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Row {
                    val fullStars = averageRating.toInt()
                    val hasHalfStar = averageRating - fullStars >= 0.5

                    repeat(fullStars) { Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFFFFB400), modifier = Modifier.size(16.dp)) }
                    if (hasHalfStar) Icon(Icons.Default.StarHalf, contentDescription = null, tint = Color(0xFFFFB400), modifier = Modifier.size(16.dp))
                }
                Text("$totalReviews nhận xét", color = GrayText, fontSize = 13.sp)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (isLoading) {
            // Hiển thị loading nhỏ nếu cần
        } else if (reviews.isEmpty()) {
            Text("Chưa có đánh giá nào", color = GrayText, fontSize = 14.sp)
        } else {
            reviews.forEach { review ->
                ReviewItem(name = review.reviewerName, comment = review.comment)
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}