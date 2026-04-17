package com.localhelp.app.ui.screens.jobmanagement

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.localhelp.app.model.response.JobResponse
import com.localhelp.app.ui.common.myjobs.TaskActionButton
import com.localhelp.app.ui.common.myjobs.TaskBottomInfo
import com.localhelp.app.ui.common.myjobs.TaskStatusBadge
import java.text.DecimalFormat

import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage

@Composable
fun MyTasksScreen(
    viewModel: MyTasksViewModel,
    onNavigateToDetail: (Long) -> Unit
) {
    // ... (rest of the screen logic)
}

@Composable
fun MyTaskJobCard(job: JobResponse, onClickAction: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(modifier = Modifier.padding(16.dp)) {
            val imageUrl = job.images?.firstOrNull()
            if (imageUrl != null) {
                AsyncImage(
                    model = imageUrl,
                    contentDescription = null,
                    modifier = Modifier
                        .size(80.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFFF3F4F6)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No Image", fontSize = 10.sp, color = Color.Gray)
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Text(
                        text = job.title ?: "",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = Color.Black,
                        modifier = Modifier.weight(1f).padding(end = 8.dp),
                        maxLines = 1
                    )
                    TaskStatusBadge(status = job.status?.name ?: "APPLIED")
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "👤 Người thuê: ${job.creatorName ?: "Khách hàng"}",
                    color = Color.Gray,
                    fontSize = 12.sp,
                    maxLines = 1
                )

                Spacer(modifier = Modifier.height(8.dp))

                val priceFormatted = DecimalFormat("#,###").format(job.price ?: 0)
                Text(
                    text = "$priceFormatted đ",
                    color = Color(0xFFE04F43),
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TaskBottomInfo(job = job)
                    TaskActionButton(status = job.status?.name ?: "APPLIED", onClick = onClickAction)
                }
            }
        }
    }
}
