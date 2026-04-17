package com.localhelp.app.ui.screens.jobdetail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
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
import com.localhelp.app.utils.FormatterUtils
import androidx.compose.foundation.clickable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.automirrored.filled.TrendingFlat
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Info
import androidx.compose.foundation.BorderStroke

val PrimaryOrange = Color(0xFFED7D68)
val BackgroundGray = Color(0xFFFDFDFD)
val TextGray = Color(0xFF757575)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JobDetailScreen(
    onBackClick: () -> Unit,
    onMessageClick: (String, String, String?, Long) -> Unit,
    onEditJob: (Long) -> Unit,
    onUserClick: (Long) -> Unit,
    onJobSuccessCallBack: () -> Unit,
    viewModel: JobDetailViewModel = hiltViewModel()
) {
    var showConfirmDialog by remember { mutableStateOf(false) }
    val job by viewModel.job.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val isApplied by viewModel.isApplied.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val acceptStatus by viewModel.acceptStatus.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()
    
    val isMyJob = remember(job, currentUser) {
        job?.creatorId != null && job?.creatorId == currentUser?.id
    }
    var showDeleteConfirm by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }

    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text("Xác nhận xóa") },
            text = { Text("Bạn có chắc chắn muốn xóa công việc này không?") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.deleteJob()
                    showDeleteConfirm = false
                }, colors = ButtonDefaults.textButtonColors(contentColor = Color.Red)) {
                    Text("Xóa")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = false }) { Text("Hủy") }
            },
            containerColor = Color.White
        )
    }

    LaunchedEffect(errorMessage) {
        errorMessage?.let { snackbarHostState.showSnackbar(it) }
    }

    LaunchedEffect(Unit) {
        viewModel.navigationEvent.collect { event ->
            when (event) {
                is JobDetailViewModel.JobDetailNavEvent.NavigateToChat -> onMessageClick(event.conversationId, event.partnerName, event.partnerAvatar, event.partnerId)
                is JobDetailViewModel.JobDetailNavEvent.NavigateToEditJob -> onEditJob(event.jobId)
                is JobDetailViewModel.JobDetailNavEvent.NavigateToUserProfile -> onUserClick(event.userId)
                is JobDetailViewModel.JobDetailNavEvent.JobDeleted -> onBackClick()
                is JobDetailViewModel.JobDetailNavEvent.NavigateToSuccess -> onJobSuccessCallBack()
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Chi tiết công việc", fontSize = 18.sp, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back") }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            if (job != null && !isLoading) {
                Surface(color = Color.White, shadowElevation = 8.dp) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(16.dp).navigationBarsPadding(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        if (isMyJob) {
                            OutlinedButton(onClick = { viewModel.onEditClick() }, modifier = Modifier.weight(1f)) { Text("Sửa") }
                            Button(
                                onClick = { showDeleteConfirm = true },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFEE2E2), contentColor = Color.Red)
                            ) { Text("Xóa") }
                        } else {
                            OutlinedButton(onClick = { viewModel.onChatClick() }, modifier = Modifier.weight(1f)) { Text("Chat") }
                            Button(
                                onClick = { showConfirmDialog = true },
                                enabled = job!!.status == JobStatus.OPEN && !isApplied,
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(containerColor = PrimaryOrange)
                            ) { Text(if (isApplied) "Đã nhận" else "Nhận việc") }
                        }
                    }
                }
            }
        }
    ) { padding ->
        if (isLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator(color = PrimaryOrange) }
        } else if (job != null) {
            val item = job!!
            var initialIndex by remember { mutableStateOf<Int?>(null) }
            
            if (initialIndex != null && !item.images.isNullOrEmpty()) {
                com.localhelp.app.ui.common.myjobs.FullscreenImagePagerDialog(
                    images = item.images!!,
                    initialPage = initialIndex!!,
                    onDismiss = { initialIndex = null }
                )
            }

            Column(Modifier.padding(padding).verticalScroll(rememberScrollState()).background(BackgroundGray)) {
                if (!item.images.isNullOrEmpty()) {
                    Box(modifier = Modifier.fillMaxWidth().height(250.dp)) {
                        val pagerState = rememberPagerState(pageCount = { item.images!!.size })
                        
                        HorizontalPager(
                            state = pagerState,
                            modifier = Modifier.fillMaxSize()
                        ) { page ->
                            AsyncImage(
                                model = item.images!![page],
                                contentDescription = null,
                                modifier = Modifier.fillMaxSize().clickable { initialIndex = page },
                                contentScale = ContentScale.Crop
                            )
                        }

                        if (item.images!!.size > 1) {
                            Surface(
                                modifier = Modifier
                                    .align(Alignment.BottomEnd)
                                    .padding(16.dp),
                                color = Color.Black.copy(alpha = 0.5f),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text(
                                    text = "${pagerState.currentPage + 1}/${item.images!!.size}",
                                    color = Color.White,
                                    fontSize = 12.sp,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }
                }
                Column(Modifier.padding(20.dp)) {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(item.title ?: "", fontSize = 22.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                        Text(FormatterUtils.formatPrice(item.price), color = PrimaryOrange, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    }
                    Spacer(Modifier.height(12.dp))
                    Row {
                        Icon(Icons.Default.AccessTime, null, tint = Color.Gray, modifier = Modifier.size(16.dp))
                        Text(" ${FormatterUtils.formatDateTime(item.createdAt)}", fontSize = 13.sp, color = Color.Gray)
                    }
                    Spacer(Modifier.height(24.dp))
                    // Creator Card
                    Surface(Modifier.fillMaxWidth().clickable { viewModel.onUserClick() }, shape = RoundedCornerShape(12.dp), border = BorderStroke(1.dp, Color(0xFFF0F0F0))) {
                        Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                            AsyncImage(model = item.creatorAvatar ?: "", contentDescription = null, modifier = Modifier.size(40.dp).clip(CircleShape), contentScale = ContentScale.Crop)
                            Spacer(Modifier.width(12.dp))
                            Column(Modifier.weight(1f)) {
                                Text(item.creatorName ?: "Người đăng", fontWeight = FontWeight.Bold)
                                Row {
                                    Icon(Icons.Default.Star, null, tint = Color(0xFFFFC107), modifier = Modifier.size(14.dp))
                                    Text(" ${item.creatorRating ?: 0.0}", fontSize = 12.sp)
                                }
                            }
                            Icon(Icons.Default.ChevronRight, null, tint = Color.LightGray)
                        }
                    }
                    Spacer(Modifier.height(24.dp))
                    Text("MÔ TẢ", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Text(item.description ?: "Không có mô tả", modifier = Modifier.padding(vertical = 8.dp))
                    Spacer(Modifier.height(24.dp))
                    Text("ĐỊA ĐIỂM", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Row(Modifier.padding(vertical = 8.dp)) {
                        Icon(Icons.Default.LocationOn, null, tint = PrimaryOrange)
                        Text(" ${item.address ?: "Chưa có địa chỉ"}")
                    }
                }
            }
        }

        if (showConfirmDialog) {
            AlertDialog(
                onDismissRequest = { showConfirmDialog = false },
                title = { Text("Nhận việc") },
                text = { Text("Bạn muốn nhận công việc này?") },
                confirmButton = { TextButton(onClick = { showConfirmDialog = false; viewModel.acceptJob() }) { Text("Đồng ý") } },
                dismissButton = { TextButton(onClick = { showConfirmDialog = false }) { Text("Hủy") } }
            )
        }
    }
}
