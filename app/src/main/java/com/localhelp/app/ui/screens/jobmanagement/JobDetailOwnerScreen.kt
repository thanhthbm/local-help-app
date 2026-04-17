package com.localhelp.app.ui.screens.jobmanagement

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.localhelp.app.model.response.ApplicationResponse
import com.localhelp.app.model.response.ReviewResponse
import com.localhelp.app.ui.common.myjobs.ActionLoadingOverlay
import com.localhelp.app.ui.common.myjobs.JobInfoHeader
import com.localhelp.app.ui.common.myjobs.PartnerCard
import com.localhelp.app.ui.common.myjobs.RemoteEvidenceSection
import com.localhelp.app.ui.common.myjobs.ReviewDisplayCard
import com.localhelp.app.ui.common.myjobs.TimelineSection

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JobDetailOwnerScreen(
    viewModel: JobDetailOwnerViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToUserProfile: (Long) -> Unit,
    onNavigateToChat: (String, String, String?, Long) -> Unit,
    onNavigateToJobDetail: (Long) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Chi tiết công việc", fontSize = 18.sp, fontWeight = FontWeight.Bold) },
                navigationIcon = { IconButton(onClick = onNavigateBack) { Icon(Icons.Filled.ArrowBack, null) } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        bottomBar = {
            if (uiState is JobDetailOwnerUiState.Success) {
                val state = uiState as JobDetailOwnerUiState.Success
                if (state.jobInfo.status?.name == "PENDING_PAYMENT") {
                    Surface(shadowElevation = 8.dp, color = Color.White) {
                        Box(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                            Button(
                                onClick = { viewModel.confirmPayment() }, modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE04F43))
                            ) { Text("Xác nhận & Thanh toán") }
                        }
                    }
                }
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues).background(Color(0xFFF9FAFB))) {
            when (val state = uiState) {
                is JobDetailOwnerUiState.Loading -> CircularProgressIndicator(modifier = Modifier.align(
                    Alignment.Center))
                is JobDetailOwnerUiState.Error -> Text(state.message, color = Color.Red, modifier = Modifier.align(Alignment.Center))
                is JobDetailOwnerUiState.Success -> {
                    val status = state.jobInfo.status?.name ?: "OPEN"
                    LazyColumn(modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        if(status != "OPEN" && status != "CANCELLED"){
                            item {
                                PartnerCard(
                                    roleTitle = "Người thực hiện",
                                    partnerName = state.jobInfo.helperName ?: "Người giúp",
                                    partnerId = state.jobInfo.helperId,
                                    onNavigate = onNavigateToUserProfile,
                                    onChat = {
                                        state.jobInfo.helperId?.let{ id ->
                                            state.conversationId?.let{
                                                onNavigateToChat(it, state.jobInfo.helperName ?: "Người giúp", state.jobInfo.helperAvatar, id)
                                            }
                                        }
                                    },
                                    avatarUrl = state.jobInfo.helperAvatar
                                )
                            }
                        }
                        item { JobInfoHeader(state.jobInfo, onNavigateToJobDetail) }
                        item { TimelineSection(state.progresses, status, isHost = true) }

                        if (status == "OPEN") {
                            if(state.applications.isNotEmpty()){
                                item { Text("Danh sách ứng viên (${state.applications.size})", fontWeight = FontWeight.Bold) }
                                items(state.applications) { app -> ApplicationCard(app, onNavigateHelperProfile = onNavigateToUserProfile, onAccept = { viewModel.acceptApplication(app.applicationId) })}
                            } else {
                                item { Text("Chưa có ứng viên nào", fontWeight = FontWeight.Bold, modifier = Modifier.align(
                                    Alignment.Center)) }
                            }
                        }

                        if (state.evidenceImages.isNotEmpty()) item { RemoteEvidenceSection(state.evidenceImages) }
                        if (status == "COMPLETED") item { HostReviewSection(state.review, viewModel) }
                    }
                    if (state.isActionLoading) ActionLoadingOverlay()
                }
            }
        }
    }
}

@Composable
fun ApplicationCard(app: ApplicationResponse, onAccept: () -> Unit, onNavigateHelperProfile: (Long) -> Unit) {
    Card(colors = CardDefaults.cardColors(containerColor = Color.White), shape = RoundedCornerShape(8.dp)
        , modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp)
            .clickable { onNavigateHelperProfile(app.helperId) }
    ) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            if (!app.helperAvatar.isNullOrEmpty()) {
                AsyncImage(
                    model = app.helperAvatar,
                    contentDescription = "Avatar",
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
                    val initial = app.helperName.takeIf { it.isNotBlank() }?.substring(0, 1)?.uppercase() ?: "?"
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
                Text(app.helperName, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Text("⭐ ${app.helperRating}", color = Color(0xFFD97706), fontSize = 12.sp)
            }
            Button(onClick = onAccept, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF059669)), shape = RoundedCornerShape(6.dp)) { Text("Chọn", fontSize = 12.sp) }
        }
    }
}

@Composable
fun HostReviewSection(review: ReviewResponse?, viewModel: JobDetailOwnerViewModel) {
    if (review != null) {
        ReviewDisplayCard(review, true)
    } else {
        var rating by remember { mutableIntStateOf(5) }
        var comment by remember { mutableStateOf("") }
        Card(colors = CardDefaults.cardColors(containerColor = Color.White), shape = RoundedCornerShape(12.dp)) {
            Column(modifier = Modifier.padding(16.dp).fillMaxWidth()) {
                Text("Viết đánh giá", fontWeight = FontWeight.Bold)
                Row(modifier = Modifier.padding(vertical = 8.dp)) {
                    repeat(5) { i -> Icon(Icons.Filled.Star, null, tint = if (i < rating) Color(0xFFF59E0B) else Color.LightGray, modifier = Modifier.size(32.dp).clickable { rating = i + 1 }) }
                }
                OutlinedTextField(value = comment, onValueChange = { comment = it }, placeholder = { Text("Nhập nhận xét của bạn...") }, modifier = Modifier.fillMaxWidth().height(80.dp))
                Spacer(modifier = Modifier.height(8.dp))
                Button(onClick = { viewModel.submitReview(rating, comment) }, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE04F43))) { Text("Gửi đánh giá") }
            }
        }
    }
}