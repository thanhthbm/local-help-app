package com.localhelp.app.ui.screens.messages

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.localhelp.app.ui.common.messages.ChatBottomInput
import com.localhelp.app.ui.common.messages.MyBubble
import com.localhelp.app.ui.common.messages.OtherBubble
import java.net.URLDecoder
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

val PrimaryOrange = Color(0xFFED7D68)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    onBackClick: () -> Unit,
    onNavigateToProfile: (Long) -> Unit = {},
    viewModel: ChatViewModel = hiltViewModel()
) {
    var messageText by remember { mutableStateOf("") }
    var selectedMediaUris by remember { mutableStateOf<List<android.net.Uri>>(emptyList()) }
    var previewImageUrl by remember { mutableStateOf<String?>(null) }

    val context = LocalContext.current

    val partnerId = viewModel.partnerId

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents()
    ) { uris ->
        selectedMediaUris = selectedMediaUris + uris
    }

    // Thu thập dữ liệu realtime từ Firebase
    val messages by viewModel.messages.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val isSending by viewModel.isSending.collectAsState()
    val error by viewModel.error.collectAsState()
    val myUserId = viewModel.myUserId

    // Giải mã lại URL ảnh do Navigation bắt buộc phải mã hóa
    val decodedAvatar = remember(viewModel.partnerAvatar) {
        try {
            if (viewModel.partnerAvatar != "none" && viewModel.partnerAvatar.isNotEmpty()) 
                URLDecoder.decode(viewModel.partnerAvatar, "UTF-8") 
            else ""
        } catch (e: Exception) {
            ""
        }
    }

    val listState = rememberLazyListState()

    // Tự động cuộn xuống khi có tin nhắn mới
    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable { onNavigateToProfile(partnerId) }
                    ) {
                        if (!decodedAvatar.isNullOrBlank() && decodedAvatar != "none") {
                            AsyncImage(
                                model = decodedAvatar,
                                contentDescription = null,
                                modifier = Modifier.size(40.dp).clip(CircleShape).background(Color(0xFFF0F0F0)),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Box(
                                modifier = Modifier.size(40.dp).clip(CircleShape).background(Color.LightGray),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Person, contentDescription = null, tint = Color.White, modifier = Modifier.size(24.dp))
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(viewModel.partnerName, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) { Icon(Icons.AutoMirrored.Filled.ArrowBack, null) }
                },
                actions = {
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        bottomBar = {
            Column {
                if (selectedMediaUris.isNotEmpty()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFF5F5F5))
                            .padding(8.dp)
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        selectedMediaUris.forEach { uri ->
                            Box(modifier = Modifier.size(80.dp)) {
                                AsyncImage(
                                    model = uri,
                                    contentDescription = null,
                                    modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(8.dp)),
                                    contentScale = ContentScale.Crop
                                )
                                IconButton(
                                    onClick = { selectedMediaUris = selectedMediaUris - uri },
                                    modifier = Modifier.align(Alignment.TopEnd).size(24.dp).background(Color.Black.copy(alpha = 0.5f), CircleShape)
                                ) {
                                    Icon(Icons.Default.Close, null, tint = Color.White, modifier = Modifier.size(16.dp))
                                }
                            }
                        }
                    }
                }
                ChatBottomInput(
                    value = messageText,
                    onValueChange = { messageText = it },
                    onSend = {
                        viewModel.sendMessage(messageText, selectedMediaUris)
                        messageText = ""
                        selectedMediaUris = emptyList()
                    },
                    onAddMedia = { launcher.launch("image/*") }
                )
            }
        },
        containerColor = Color.White
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center), color = PrimaryOrange)
            } else if (error != null) {
                Column(
                    modifier = Modifier.fillMaxSize().padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text("Oops!", fontWeight = FontWeight.Bold, fontSize = 24.sp, color = Color.Gray)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(error ?: "Có lỗi xảy ra", color = Color.Gray, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                }
            } else if (messages.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Chưa có tin nhắn nào. Hãy bắt đầu cuộc trò chuyện!", color = Color.Gray, fontSize = 14.sp)
                }
            } else {
                LazyColumn(
                    state = listState,
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(messages) { msg ->
                        val isMe = msg.senderId == myUserId
                        val timeString = try {
                            SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(msg.timestamp))
                        } catch (e: Exception) {
                            "--:--"
                        }

                        if (isMe) {
                            MyBubble(
                                text = msg.text,
                                time = timeString,
                                mediaUrls = msg.mediaUrls,
                                onImageClick = { previewImageUrl = it }
                            )
                        } else {
                            OtherBubble(
                                text = msg.text,
                                time = timeString,
                                avatarUrl = decodedAvatar,
                                mediaUrls = msg.mediaUrls,
                                onImageClick = { previewImageUrl = it }
                            )
                        }
                    }
                }
            }

            if (isSending) {
                Box(
                    modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = PrimaryOrange)
                }
            }

            if (previewImageUrl != null) {
                Dialog(
                    onDismissRequest = { previewImageUrl = null },
                    properties = DialogProperties(usePlatformDefaultWidth = false)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black)
                    ) {
                        AsyncImage(
                            model = previewImageUrl,
                            contentDescription = null,
                            modifier = Modifier
                                .fillMaxSize()
                                .clickable { previewImageUrl = null },
                            contentScale = ContentScale.Fit
                        )

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                                .align(Alignment.TopEnd),
                            horizontalArrangement = Arrangement.End
                        ) {
                            IconButton(
                                onClick = {
                                    downloadImage(context, previewImageUrl!!)
                                },
                                modifier = Modifier.background(Color.Black.copy(alpha = 0.5f), CircleShape)
                            ) {
                                Icon(Icons.Default.Download, contentDescription = "Download", tint = Color.White)
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            IconButton(
                                onClick = { previewImageUrl = null },
                                modifier = Modifier.background(Color.Black.copy(alpha = 0.5f), CircleShape)
                            ) {
                                Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                            }
                        }
                    }
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
