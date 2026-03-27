package com.localhelp.app.ui.screens.messages

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
    viewModel: ChatViewModel = hiltViewModel()
) {
    var messageText by remember { mutableStateOf("") }

    // Thu thập dữ liệu realtime từ Firebase
    val messages by viewModel.messages.collectAsState()
    val myUserId = viewModel.myUserId

    // Giải mã lại URL ảnh do Navigation bắt buộc phải mã hóa
    val decodedAvatar = remember(viewModel.partnerAvatar) {
        try {
            if (viewModel.partnerAvatar != "none") URLDecoder.decode(viewModel.partnerAvatar, "UTF-8") else ""
        } catch (e: Exception) {
            ""
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (decodedAvatar.isNotEmpty()) {
                            AsyncImage(
                                model = decodedAvatar,
                                contentDescription = null,
                                modifier = Modifier.size(40.dp).clip(CircleShape).background(Color(0xFFF0F0F0)),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Box(modifier = Modifier.size(40.dp).background(Color.LightGray, CircleShape))
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(viewModel.partnerName, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                            Text("Đang trực tuyến", color = Color(0xFF4CAF50), fontSize = 12.sp, fontWeight = FontWeight.Medium)
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) { Icon(Icons.AutoMirrored.Filled.ArrowBack, null) }
                },
                actions = {
                    TextButton(onClick = { /* TODO: Xử lý nút hoàn thành nếu cần */ }) {
                        Text("Hoàn thành", color = PrimaryOrange, fontWeight = FontWeight.Medium, fontSize = 14.sp)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        bottomBar = {
            ChatBottomInput(
                value = messageText,
                onValueChange = { messageText = it },
                onSend = {
                    viewModel.sendMessage(messageText)
                    messageText = ""
                }
            )
        },
        containerColor = Color.White
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(messages) { msg ->
                val isMe = msg.senderId == myUserId
                // Format lại timestamp Firebase thành giờ phút (vd: 09:34)
                val timeString = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(msg.timestamp))

                if (isMe) {
                    MyBubble(text = msg.text, time = timeString)
                } else {
                    OtherBubble(text = msg.text, time = timeString, avatarUrl = decodedAvatar)
                }
            }
        }
    }
}
