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
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.hilt.navigation.compose.hiltViewModel
import java.text.NumberFormat
import java.util.Locale
import java.time.format.DateTimeFormatter
import java.time.ZoneId
import com.localhelp.app.model.response.JobResponse
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

/**
 * Màn chi tiết giao dịch thu/chi.
 *
 * transactionId chính là jobId; isEarning quyết định cách hiển thị vai trò
 * đối tác và dấu +/- của số tiền.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionDetailScreen(
    transactionId: Int = 3,
    isEarning: Boolean = false,
    viewModel: TransactionDetailViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit = {},
    onViewProfile: (Long) -> Unit = {},
    onViewMap: (Double, Double) -> Unit = { _, _ -> }
) {
    LaunchedEffect(transactionId) {
        viewModel.fetchTransactionDetail(transactionId.toLong())
    }

    val uiState by viewModel.uiState.collectAsState()
    
    val jobData = uiState.data?.jobInfo

    val accentColor = if (isEarning) TxGreen else TxOrange
    
    val jobName = jobData?.title ?: (if (isEarning) "Dịch vụ" else "Dịch vụ thực hiện")
    
    // Convert Job category to Icon
    val categoryNameLower = jobData?.categoryName?.lowercase() ?: ""
    val jobIcon = when {
        categoryNameLower.contains("dọn") -> Icons.Filled.Brush
        categoryNameLower.contains("giao") || categoryNameLower.contains("thuê") -> Icons.Filled.LocalShipping
        categoryNameLower.contains("chó") || categoryNameLower.contains("chăm") -> Icons.Filled.Pets
        else -> Icons.Filled.Info
    }
    
    val jobIconColor = if (isEarning) Color(0xFF4A90D9) else Color(0xFFE06080)
    val jobIconBg = if (isEarning) Color(0xFFE0F0FF) else Color(0xFFFFE8F0)
    
    val fmt = NumberFormat.getNumberInstance(Locale("vi", "VN"))
    val formattedPrice = "${fmt.format(jobData?.price ?: 0.0)} đ"
    val amount = if (isEarning) "+$formattedPrice" else "-$formattedPrice"
    val amountColor = if (isEarning) TxGreen else Color(0xFF1A1A1A)
    val paymentStatus = if (isEarning) "Đã nhận " else "Đã thanh toán "
    val partnerRole = if (isEarning) "NGƯỜI THUÊ" else "NGƯỜI LÀM"

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
        if (uiState.isLoading) {
            Box(modifier = Modifier.fillMaxSize().padding(innerPadding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = accentColor)
            }
        } else if (uiState.error != null) {
            Box(modifier = Modifier.fillMaxSize().padding(innerPadding), contentAlignment = Alignment.Center) {
                Text(uiState.error ?: "", color = Color.Red)
            }
        } else if (jobData != null) {
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
                            Text("Mã giao dịch: #TX$transactionId", color = TxGray, fontSize = 12.sp)
                        }
                        Spacer(Modifier.height(2.dp))
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            val formatterDate = DateTimeFormatter.ofPattern("dd/MM/yyyy")
                            val formatterTime = DateTimeFormatter.ofPattern("HH:mm")
                            val dateStr = jobData.createdAt?.format(formatterDate) ?: "--/--/----"
                            val timeStr = jobData.createdAt?.format(formatterTime) ?: "--:--"
                            InfoChip(Icons.Filled.CalendarToday, dateStr)
                            InfoChip(Icons.Filled.Schedule, timeStr)
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
                        val partnerName = if (isEarning) jobData.creatorName else jobData.helperName
                        val partnerRating = if (isEarning) jobData.creatorRating else jobData.helperRating
                        val partnerId = if (isEarning) jobData.creatorId else jobData.helperId
                        Text(partnerName ?: "Người dùng",
                            fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Filled.Star, null,
                                tint = Color(0xFFF0A040), modifier = Modifier.size(14.dp))
                            Spacer(Modifier.width(2.dp))
                            Text("${partnerRating ?: "5.0"}", fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                        }
                    }
                    val partnerId = if (isEarning) jobData.creatorId else jobData.helperId
                    OutlinedButton(
                        onClick = { partnerId?.let { onViewProfile(it) } },
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
                        Text(jobData.address ?: "Vị trí không xác định", color = TxGray, fontSize = 13.sp, lineHeight = 18.sp)
                    }
                }
                Spacer(Modifier.height(12.dp))
                // Map placeholder
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(130.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFDEE8D5))
                        .clickable {
                            val lat = jobData?.latitude
                            val lng = jobData?.longitude
                            if (lat != null && lng != null) {
                                onViewMap(lat, lng)
                            }
                        },
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
                    jobData.description ?: "Không có mô tả thêm.",
                    fontSize = 13.sp,
                    color = Color(0xFF333333),
                    lineHeight = 19.sp
                )
                Spacer(Modifier.height(12.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    TagChip(jobData.categoryName ?: "Dịch vụ")
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
}

// ─── Sub-composables ───────────────────────────────────────────────────────────

/** Card dùng lại cho từng khối thông tin trong chi tiết giao dịch. */
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

/** Chip nhỏ hiển thị ngày/giờ hoặc thông tin ngắn. */
@Composable
private fun InfoChip(icon: ImageVector, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, null, modifier = Modifier.size(13.dp), tint = TxGray)
        Spacer(Modifier.width(3.dp))
        Text(label, color = TxGray, fontSize = 12.sp)
    }
}

/** Tag danh mục công việc trong màn chi tiết giao dịch. */
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
