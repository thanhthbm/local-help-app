package com.localhelp.app.ui.screens.jobdetail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.localhelp.app.model.constant.JobStatus
import java.text.DecimalFormat

val PrimaryOrange = Color(0xFFED7D68)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JobDetailScreen(
    onBackClick: () -> Unit,
    onMessageClick: (String, String, String?) -> Unit, // Điều hướng sang chat
    viewModel: JobDetailViewModel = hiltViewModel()
) {
    val job by viewModel.job.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val df = DecimalFormat("#,###")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Chi tiết công việc", fontSize = 18.sp, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) { Icon(Icons.AutoMirrored.Filled.ArrowBack, null) }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        bottomBar = {
            if (job != null && !isLoading) {
                Surface(
                    color = Color.White,
                    shadowElevation = 8.dp
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                // Gọi hàm chat, truyền ID người tạo
                                onMessageClick(
                                    job!!.creatorId.toString(),
                                    job!!.creatorName ?: "Người dùng",
                                    job!!.creatorAvatar
                                )
                            },
                            modifier = Modifier.weight(1f).height(50.dp),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Nhắn tin", color = Color.Black, fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = { viewModel.acceptJob() },
                            enabled = job!!.status == JobStatus.OPEN && !isLoading, // Chỉ cho nhận khi Job đang OPEN
                            modifier = Modifier.weight(1f).height(50.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryOrange),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = if (job!!.status == JobStatus.OPEN) "Nhận việc" else "Đã có người nhận",
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        },
        containerColor = Color.White
    ) { paddingValues ->
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = PrimaryOrange)
            }
        } else if (job != null) {
            val currentJob = job!!
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
            ) {
                // Ảnh mô tả công việc (nếu có)
                if (!currentJob.images.isNullOrEmpty()) {
                    AsyncImage(
                        model = currentJob.images.first(),
                        contentDescription = "Hình ảnh công việc",
                        modifier = Modifier.fillMaxWidth().height(200.dp).background(Color(0xFFF5F5F5)),
                        contentScale = ContentScale.Crop
                    )
                }

                Column(modifier = Modifier.padding(16.dp)) {
                    // Tiêu đề và giá
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Text(
                            text = currentJob.title ?: "Không có tiêu đề",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.weight(1f)
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(
                            text = "${df.format(currentJob.price ?: 0)}đ",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = PrimaryOrange
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Địa chỉ
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.LocationOn, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = currentJob.address ?: "Chưa cập nhật địa chỉ", color = Color.DarkGray, fontSize = 14.sp)
                    }

                    HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp), color = Color(0xFFF0F0F0))

                    // Thông tin người đăng
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        AsyncImage(
                            model = if (currentJob.creatorAvatar.isNullOrEmpty()) "https://via.placeholder.com/150" else currentJob.creatorAvatar,
                            contentDescription = "Avatar",
                            modifier = Modifier.size(50.dp).clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = currentJob.creatorName ?: "Người ẩn danh", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFFFFC107), modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(text = "${currentJob.creatorRating ?: "0.0"} điểm uy tín", fontSize = 13.sp, color = Color.Gray)
                            }
                        }
                    }

                    HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp), color = Color(0xFFF0F0F0))

                    Text(text = "Mô tả công việc", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = currentJob.description ?: "Không có mô tả chi tiết.",
                        fontSize = 15.sp,
                        color = Color(0xFF4A4A4A),
                        lineHeight = 22.sp
                    )

                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        }
    }
}