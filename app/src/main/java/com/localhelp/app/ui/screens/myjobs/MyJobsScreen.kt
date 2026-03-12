package com.localhelp.app.ui.screens.myjobs

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.localhelp.app.model.constant.JobStatus
import com.localhelp.app.model.response.JobResponse
import java.text.DecimalFormat

val PrimaryOrange = Color(0xFFED7D68)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyJobsScreen(
    viewModel: MyJobsViewModel = hiltViewModel()
) {
    val jobs by viewModel.jobs.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Danh sách công việc", fontWeight = FontWeight.Bold, fontSize = 20.sp) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = Color.White
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            FilterSection()

            Spacer(modifier = Modifier.height(8.dp))

            Box(modifier = Modifier.fillMaxSize()) {
                if (isLoading) {
                    CircularProgressIndicator(
                        color = PrimaryOrange,
                        modifier = Modifier.align(Alignment.Center)
                    )
                } else if (jobs.isEmpty()) {
                    Text(
                        text = "Chưa có công việc nào",
                        color = Color.Gray,
                        modifier = Modifier.align(Alignment.Center)
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(jobs) { job ->
                            MyJobCard(job = job)
                        }
                        item { Spacer(modifier = Modifier.height(80.dp)) }
                    }
                }
            }
        }
    }
}

@Composable
fun FilterSection() {
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            // Nút "Tất cả"
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = PrimaryOrange,
                modifier = Modifier.height(36.dp)
            ) {
                Box(modifier = Modifier.padding(horizontal = 16.dp), contentAlignment = Alignment.Center) {
                    Text("Tất cả", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                }
            }
        }
        item {
            FilterDropdownChip(text = "Đang tìm người")
        }
        item {
            FilterDropdownChip(text = "Thời gian")
        }
    }
}

@Composable
fun FilterDropdownChip(text: String) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = Color.White,
        border = BorderStroke(1.dp, Color.LightGray),
        modifier = Modifier.height(36.dp).clickable { /* Mở menu chọn */ }
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = text, color = Color.DarkGray, fontSize = 14.sp)
            Spacer(modifier = Modifier.width(4.dp))
            Icon(Icons.Default.KeyboardArrowDown, contentDescription = null, tint = Color.DarkGray, modifier = Modifier.size(18.dp))
        }
    }
}

@Composable
fun MyJobCard(job: JobResponse) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        border = BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.5f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth()
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    val isCompleted = job.status == JobStatus.COMPLETED
                    Text(
                        text = job.categoryName?.uppercase() ?: "KHÁC",
                        color = if (isCompleted) Color.Gray else PrimaryOrange,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .background(if (isCompleted) Color(0xFFF5F5F5) else Color(0xFFFFF0ED), RoundedCornerShape(4.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = job.createdAt ?: "", color = Color.Gray, fontSize = 12.sp)
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Tiêu đề
                Text(
                    text = job.title ?: "Không có tiêu đề",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = Color(0xFF2C3E50)
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Vị trí / Địa chỉ
                Row(verticalAlignment = Alignment.CenterVertically) {
                    val isCompleted = job.status == JobStatus.COMPLETED
                    Icon(
                        imageVector = if (isCompleted) Icons.Default.CheckCircle else Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = Color.Gray,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (isCompleted) "Đã hoàn thành" else (job.address ?: ""),
                        color = Color.Gray,
                        fontSize = 13.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Trạng thái & Hành động
                JobStatusActionRow(job)
            }

            Spacer(modifier = Modifier.width(12.dp))

            val imageUrl = job.images?.firstOrNull()
            if (!imageUrl.isNullOrEmpty()) {
                AsyncImage(
                    model = imageUrl,
                    contentDescription = null,
                    modifier = Modifier
                        .size(80.dp)
                        .clip(RoundedCornerShape(12.dp)),
                    contentScale = ContentScale.Crop
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .background(Color(0xFFF0F0F0), RoundedCornerShape(12.dp))
                )
            }
        }
    }
}

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