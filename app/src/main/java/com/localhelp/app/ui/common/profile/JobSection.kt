package com.localhelp.app.ui.common.profile

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.localhelp.app.ui.screens.profile.GrayBackground
import com.localhelp.app.ui.screens.profile.GrayText
import com.localhelp.app.ui.screens.profile.GreenLight
import com.localhelp.app.ui.screens.profile.GreenText
import com.localhelp.app.ui.screens.profile.OrangeLight
import com.localhelp.app.ui.screens.profile.OrangePrimary

@Composable
fun JobsSection() {
    Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = "Việc đã hoàn thành",
            color = OrangePrimary,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        HorizontalDivider(color = OrangePrimary, thickness = 2.dp, modifier = Modifier.width(160.dp))
        HorizontalDivider(color = Color.LightGray, thickness = 1.dp)

        Spacer(modifier = Modifier.height(16.dp))

        // Job tĩnh làm mẫu
        JobCard("Sửa vòi nước", "Hôm qua • 50.000đ", "Đã xong", GrayBackground, GrayText, actionText = "Xem chi tiết")
        Spacer(modifier = Modifier.height(12.dp))
        JobCard("Mua giúp thuốc", "2 giờ trước • Thương lượng", "Đang tìm", OrangeLight, OrangePrimary, actionText = "Quản lý", actionIsButton = true)
        Spacer(modifier = Modifier.height(12.dp))
        JobCard("Dắt chó đi", "3 ngày trước • 30.000đ", "Hoàn thành", GreenLight, GreenText, actionText = "Đánh giá lại")
    }
}