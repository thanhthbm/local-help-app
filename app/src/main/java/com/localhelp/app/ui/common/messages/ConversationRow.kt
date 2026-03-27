package com.localhelp.app.ui.common.messages

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.localhelp.app.model.response.ConversationResponse

@Composable
fun ConversationRow(conversation: ConversationResponse, onClick: () -> Unit) {
    val partner = conversation.partner
    Row (
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.Top
    ) {
        if (!partner.avatarUrl.isNullOrEmpty()) {
            AsyncImage(
                model = partner.avatarUrl, contentDescription = null,
                modifier = Modifier.size(56.dp).clip(CircleShape).background(Color(0xFFF0F0F0)),
                contentScale = ContentScale.Crop
            )
        } else {
            Box(modifier = Modifier.size(56.dp).clip(CircleShape).background(Color.LightGray))
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(partner.fullName ?: "Người dùng", fontWeight = FontWeight.Medium, fontSize = 17.sp, color = Color.Black)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Bấm để xem tin nhắn",
                color = Color.Gray,
                fontSize = 14.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}