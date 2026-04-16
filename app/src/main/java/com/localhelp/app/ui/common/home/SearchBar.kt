package com.localhelp.app.ui.common.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun SearchBar(
    modifier: Modifier = Modifier,
    onSearchClick: () -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onSearchClick() }
            .background(Color.White, RoundedCornerShape(50.dp))
            .border(1.dp, Color(0xFFEEEEEE), RoundedCornerShape(50.dp))
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(Icons.Default.Search, contentDescription = null, tint = Color.Gray)
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = "Bạn muốn làm gì hôm nay",
            color = Color.Gray,
            modifier = Modifier.weight(1f)
        )
        Box(
            modifier = Modifier
                .background(Color(0xFFED7D68), RoundedCornerShape(8.dp))
                .padding(6.dp)
        ) {
            Icon(Icons.Default.FilterList, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
        }
    }
}
