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
import androidx.compose.foundation.clickable
import com.localhelp.app.data.local.LocalUser
import com.localhelp.app.utils.FormatterUtils
import java.text.DecimalFormat

import androidx.compose.foundation.BorderStroke
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.automirrored.filled.TrendingFlat
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import com.localhelp.app.ui.screens.Screen
import java.net.URLEncoder

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
    val errorMessage by viewModel.errorMessage.collectAsState()
    val acceptStatus by viewModel.acceptStatus.collectAsState()
    
    val currentUser = LocalUser.current
    val isMyJob = job?.creatorId == currentUser?.id

    var showDeleteConfirm by remember { mutableStateOf(false) }

    val snackbarHostState = remember { SnackbarHostState() }

    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text("Xác nhận xóa") },
            text = { Text("Bạn có chắc chắn muốn xóa công việc này không? Thao tác này không thể hoàn tác.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteJob()
                        showDeleteConfirm = false
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = Color.Red)
                ) {
                    Text("Xóa")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = false }) {
                    Text("Hủy")
                }
            },
            containerColor = Color.White,
            titleContentColor = Color.Black,
            textContentColor = Color.Black
        )
    }

    LaunchedEffect(errorMessage) {
        errorMessage?.let {
            snackbarHostState.showSnackbar(it)
        }
    }

    LaunchedEffect(acceptStatus) {
        if (acceptStatus == true) {
            snackbarHostState.showSnackbar("Nhận việc thành công!")
        }
    }

    LaunchedEffect(Unit) {
        viewModel.navigationEvent.collect { event ->
            when (event) {
                is JobDetailViewModel.JobDetailNavEvent.NavigateToChat -> {
                    onMessageClick(event.conversationId, event.partnerName, event.partnerAvatar, event.partnerId)
                }
                is JobDetailViewModel.JobDetailNavEvent.NavigateToEditJob -> {
                    onEditJob(event.jobId)
                }
                is JobDetailViewModel.JobDetailNavEvent.NavigateToUserProfile -> {
                    onUserClick(event.userId)
                }
                is JobDetailViewModel.JobDetailNavEvent.JobDeleted -> {
                    onBackClick()
                }
                is JobDetailViewModel.JobDetailNavEvent.NavigateToSuccess -> {
                    onJobSuccessCallBack()
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        Text(
                            "Chi tiết công việc", 
                            fontSize = 18.sp, 
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(end = 48.dp) // Offset for back button to center title
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) { 
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back") 
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        bottomBar = {
            if (job != null && !isLoading) {
                Surface(
                    color = Color.White,
                    shadowElevation = 16.dp,
                    border = BorderStroke(1.dp, Color(0xFFEEEEEE))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                            .navigationBarsPadding(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (isMyJob) {
                            // My Job Actions: Edit and Remove
                            OutlinedButton(
                                onClick = { viewModel.onEditClick() },
                                modifier = Modifier.weight(1f).height(50.dp),
                                shape = RoundedCornerShape(12.dp),
                                border = BorderStroke(1.dp, Color(0xFFEEEEEE)),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Black)
                            ) {
                                Text("Chỉnh sửa", fontWeight = FontWeight.Bold)
                            }

                            Button(
                                onClick = { showDeleteConfirm = true },
                                enabled = job!!.status == JobStatus.OPEN || job!!.status == JobStatus.CANCELLED,
                                modifier = Modifier.weight(1f).height(50.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFFFEE2E2),
                                    contentColor = Color(0xFFEF4444),
                                    disabledContainerColor = Color(0xFFF3F4F6),
                                    disabledContentColor = Color.Gray
                                ),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text("Gỡ bỏ", fontWeight = FontWeight.Bold)
                            }
                        } else {
                            // Other's Job Actions: Chat and Accept
                            OutlinedButton(
                                onClick = {
                                    viewModel.onChatClick()
                                },
                                modifier = Modifier.weight(0.4f).height(50.dp),
                                shape = RoundedCornerShape(12.dp),
                                border = BorderStroke(1.dp, Color(0xFFF5F5F5)),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = PrimaryOrange)
                            ) {
                                Icon(Icons.AutoMirrored.Filled.Chat, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Chat ngay", fontWeight = FontWeight.Bold)
                            }

                            Button(
                                onClick = {
                                    showConfirmDialog = true
                                },
                                enabled = job!!.status == JobStatus.OPEN && !isLoading,
                                modifier = Modifier.weight(0.6f).height(50.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = PrimaryOrange,
                                    disabledContainerColor = Color.Gray
                                ),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text(
                                    text = if (job!!.status == JobStatus.OPEN) "Nhận việc" else "Đã có người nhận",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Icon(Icons.AutoMirrored.Filled.TrendingFlat, contentDescription = null)
                            }
                        }
                    }
                }
            }
        },
        containerColor = Color.White
    ) { paddingValues ->
        if (showConfirmDialog) {
            AlertDialog(
                onDismissRequest = { showConfirmDialog = false },
                title = {
                    Text(text = "Xác nhận nhận việc", fontWeight = FontWeight.Bold)
                },
                text = {
                    Text("Bạn có chắc chắn muốn nhận công việc này không?")
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            showConfirmDialog = false
                            viewModel.acceptJob()
                        }
                    ) {
                        Text("Đồng ý", color = PrimaryOrange)
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = { showConfirmDialog = false }
                    ) {
                        Text("Hủy", color = Color.Gray)
                    }
                }
            )
        }
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
                    .background(BackgroundGray)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    // Title and Price Pill
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Text(
                            text = currentJob.title ?: "Không có tiêu đề",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.weight(1f),
                            lineHeight = 32.sp
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Surface(
                            color = Color(0xFFFFF1F0),
                            shape = RoundedCornerShape(20.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text("Thù lao", fontSize = 10.sp, color = PrimaryOrange)
                                Text(
                                    FormatterUtils.formatPrice(currentJob.price),
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = PrimaryOrange
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Time and Category
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.AccessTime, null, tint = TextGray, modifier = Modifier.size(14.dp))
                        Text(" ${FormatterUtils.formatDateTime(currentJob.createdAt)}", color = TextGray, fontSize = 13.sp)
                        Text("  •  ", color = TextGray)
                        Icon(Icons.Default.Pets, null, tint = Color(0xFF8B4513), modifier = Modifier.size(14.dp))
                        Text(" ${currentJob.categoryName ?: "Việc nhẹ"}", color = TextGray, fontSize = 13.sp)
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Creator Info Box
                    Surface(
                        modifier = Modifier.fillMaxWidth().clickable { viewModel.onUserClick() },
                        color = Color.White,
                        shape = RoundedCornerShape(16.dp),
                        border = BorderStroke(1.dp, Color(0xFFF0F0F0))
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            AsyncImage(
                                model = if (currentJob.creatorAvatar.isNullOrEmpty()) "https://via.placeholder.com/150" else currentJob.creatorAvatar,
                                contentDescription = "Avatar",
                                modifier = Modifier.size(48.dp).clip(CircleShape),
                                contentScale = ContentScale.Crop
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Người đăng", fontSize = 11.sp, color = TextGray)
                                Text(
                                    text = currentJob.creatorName ?: "Người ẩn danh",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
                                )
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Surface(
                                        color = Color(0xFFE6F7ED),
                                        shape = RoundedCornerShape(4.dp)
                                    ) {
                                        Text(
                                            "● Uy tín",
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                            fontSize = 10.sp,
                                            color = Color(0xFF27AE60),
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Icon(Icons.Default.Star, null, tint = Color(0xFFFFC107), modifier = Modifier.size(14.dp))
                                    Text(
                                        " ${currentJob.creatorRating ?: "0.0"}",
                                        fontSize = 12.sp,
                                        color = TextGray
                                    )
                                }
                            }
                            Icon(Icons.Default.ChevronRight, null, tint = Color.LightGray)
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Description Section
                    Text(
                        "NỘI DUNG CÔNG VIỆC",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF8B5E51)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = Color.White,
                        shape = RoundedCornerShape(16.dp),
                        border = BorderStroke(1.dp, Color(0xFFF0F0F0))
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = currentJob.description ?: "Không có mô tả chi tiết.",
                                fontSize = 15.sp,
                                color = Color(0xFF4A4A4A),
                                lineHeight = 24.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Location Section
                    Text(
                        "Địa điểm",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    // Simple address display instead of map as requested
                    Row(verticalAlignment = Alignment.Top) {
                        Icon(Icons.Default.LocationOn, null, tint = PrimaryOrange, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = currentJob.address ?: "Chưa cập nhật địa chỉ",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Info, null, tint = TextGray, modifier = Modifier.size(12.dp))
                                Text(
                                    " Địa chỉ chính xác sẽ hiển thị sau khi nhận việc.",
                                    fontSize = 12.sp,
                                    color = TextGray
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(40.dp))
                }
            }
        }
    }
}

@Composable
fun TagPill(icon: ImageVector, text: String) {
    Surface(
        color = Color(0xFFF5F5F5),
        shape = RoundedCornerShape(20.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, null, modifier = Modifier.size(14.dp), tint = Color.Gray)
            Spacer(modifier = Modifier.width(4.dp))
            Text(text, fontSize = 12.sp, color = Color.Gray)
        }
    }
}
