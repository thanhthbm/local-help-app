package com.localhelp.app.ui.screens.messages


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.localhelp.app.ui.common.messages.ConversationRow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MessagesScreen(
    onNavigateToChat: (String, String, String?) -> Unit,
    viewModel: MessagesViewModel = hiltViewModel()
) {
    var searchQuery by remember { mutableStateOf("") }
    val conversations by viewModel.conversations.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    val filteredConversations = if (searchQuery.isBlank()) {
        conversations
    } else {
        conversations.filter { it.partner.fullName?.contains(searchQuery, ignoreCase = true) == true }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center){
                        Text("Tin nhắn", fontWeight = FontWeight.Bold, fontSize = 20.sp)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = Color.White
    ) { paddingValues ->
        Column(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .shadow(elevation = 6.dp, shape = RoundedCornerShape(24.dp))
                    .background(Color.White, RoundedCornerShape(24.dp))
            ){
                TextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Tìm kiếm tin nhắn", color = Color.Gray, fontSize = 15.sp) },
                    leadingIcon = { Icon(Icons.Default.Search, null, tint = Color(0xFFED7D68)) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent, unfocusedContainerColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent, unfocusedIndicatorColor = Color.Transparent
                    ),
                    singleLine = true
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Box(modifier = Modifier.fillMaxSize()) {
                if (isLoading) {
                    CircularProgressIndicator(color = Color(0xFFED7D68), modifier = Modifier.align(Alignment.Center))
                } else if (filteredConversations.isEmpty()) {
                    Text("Chưa có tin nhắn nào", color = Color.Gray, modifier = Modifier.align(Alignment.Center))
                } else {
                    LazyColumn(contentPadding = PaddingValues(bottom = 80.dp)) {
                        items(filteredConversations) { conv ->
                            ConversationRow(
                                conversation = conv,
                                onClick = {
                                    // Truyền thông tin sang màn hình chi tiết
                                    onNavigateToChat(
                                        conv.id,
                                        conv.partner.fullName ?: "Người dùng",
                                        conv.partner.avatarUrl ?: "none"
                                    )
                                }
                            )
                        }
                    }
                }
            }
        }

    }
}
