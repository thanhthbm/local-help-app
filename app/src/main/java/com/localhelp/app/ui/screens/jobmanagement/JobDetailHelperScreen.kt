package com.localhelp.app.ui.screens.jobmanagement

import android.app.DownloadManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Directions
import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.localhelp.app.model.response.ApplicationResponse
import com.localhelp.app.model.response.ReviewResponse
import com.localhelp.app.ui.common.myjobs.ReviewDisplayCard
import okhttp3.MultipartBody
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Download
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import com.localhelp.app.ui.common.myjobs.ActionLoadingOverlay
import com.localhelp.app.ui.common.myjobs.FullscreenImageDialog
import com.localhelp.app.ui.common.myjobs.JobInfoHeader
import com.localhelp.app.ui.common.myjobs.PartnerCard
import com.localhelp.app.ui.common.myjobs.RemoteEvidenceSection
import com.localhelp.app.ui.common.myjobs.TimelineSection
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.FileOutputStream

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JobDetailHelperScreen(
    viewModel: JobDetailHelperViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToUserProfile: (Long) -> Unit,
    onOpenGoogleMaps: (Double, Double) -> Unit,
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
            if (uiState is JobDetailHelperUiState.Success) {
                val state = uiState as JobDetailHelperUiState.Success
                HelperBottomBar(state, viewModel, onOpenGoogleMaps)
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .background(Color(0xFFF9FAFB))) {
            when (val state = uiState) {
                is JobDetailHelperUiState.Loading -> CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                is JobDetailHelperUiState.Error -> Text(state.message, color = Color.Red, modifier = Modifier.align(Alignment.Center))
                is JobDetailHelperUiState.Success -> {
                    val status = state.jobInfo.status?.name ?: "APPLIED"
                    LazyColumn(modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        item {
                            PartnerCard(
                                roleTitle = "Người thuê",
                                partnerName = state.jobInfo.creatorName ?: "Khách hàng",
                                partnerId = state.jobInfo.creatorId,
                                onNavigate = onNavigateToUserProfile,
                                onChat = {
                                    state.conversationId?.let{
                                        onNavigateToChat(it, state.jobInfo.creatorName ?: "Khách Hàng", state.jobInfo.creatorAvatar, state.jobInfo.creatorId)
                                    }
                                },
                                avatarUrl = state.jobInfo.creatorAvatar
                            )
                        }
                        item { JobInfoHeader(state.jobInfo, onNavigateToJobDetail) }
                        item { TimelineSection(state.progresses, status, isHost = false) }

                        if (status == "WORKING") item { LocalEvidenceSection(state.selectedLocalImages, viewModel) }
                        if (state.evidenceImages.isNotEmpty()) item { RemoteEvidenceSection(state.evidenceImages) }
                        if (status == "COMPLETED") {
                            item {
                                if (state.review != null) ReviewDisplayCard(state.review, false)
                                else Text("Chủ nhà chưa để lại đánh giá.", color = Color.Gray, fontSize = 14.sp)
                            }
                        }
                    }
                    if (state.isActionLoading) ActionLoadingOverlay()
                }
            }
        }
    }
}

@Composable
fun LocalEvidenceSection(images: List<Uri>, viewModel: JobDetailHelperViewModel) {
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetMultipleContents()) { uris ->
        viewModel.addLocalImages(uris)
    }
    var previewUri by remember { mutableStateOf<Uri?>(null) }

    if (previewUri != null) {
        FullscreenImageDialog(
            imageModel = previewUri!!,
            onDismiss = { previewUri = null },
            showDownload = false
        )
    }

    Column {
        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
            Text("Ảnh bằng chứng", fontWeight = FontWeight.Bold)
            Icon(
                Icons.Filled.CameraAlt,
                contentDescription = "Add",
                modifier = Modifier.clickable { launcher.launch("image/*") }
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(images) { uri ->
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { previewUri = uri }
                ) {
                    AsyncImage(
                        model = uri,
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                    IconButton(
                        onClick = { viewModel.removeLocalImage(uri) },
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .size(24.dp)
                            .background(Color.Black.copy(0.5f), CircleShape)
                    ) {
                        Icon(Icons.Filled.Close, null, tint = Color.White, modifier = Modifier.size(16.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun HelperBottomBar(state: JobDetailHelperUiState.Success, viewModel: JobDetailHelperViewModel, onMap: (Double, Double) -> Unit) {
    val status = state.jobInfo.status?.name ?: "APPLIED"
    val context = LocalContext.current

    Surface(shadowElevation = 8.dp, color = Color.White) {
        Row(modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            when (status) {
                "APPLIED" -> Button(onClick = {}, enabled = false, modifier = Modifier.fillMaxWidth()) { Text("Đã gửi yêu cầu - Chờ duyệt") }
                "ACCEPTED" -> {
                    OutlinedButton(onClick = { onMap(state.jobInfo.latitude ?: 0.0, state.jobInfo.longitude ?: 0.0) }, modifier = Modifier.weight(1f)) { Icon(Icons.Filled.Directions, null); Spacer(Modifier.width(4.dp)); Text("Chỉ đường") }
                    Button(onClick = { viewModel.updateStatusMoving() }, modifier = Modifier.weight(1f), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE04F43))) { Text("Bắt đầu đi") }
                }
                "ON_THE_WAY" -> {
                    OutlinedButton(onClick = {onMap(state.jobInfo.latitude ?: 0.0, state.jobInfo.longitude ?: 0.0) }, modifier = Modifier.weight(1f)) { Icon(Icons.Filled.Directions, null); Spacer(Modifier.width(4.dp)); Text("Chỉ đường") }
                    Button(onClick = { viewModel.updateStatusArrived() }, modifier = Modifier.weight(1f), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF059669))) { Text("Đã đến nơi") }
                }
                "WORKING" -> {
                    Button(
                        onClick = {
                            viewModel.submitEvidence(context)
                        },
                        enabled = state.selectedLocalImages.isNotEmpty(), modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF059669))
                    ) { Text("Gửi bằng chứng & Hoàn thành") }
                }
                "PENDING_PAYMENT" -> Button(onClick = {}, enabled = false, modifier = Modifier.fillMaxWidth()) { Text("Đang chờ thanh toán") }
            }
        }
    }
}