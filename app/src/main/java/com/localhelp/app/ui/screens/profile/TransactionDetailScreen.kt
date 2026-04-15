package com.localhelp.app.ui.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val TxGreen = Color(0xFF2E9B5B)
private val TxGreenBg = Color(0xFFE8F8F0)
private val TxOrange = Color(0xFFF06A50)
private val TxGray = Color(0xFF888888)
private val TxGrayBg = Color(0xFFF7F7F7)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionDetailScreen(
    transactionId: Int = 3,
    onNavigateBack: () -> Unit = {},
    onViewProfile: () -> Unit = {}
) {
    val isEarning = transactionId > 100
    val accentColor = if (isEarning) TxGreen else TxOrange
    
    val jobName = if (isEarning) "Giao hàng siêu tốc" else "Dắt chó đi dạo"
    val jobIcon = if (isEarning) Icons.Filled.LocalShipping else Icons.Filled.Pets
    val jobIconColor = if (isEarning) Color(0xFF4A90D9) else Color(0xFFE06080)
    val jobIconBg = if (isEarning) Color(0xFFE0F0FF) else Color(0xFFFFE8F0)
    
    val amount = if (isEarning) "+180.000 đ" else "180.000 đ"
    val amountColor = if (isEarning) TxGreen else Color(0xFF1A1A1A)
    val paymentStatus = if (isEarning) "Đã nhận qua Ví điện tử" else "Đã thanh toán qua Ví điện tử"
    val partnerRole = if (isEarning) "NGƯỜI THUÊ" else "NGƯỜI THỰC HIỆN"

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (isEarning) "Chi tiết thu nhập" else "Chi tiết chi tiêu", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
                    }
                },
                actions = {
                    IconButton(onClick = {}) {
                        Icon(Icons.Filled.IosShare, null, tint = TxGray)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = TxGrayBg
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
        ) {
            // ── Status + amount header ────────────────────────────────────────
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(horizontal = 20.dp, vertical = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Surface(
                    shape = RoundedCornerShape(50),
                    color = TxGreenBg
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(Icons.Filled.CheckCircle, null,
                            tint = TxGreen, modifier = Modifier.size(16.dp))
                        Text("Hoàn thành", color = TxGreen,
                            fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                }
                Spacer(Modifier.height(12.dp))
                Text(amount, fontSize = 34.sp,
                    fontWeight = FontWeight.ExtraBold, color = amountColor)
                Spacer(Modifier.height(4.dp))
                Text(paymentStatus, color = TxGray, fontSize = 13.sp)
            }

            Spacer(Modifier.height(10.dp))

            // ── Job info card ─────────────────────────────────────────────────
            SectionCard {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .background(jobIconBg, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(jobIcon, null,
                            tint = jobIconColor, modifier = Modifier.size(26.dp))
                    }
                    Spacer(Modifier.width(14.dp))
                    Column {
                        Text(jobName, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Spacer(Modifier.height(4.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Filled.Tag, null,
                                modifier = Modifier.size(14.dp), tint = TxGray)
                            Spacer(Modifier.width(4.dp))
                            Text("Mã giao dịch: #TX882910", color = TxGray, fontSize = 12.sp)
                        }
                        Spacer(Modifier.height(2.dp))
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            InfoChip(Icons.Filled.CalendarToday, "14/10/2023")
                            InfoChip(Icons.Filled.Schedule, "07:30 - 08:30")
                        }
                    }
                }
            }

            Spacer(Modifier.height(10.dp))

            // ── Performer / Client ────────────────────────────────────────────
            SectionCard {
                Text(partnerRole,
                    fontWeight = FontWeight.Bold, fontSize = 11.sp,
                    color = TxGray, letterSpacing = 0.8.sp)
                Spacer(Modifier.height(12.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF4A90D9)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Filled.Person, null,
                            tint = Color.White, modifier = Modifier.size(28.dp))
                    }
                    Spacer(Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Nguyễn Văn A",
                            fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Filled.Star, null,
                                tint = Color(0xFFF0A040), modifier = Modifier.size(14.dp))
                            Spacer(Modifier.width(2.dp))
                            Text("4.8", fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                            Text(" (120 đánh giá)", color = TxGray, fontSize = 12.sp)
                        }
                    }
                    OutlinedButton(
                        onClick = onViewProfile,
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = accentColor),
                        border = ButtonDefaults.outlinedButtonBorder.copy(
                            width = 1.dp
                        )
                    ) {
                        Text("Xem hồ sơ", fontSize = 12.sp)
                    }
                }
            }

            Spacer(Modifier.height(10.dp))

            // ── Location ─────────────────────────────────────────────────────
            SectionCard {
                Text("ĐỊA ĐIỂM THỰC HIỆN",
                    fontWeight = FontWeight.Bold, fontSize = 11.sp,
                    color = TxGray, letterSpacing = 0.8.sp)
                Spacer(Modifier.height(12.dp))
                Row(verticalAlignment = Alignment.Top) {
                    Icon(Icons.Filled.LocationOn, null,
                        tint = accentColor, modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(8.dp))
                    Column {
                        Text("Công viên Cầu Giấy", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                        Text("Đường Thành Thái, Dịch Vọng, Cầu Giấy, Hà Nội",
                            color = TxGray, fontSize = 12.sp, lineHeight = 16.sp)
                    }
                }
                Spacer(Modifier.height(12.dp))
                // Map placeholder
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(130.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFDEE8D5)),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Filled.Map, null,
                            tint = Color(0xFF6A8F6A), modifier = Modifier.size(32.dp))
                        Spacer(Modifier.height(6.dp))
                        Text("Nhấn để xem bản đồ", color = Color(0xFF6A8F6A),
                            fontSize = 13.sp, fontWeight = FontWeight.Medium)
                    }
                }
            }

            Spacer(Modifier.height(10.dp))

            // ── Job description ───────────────────────────────────────────────
            SectionCard {
                Text("MÔ TẢ CÔNG VIỆC",
                    fontWeight = FontWeight.Bold, fontSize = 11.sp,
                    color = TxGray, letterSpacing = 0.8.sp)
                Spacer(Modifier.height(10.dp))
                Text(
                    if (isEarning) "Nhờ bạn giao nhanh gói quà nhỏ gọn đến địa chỉ này trước 9rồi nhé. Cẩn thận tránh va đập."
                    else "Cần người dắt cho Corgi của mình đi dạo quanh công viên khoảng 1 tiếng. Bé ngoan, đã tiêm phòng đầy đủ. Yêu cầu người yêu động vật, có kinh nghiệm dắt chó.",
                    fontSize = 13.sp,
                    color = Color(0xFF333333),
                    lineHeight = 19.sp
                )
                Spacer(Modifier.height(12.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    TagChip(if (isEarning) "Giao cẩn thận" else "Cẩn thận trọng")
                    TagChip(if (isEarning) "Đúng giờ" else "Mang nước uống")
                }
            }

            Spacer(Modifier.height(8.dp))

            // ── Report button ─────────────────────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "Báo cáo sự cố",
                    color = TxGray,
                    fontSize = 13.sp,
                    modifier = Modifier.clickable {}
                )
            }

            Spacer(Modifier.height(24.dp))
        }
    }
}

// ─── Sub-composables ───────────────────────────────────────────────────────────

@Composable
private fun SectionCard(content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp), content = content)
    }
}

@Composable
private fun InfoChip(icon: ImageVector, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, null, modifier = Modifier.size(13.dp), tint = TxGray)
        Spacer(Modifier.width(3.dp))
        Text(label, color = TxGray, fontSize = 12.sp)
    }
}

@Composable
private fun TagChip(label: String) {
    Surface(
        shape = RoundedCornerShape(50),
        color = Color(0xFFF0F0F0)
    ) {
        Text(
            label,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            fontSize = 12.sp,
            color = Color(0xFF555555)
        )
    }
}
