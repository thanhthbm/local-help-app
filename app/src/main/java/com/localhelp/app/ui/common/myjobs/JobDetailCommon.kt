package com.localhelp.app.ui.common.myjobs

import android.app.DownloadManager
import android.content.Context
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Directions
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.localhelp.app.model.response.JobResponse
import android.net.Uri
import android.os.Environment
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Message
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.content.getSystemService
import coil.compose.AsyncImage
import com.localhelp.app.model.response.JobImageResponse
import com.localhelp.app.model.response.ProgressResponse
import com.localhelp.app.model.response.ReviewResponse
import java.text.DecimalFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Composable
fun TaskStatusBadge(status: String) {
    val (bgColor, textColor, text) = when (status) {
        "APPLIED" -> Triple(Color(0xFFF3F4F6), Color(0xFF4B5563), "Đã gửi yêu cầu")
        "ACCEPTED" -> Triple(Color(0xFFFEF3C7), Color(0xFFD97706), "Đã xác nhận")
        "REJECTED" -> Triple(Color(0xFFFEE2E2), Color(0xFFEF4444), "Bị từ chối")
        "ON_THE_WAY" -> Triple(Color(0xFFE0E7FF), Color(0xFF4338CA), "Đang đến")
        "WORKING" -> Triple(Color(0xFFFEF08A), Color(0xFF854D0E), "Đang làm")
        "PENDING_PAYMENT" -> Triple(Color(0xFFDCFCE7), Color(0xFF15803D), "Chờ thanh toán")
        "COMPLETED" -> Triple(Color(0xFFD1FAE5), Color(0xFF059669), "Hoàn thành")
        else -> Triple(Color(0xFFF3F4F6), Color(0xFF4B5563), status)
    }

    Surface(
        color = bgColor,
        shape = RoundedCornerShape(4.dp)
    ) {
        Text(
            text = text,
            color = textColor,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

@Composable
fun TaskBottomInfo(job: JobResponse) {
    val status = job.status?.name ?: "APPLIED"

    Row(verticalAlignment = Alignment.CenterVertically) {
        if (status == "ON_THE_WAY" && job.distance != null) {
            Icon(Icons.Filled.Directions, contentDescription = "Distance", modifier = Modifier.size(16.dp), tint = Color.Gray)
            Spacer(modifier = Modifier.width(4.dp))
            Text(text = "Cách ${String.format("%.1f", job.distance)} km", color = Color.Gray, fontSize = 13.sp)
        } else if (status == "PENDING_PAYMENT" || status == "COMPLETED") {
            Icon(Icons.Outlined.CheckCircle, contentDescription = "Done", modifier = Modifier.size(16.dp), tint = Color.Gray)
            Spacer(modifier = Modifier.width(4.dp))
            Text(text = "Hoàn thành công việc", color = Color.Gray, fontSize = 13.sp)
        } else {
            Icon(Icons.Filled.Schedule, contentDescription = "Time", modifier = Modifier.size(16.dp), tint = Color.Gray)
            Spacer(modifier = Modifier.width(4.dp))
            Text(text = "Hôm nay, 14:00", color = Color.Gray, fontSize = 13.sp)
        }
    }
}

@Composable
fun TaskActionButton(status: String, onClick: () -> Unit) {
    val buttonText = "Xem chi tiết"

    val btnColor = if (status == "REJECTED") Color(0xFFF87171) else Color(0xFFE04F43)

    Button(
        onClick = onClick,
        modifier = Modifier.height(36.dp),
        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = btnColor,
            contentColor = Color.White
        ),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 0.dp)
    ) {
        if (buttonText == "Chỉ đường") {
            Icon(Icons.Filled.Directions, contentDescription = null, modifier = Modifier
                .size(16.dp)
                .padding(end = 4.dp))
        }
        Text(buttonText, fontWeight = FontWeight.Medium, fontSize = 13.sp)
    }
}



@Composable
fun JobInfoHeader(
    job: JobResponse,
    onClick: (Long) -> Unit
    ) {
    Card(colors = CardDefaults.cardColors(containerColor = Color.White), shape = RoundedCornerShape(12.dp), modifier = Modifier.clickable{onClick(job.id)}) {
        Column(modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()) {
            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Text(job.title ?: "", fontWeight = FontWeight.Bold, fontSize = 20.sp, modifier = Modifier.weight(1f))
                TaskStatusBadge(job.status?.name ?: "OPEN")
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text("${DecimalFormat("#,###").format(job.price ?: 0)} đ", color = Color(0xFFE04F43), fontWeight = FontWeight.Bold, fontSize = 20.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Text("📍 ${job.address ?: ""}", color = Color.Gray, fontSize = 14.sp)
            if (!job.description.isNullOrEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(job.description, fontSize = 14.sp)
            }
        }
    }
}

@Composable
fun PartnerCard(
    roleTitle: String,
    partnerName: String,
    partnerId: Long?,
    avatarUrl: String?,
    onNavigate: (Long) -> Unit,
    onChat: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { partnerId?.let { onNavigate(it) } }
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (!avatarUrl.isNullOrEmpty()) {
                AsyncImage(
                    model = avatarUrl,
                    contentDescription = "Avatar của $partnerName",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(Color.LightGray)
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFE0E7FF)),
                    contentAlignment = Alignment.Center
                ) {
                    val initial = partnerName.takeIf { it.isNotBlank() }?.substring(0, 1)?.uppercase() ?: "?"
                    Text(
                        text = initial,
                        color = Color(0xFF4338CA),
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(roleTitle, color = Color.Gray, fontSize = 12.sp)
                Text(partnerName, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }

            IconButton(
                onClick = onChat,
                modifier = Modifier.background(Color(0xFFE0E7FF), CircleShape)
            ) {
                Icon(Icons.Filled.Message, contentDescription = "Message", tint = Color(0xFF4338CA))
            }
        }
    }
}
fun getStepDisplayName(step: String, isHost: Boolean): String {
    return when (step) {
        "OPEN" -> "Tạo công việc"
        "APPLIED" -> if (isHost) "Có người ứng tuyển" else "Đã gửi yêu cầu nhận việc"
        "ACCEPTED" -> "Đã chốt thợ"
        "ON_THE_WAY" -> "Đang di chuyển"
        "WORKING" -> "Đang làm việc"
        "PENDING_PAYMENT" -> "Chờ thanh toán"
        "COMPLETED" -> "Hoàn thành"
        "CANCELLED" -> "Đã hủy"
        "REJECTED" -> "Bị từ chối"
        else -> step
    }
}

fun formatTime(timeString: String?): String {
    if (timeString.isNullOrEmpty()) return ""
    return try {
        val parsed = LocalDateTime.parse(timeString)
        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")
        parsed.format(formatter)
    } catch (e: Exception) {
        timeString
    }
}

@Composable
fun TimelineSection(progresses: List<ProgressResponse>, jobStatus: String, isHost: Boolean) {
    val baseSteps = if (isHost) {
        listOf("OPEN", "ACCEPTED", "ON_THE_WAY", "WORKING", "PENDING_PAYMENT", "COMPLETED")
    } else {
        listOf("APPLIED", "ACCEPTED", "ON_THE_WAY", "WORKING", "PENDING_PAYMENT", "COMPLETED")
    }

    val timelineSteps = if (jobStatus == "CANCELLED" || jobStatus == "REJECTED") {
        progresses.map { it.stepName }
    } else {
        baseSteps
    }

    // Tìm index của bước xa nhất đã hoàn thành hoặc đang thực hiện dựa trên progresses
    val lastCompletedIndex = timelineSteps.indexOfLast { step ->
        progresses.any { it.stepName == step }
    }

    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Tiến độ công việc", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Spacer(modifier = Modifier.height(16.dp))

            timelineSteps.forEachIndexed { index, stepName ->
                val isLastItem = index == timelineSteps.size - 1
                val matchedProgress = progresses.find { it.stepName == stepName }
                
                val isJobCompleted = jobStatus == "COMPLETED"
                
                // Logic màu sắc mới:
                // 1. Nếu job đã xong -> Tất cả xanh
                // 2. Nếu bước này nằm trong quá khứ (index <= lastCompletedIndex) -> Xanh
                // 3. Nếu bước này là bước kế tiếp ngay sau bước cuối cùng đã xong -> Cam (đang chờ/làm)
                // 4. Còn lại -> Xám
                
                val nodeColor = when {
                    isJobCompleted || index <= lastCompletedIndex -> Color(0xFF059669) // Đã xong
                    index == lastCompletedIndex + 1 -> Color(0xFFE04F43) // Bước hiện tại/kế tiếp
                    else -> Color.LightGray // Chưa tới
                }

                // Sửa: Dùng <= để tô màu cả đoạn nối từ node đã xong tới node đang thực hiện
                val lineColor = if (isJobCompleted || index <= lastCompletedIndex) Color(0xFF059669) else Color.LightGray

                Row(modifier = Modifier
                    .fillMaxWidth()
                    .height(IntrinsicSize.Min)) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.width(24.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .padding(top = 2.dp)
                                .size(16.dp)
                                .clip(CircleShape)
                                .background(nodeColor)
                                .border(2.dp, nodeColor.copy(alpha = 0.3f), CircleShape)
                        )
                        if (!isLastItem) {
                            Box(
                                modifier = Modifier
                                    .width(2.dp)
                                    .fillMaxHeight()
                                    .background(lineColor)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.padding(bottom = if (!isLastItem) 16.dp else 0.dp)) {
                        val isHighlight = isJobCompleted || index <= lastCompletedIndex || index == lastCompletedIndex + 1
                        Text(
                            text = getStepDisplayName(stepName, isHost),
                            fontWeight = if (isHighlight) FontWeight.Bold else FontWeight.Normal,
                            color = if (index == lastCompletedIndex + 1 && !isJobCompleted) Color(0xFFE04F43) else if(index <= lastCompletedIndex || isJobCompleted) Color.Black else Color.Gray,
                            fontSize = 14.sp
                        )

                        if (matchedProgress != null && !matchedProgress.time.isNullOrEmpty()) {
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = formatTime(matchedProgress.time),
                                color = Color.Gray,
                                fontSize = 11.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun RemoteEvidenceSection(images: List<JobImageResponse>) {
    var initialIndex by remember { mutableStateOf<Int?>(null) }
    
    if (initialIndex != null) {
        FullscreenImagePagerDialog(
            images = images.map { it.imageUrl },
            initialPage = initialIndex!!,
            onDismiss = { initialIndex = null },
            showDownload = true
        )
    }
    Column {
        Text("Ảnh bằng chứng", fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(images.size) { index ->
                val img = images[index]
                AsyncImage(
                    model = img.imageUrl, contentDescription = null, contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(80.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.LightGray)
                        .clickable { initialIndex = index }
                )
            }
        }
    }
}

@Composable
fun FullscreenImagePagerDialog(
    images: List<String>,
    initialPage: Int = 0,
    onDismiss: () -> Unit,
    showDownload: Boolean = true
) {
    val context = LocalContext.current
    val pagerState = rememberPagerState(initialPage = initialPage, pageCount = { images.size })

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black),
            contentAlignment = Alignment.Center
        ) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize()
            ) { page ->
                AsyncImage(
                    model = images[page],
                    contentDescription = "Full Screen Image",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Fit
                )
            }

            // Top controls
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .align(Alignment.TopCenter)
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Page Indicator Text
                Surface(
                    color = Color.Black.copy(alpha = 0.5f),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Text(
                        text = "${pagerState.currentPage + 1} / ${images.size}",
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }

                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.background(Color.Black.copy(alpha = 0.5f), CircleShape)
                ) {
                    Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                }
            }

            if (showDownload) {
                IconButton(
                    onClick = {
                        downloadImage(context, images[pagerState.currentPage])
                    },
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .navigationBarsPadding()
                        .padding(bottom = 24.dp, end = 16.dp)
                        .background(Color.Black.copy(alpha = 0.5f), CircleShape)
                ) {
                    Icon(Icons.Default.Download, contentDescription = "Download", tint = Color.White)
                }
            }
        }
    }
}

@Composable
fun FullscreenImageDialog(
    imageModel: Any,
    onDismiss: () -> Unit,
    showDownload: Boolean = true
) {
    val context = LocalContext.current

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
                .clickable { onDismiss() },
            contentAlignment = Alignment.Center
        ) {
            AsyncImage(
                model = imageModel,
                contentDescription = "Full Screen Image",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Fit
            )

            IconButton(
                onClick = onDismiss,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 40.dp, end = 16.dp)
                    .background(Color.Black.copy(alpha = 0.5f), CircleShape)
            ) {
                Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
            }

            if (showDownload && imageModel is String) {
                IconButton(
                    onClick = {
                        downloadImage(context, imageModel)
                    },
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(bottom = 40.dp, end = 16.dp)
                        .background(Color.Black.copy(alpha = 0.5f), CircleShape)
                ) {
                    Icon(Icons.Default.Download, contentDescription = "Download", tint = Color.White)
                }
            }
        }
    }
}

fun downloadImage(context: Context, url: String) {
    try {
        val request = DownloadManager.Request(Uri.parse(url))
            .setTitle("Image Download")
            .setDescription("Downloading image from Local Help")
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "LocalHelp_${System.currentTimeMillis()}.jpg")
            .setAllowedOverMetered(true)
            .setAllowedOverRoaming(true)

        val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        downloadManager.enqueue(request)
        Toast.makeText(context, "Bắt đầu tải ảnh...", Toast.LENGTH_SHORT).show()
    } catch (e: Exception) {
        Toast.makeText(context, "Lỗi tải ảnh: ${e.message}", Toast.LENGTH_SHORT).show()
    }
}

@Composable
fun ReviewDisplayCard(review: ReviewResponse, isHost: Boolean) {
    Card(colors = CardDefaults.cardColors(containerColor = Color(0xFFFEF3C7)), shape = RoundedCornerShape(12.dp)) {
        Column(modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()) {
            Text("Đánh giá từ ${if (isHost) "bạn" else "chủ nhà"}", fontWeight = FontWeight.Bold, color = Color(0xFF92400E))
            Spacer(modifier = Modifier.height(4.dp))
            Row { repeat(5) { i -> Icon(Icons.Filled.Star, null, tint = if (i < review.rating) Color(0xFFF59E0B) else Color.LightGray, modifier = Modifier.size(16.dp)) } }
            Spacer(modifier = Modifier.height(8.dp))
            Text(review.comment, color = Color.DarkGray)
        }
    }
}

@Composable
fun ActionLoadingOverlay() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.3f))
            .clickable(enabled = false) {},
        contentAlignment = Alignment.Center
    ) { CircularProgressIndicator(color = Color.White) }
}