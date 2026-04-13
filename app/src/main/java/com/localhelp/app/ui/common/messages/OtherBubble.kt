package com.localhelp.app.ui.common.messages

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage

@Composable
fun OtherBubble(text: String, time: String, avatarUrl: String, mediaUrls: List<String> = emptyList()) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Start, verticalAlignment = Alignment.Bottom) {
        if (avatarUrl.isNotEmpty()) {
            AsyncImage(
                model = avatarUrl, contentDescription = null,
                modifier = Modifier.size(32.dp).clip(CircleShape).background(Color(0xFFF0F0F0)), contentScale = ContentScale.Crop
            )
        } else {
            Box(modifier = Modifier.size(32.dp).background(Color.LightGray, CircleShape))
        }
        Spacer(modifier = Modifier.width(8.dp))
        Column(horizontalAlignment = Alignment.Start) {
            if (mediaUrls.isNotEmpty()) {
                mediaUrls.forEach { url ->
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.padding(bottom = 4.dp).widthIn(max = 240.dp)
                    ) {
                        AsyncImage(
                            model = url,
                            contentDescription = null,
                            modifier = Modifier.fillMaxWidth(),
                            contentScale = ContentScale.Crop
                        )
                    }
                }
            }
            if (text.isNotBlank()) {
                Surface(
                    color = Color.White,
                    shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp, bottomStart = 4.dp, bottomEnd = 16.dp),
                    border = BorderStroke(1.dp, Color(0xFFE0E0E0)),
                    modifier = Modifier.widthIn(max = 260.dp)
                ) {
                    Text(text = text, color = Color.Black, fontSize = 15.sp, modifier = Modifier.padding(12.dp))
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = time, color = Color.Gray, fontSize = 11.sp)
        }
    }
}