package com.localhelp.app.ui.common.profile

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.localhelp.app.ui.screens.profile.GrayBackground
import com.localhelp.app.ui.screens.profile.GrayText
import com.localhelp.app.ui.screens.profile.GreenLight
import com.localhelp.app.ui.screens.profile.GreenText
import com.localhelp.app.ui.screens.profile.OrangeLight
import com.localhelp.app.ui.screens.profile.OrangePrimary

import com.localhelp.app.model.response.JobResponse
import com.localhelp.app.ui.screens.profile.GrayBackground
import com.localhelp.app.ui.screens.profile.GrayText
import com.localhelp.app.ui.screens.profile.GreenLight
import com.localhelp.app.ui.screens.profile.GreenText
import com.localhelp.app.ui.screens.profile.OrangeLight
import com.localhelp.app.ui.screens.profile.OrangePrimary
import com.localhelp.app.model.constant.JobStatus

@Composable
fun JobsSection(jobs: List<JobResponse>, onJobClick: (Long) -> Unit = {}) {
    Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = "Việc đã đăng",
            color = OrangePrimary,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        HorizontalDivider(color = OrangePrimary, thickness = 2.dp, modifier = Modifier.width(160.dp))
        HorizontalDivider(color = Color.LightGray, thickness = 1.dp)

        Spacer(modifier = Modifier.height(16.dp))

        if (jobs.isEmpty()) {
            Text("Chưa có công việc nào", color = GrayText, modifier = Modifier.padding(16.dp))
        } else {
            jobs.forEach { job ->
                val statusColor = when (job.status) {
                    JobStatus.OPEN -> OrangePrimary
                    JobStatus.COMPLETED -> GreenText
                    else -> GrayText
                }
                val statusBg = when (job.status) {
                    JobStatus.OPEN -> OrangeLight
                    JobStatus.COMPLETED -> GreenLight
                    else -> GrayBackground
                }
                
                JobCard(
                    title = job.title ?: "Không tiêu đề",
                    subtitle = "${job.createdAt?.take(10) ?: "..."} • ${job.price?.toInt() ?: 0}đ",
                    status = when(job.status) {
                        JobStatus.OPEN -> "Đang tìm"
                        JobStatus.ACCEPTED -> "Đã nhận"
                        JobStatus.WORKING -> "Đang làm"
                        JobStatus.COMPLETED -> "Hoàn thành"
                        JobStatus.CANCELLED -> "Đã hủy"
                        else -> "Không rõ"
                    },
                    statusBg = statusBg,
                    statusColor = statusColor,
                    actionText = "Xem chi tiết",
                    onClick = { onJobClick(job.id) }
                )
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}