package com.localhelp.app.ui.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage

// Mã màu tĩnh (Sau này bạn có thể chuyển vào file Theme/Color.kt)
val OrangePrimary = Color(0xFFF06A50)
val OrangeLight = Color(0xFFFFF0ED)
val GreenLight = Color(0xFFE8F8F0)
val GreenText = Color(0xFF2E9B5B)
val GrayText = Color(0xFF888888)
val GrayBackground = Color(0xFFF7F7F7)

@Composable
fun ProfileScreen(
    modifier: Modifier = Modifier,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val scrollState = rememberScrollState()
    val user by viewModel.currentUser.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(horizontal = 16.dp)
            .verticalScroll(scrollState)
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // --- TIÊU ĐỀ & NÚT ĐĂNG XUẤT ---
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Hồ sơ cá nhân", fontSize = 24.sp, fontWeight = FontWeight.Bold)
            IconButton(onClick = { viewModel.logout() }) {
                Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = "Đăng xuất", tint = Color.Red)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // --- THẺ THÔNG TIN CÁ NHÂN ---
        ProfileCard(
            fullName = user?.fullName ?: "Người dùng",
            bio = user?.bio ?: "Chưa có thông tin giới thiệu",
            avatarUrl = user?.avatarUrl
        )

        Spacer(modifier = Modifier.height(16.dp))

        // --- THỐNG KÊ ---
        val ratePercent = ((user?.responseRate ?: 0.0) * 100).toInt()
        StatsSection(
            completedJobs = user?.completedJobs?.toString() ?: "0",
            responseRate = "$ratePercent%"
        )

        Spacer(modifier = Modifier.height(16.dp))
        FinanceButton()

        Spacer(modifier = Modifier.height(24.dp))

        // --- PHẦN ĐÁNH GIÁ ---
        ReviewsSection(
            averageRating = user?.averageRating ?: 0.0,
            totalReviews = user?.totalReviews ?: 0
        )

        Spacer(modifier = Modifier.height(24.dp))

        // --- PHẦN CÔNG VIỆC GẦN ĐÂY (Tạm thời tĩnh) ---
        JobsSection()

        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
fun ProfileCard(fullName: String, bio: String, avatarUrl: String?) {
    OutlinedCard(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.outlinedCardColors(containerColor = Color.White),
        border = CardDefaults.outlinedCardBorder().copy(width = 1.dp, brush = Brush.linearGradient(listOf(OrangePrimary, OrangeLight)))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Hiển thị Avatar (Dùng Coil nếu có URL, không thì dùng Icon mặc định)
            if (!avatarUrl.isNullOrEmpty()) {
                AsyncImage(
                    model = avatarUrl,
                    contentDescription = "Avatar",
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .background(Color.LightGray),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Person, contentDescription = null, tint = Color.White, modifier = Modifier.size(50.dp))
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Tên và Xác thực
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(fullName, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.width(4.dp))
                Icon(Icons.Default.Verified, contentDescription = "Verified", tint = Color(0xFF1D9BF0), modifier = Modifier.size(20.dp))
            }

            Text(bio, color = GrayText, fontSize = 14.sp, modifier = Modifier.padding(top = 4.dp))

            Spacer(modifier = Modifier.height(16.dp))

            // Badges
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                BadgeItem("Hàng xóm vàng", Icons.Default.ThumbUp, OrangeLight, OrangePrimary)
                BadgeItem("Đáng tin cậy", Icons.Default.Shield, GreenLight, GreenText)
            }
        }
    }
}

@Composable
fun BadgeItem(text: String, icon: ImageVector, bgColor: Color, contentColor: Color) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .background(bgColor, RoundedCornerShape(16.dp))
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Text(text, color = contentColor, fontSize = 12.sp, fontWeight = FontWeight.Medium)
        Spacer(modifier = Modifier.width(4.dp))
        Icon(icon, contentDescription = null, tint = contentColor, modifier = Modifier.size(14.dp))
    }
}

@Composable
fun StatsSection(completedJobs: String, responseRate: String) {
    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        StatCard(modifier = Modifier.weight(1f), icon = Icons.Outlined.CheckCircle, iconTint = OrangePrimary, value = completedJobs, label = "Việc đã xong")
        StatCard(modifier = Modifier.weight(1f), icon = Icons.Outlined.ChatBubbleOutline, iconTint = Color(0xFF4A90E2), value = responseRate, label = "Tỉ lệ phản hồi")
    }
}

@Composable
fun StatCard(modifier: Modifier = Modifier, icon: ImageVector, iconTint: Color, value: String, label: String) {
    OutlinedCard(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.outlinedCardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(28.dp))
            Spacer(modifier = Modifier.height(8.dp))
            Text(value, fontSize = 24.sp, fontWeight = FontWeight.Bold)
            Text(label, fontSize = 13.sp, color = GrayText)
        }
    }
}

@Composable
fun FinanceButton() {
    OutlinedCard(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.outlinedCardColors(containerColor = Color.White),
        border = CardDefaults.outlinedCardBorder().copy(width = 1.dp, brush = Brush.linearGradient(listOf(OrangePrimary, OrangeLight)))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(OrangeLight, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.AccountBalanceWallet, contentDescription = null, tint = OrangePrimary)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text("Thống kê thu chi", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text("Xem lịch sử giao dịch", color = GrayText, fontSize = 13.sp)
            }
            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = GrayText)
        }
    }
}

@Composable
fun ReviewsSection(averageRating: Double, totalReviews: Int) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Đánh giá & Nhận xét", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Text("Xem chi tiết >", color = OrangePrimary, fontSize = 14.sp)
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(averageRating.toString(), fontSize = 32.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Row {
                    val fullStars = averageRating.toInt()
                    val hasHalfStar = averageRating - fullStars >= 0.5

                    repeat(fullStars) { Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFFFFB400), modifier = Modifier.size(16.dp)) }
                    if (hasHalfStar) Icon(Icons.Default.StarHalf, contentDescription = null, tint = Color(0xFFFFB400), modifier = Modifier.size(16.dp))
                }
                Text("$totalReviews nhận xét", color = GrayText, fontSize = 13.sp)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Review tĩnh làm mẫu
        ReviewItem("Minh Tuấn", "Cô Lan rất nhiệt tình, sửa vòi nước xong còn dọn dẹp sạch sẽ giúp mình nữa. Rất cảm ơn cô!")
        Spacer(modifier = Modifier.height(12.dp))
        ReviewItem("Chị Hạnh", "Giao thuốc rất nhanh, đúng loại mình cần. Người hàng xóm tuyệt vời.")
    }
}

@Composable
fun ReviewItem(name: String, comment: String) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = GrayBackground),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(name, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Row {
                    repeat(5) { Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFFFFB400), modifier = Modifier.size(14.dp)) }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text("\"$comment\"", fontSize = 14.sp, color = Color.DarkGray)
        }
    }
}

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

@Composable
fun JobCard(title: String, subtitle: String, status: String, statusBg: Color, statusColor: Color, actionText: String, actionIsButton: Boolean = false) {
    OutlinedCard(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.outlinedCardColors(containerColor = Color.White)
    ) {
        Row(modifier = Modifier.padding(12.dp).fillMaxWidth()) {
            Box(
                modifier = Modifier.size(80.dp).clip(RoundedCornerShape(8.dp)).background(Color.LightGray)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Top) {
                    Text(title, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Text(
                        text = status,
                        fontSize = 11.sp,
                        color = statusColor,
                        modifier = Modifier.background(statusBg, RoundedCornerShape(12.dp)).padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Schedule, contentDescription = null, tint = GrayText, modifier = Modifier.size(12.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(subtitle, color = GrayText, fontSize = 13.sp)
                }

                Spacer(modifier = Modifier.height(12.dp))

                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterEnd) {
                    if (actionIsButton) {
                        Button(
                            onClick = { },
                            colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp),
                            modifier = Modifier.height(32.dp)
                        ) {
                            Text(actionText, fontSize = 13.sp)
                        }
                    } else {
                        Text(actionText, color = if (actionText == "Xem chi tiết") OrangePrimary else GrayText, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                    }
                }
            }
        }
    }
}