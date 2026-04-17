package com.localhelp.app.ui.common.myjobs

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
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Message
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
    val buttonText = when (status) {
        "ACCEPTED", "ON_THE_WAY" -> "Chỉ đường"
        "WORKING" -> "Cập nhật"
        else -> "Chi tiết"
    }

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
            Icon(Icons.Filled.Directions, contentDescription = null, modifier = Modifier.size(16.dp).padding(end = 4.dp))
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
        Column(modifier = Modifier.padding(16.dp).fillMaxWidth()) {
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
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
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
                val isCompleted = matchedProgress?.isCompleted == true || (isJobCompleted && matchedProgress != null)
                val isCurrent = matchedProgress?.isCurrent == true || (jobStatus == stepName && matchedProgress != null)

                val nodeColor = when {
                    isCompleted -> Color(0xFF059669)
                    isCurrent -> Color(0xFFE04F43)
                    else -> Color.LightGray
                }

                val isNextStepStarted = if (!isLastItem) progresses.any { it.stepName == timelineSteps[index + 1] } else false
                val lineColor = if (isCompleted && isNextStepStarted) Color(0xFF059669) else Color.LightGray

                Row(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.width(24.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(16.dp)
                                .clip(CircleShape)
                                .background(nodeColor)
                                .border(2.dp, nodeColor.copy(alpha = 0.3f), CircleShape)
                        )
                        if (!isLastItem) {
                            Box(
                                modifier = Modifier
                                    .width(2.dp)
                                    .heightIn(min = 40.dp)
                                    .weight(1f, fill = false)
                                    .background(lineColor)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.padding(bottom = if (!isLastItem) 16.dp else 0.dp)) {
                        Text(
                            text = getStepDisplayName(stepName, isHost),
                            fontWeight = if (isCurrent || isCompleted) FontWeight.Bold else FontWeight.Normal,
                            color = if (isCurrent) Color(0xFFE04F43) else if(isCompleted) Color.Black else Color.Gray,
                            fontSize = 14.sp
                        )

                        if (matchedProgress != null && !matchedProgress.description.isNullOrEmpty()) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = matchedProgress.description,
                                color = Color.Gray,
                                fontSize = 12.sp
                            )
                        }

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
    Column {
        Text("Ảnh bằng chứng", fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(images) { img ->
                AsyncImage(
                    model = img.imageUrl, contentDescription = null, contentScale = ContentScale.Crop,
                    modifier = Modifier.size(80.dp).clip(RoundedCornerShape(8.dp)).background(Color.LightGray)
                )
            }
        }
    }
}

@Composable
fun ReviewDisplayCard(review: ReviewResponse, isHost: Boolean) {
    Card(colors = CardDefaults.cardColors(containerColor = Color(0xFFFEF3C7)), shape = RoundedCornerShape(12.dp)) {
        Column(modifier = Modifier.padding(16.dp).fillMaxWidth()) {
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
        modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.3f)).clickable(enabled = false) {},
        contentAlignment = Alignment.Center
    ) { CircularProgressIndicator(color = Color.White) }
}