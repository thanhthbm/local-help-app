package com.localhelp.app.ui.screens.profile

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.TrendingDown
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.Brush
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.NumberFormat
import java.util.Locale

// ─── Color tokens ──────────────────────────────────────────────────────────────
private val Orange = Color(0xFFF06A50)
private val Green = Color(0xFF2E9B5B)
private val GreenBg = Color(0xFFE8F8F0)
private val Gray = Color(0xFF888888)
private val GrayBg = Color(0xFFF7F7F7)
private val Blue = Color(0xFF4A90D9)
private val Purple = Color(0xFF9B59B6)

// ─── Mock data ─────────────────────────────────────────────────────────────────
data class SpendingCategory(
    val id: Int,
    val name: String,
    val icon: ImageVector,
    val iconBg: Color,
    val iconTint: Color,
    val amount: Long,
    val percent: Int,
    val barColor: Color
)

data class TransactionItem(
    val id: Int,
    val name: String,
    val icon: ImageVector,
    val iconBg: Color,
    val iconTint: Color,
    val dateTime: String,
    val amount: Long,
    val status: String,           // "Hoàn tất" | "Đang chờ"
    val statusColor: Color,
    val statusBg: Color
)

// --- Chi tiêu ---
private val mockCategories = listOf(
    SpendingCategory(1, "Dọn dẹp nhà cửa", Icons.Filled.Brush, Color(0xFFFFF0E0), Color(0xFFF0A040), 2_100_000, 50, Orange),
    SpendingCategory(2, "Giao hàng", Icons.Filled.LocalShipping, Color(0xFFE0F0FF), Blue, 1_260_000, 30, Blue),
    SpendingCategory(3, "Chăm sóc thú cưng", Icons.Filled.Pets, Color(0xFFFFE8F0), Color(0xFFE06080), 840_000, 20, Purple)
)

private val mockTransactions = listOf(
    TransactionItem(1, "Dọn nhà theo giờ", Icons.Filled.Brush, Color(0xFFFFF0E0), Color(0xFFF0A040),
        "Hôm nay, 14:30", 250_000, "Hoàn tất", Green, GreenBg),
    TransactionItem(2, "Giao hàng nhanh", Icons.Filled.LocalShipping, Color(0xFFE0F0FF), Blue,
        "Hôm qua, 09:15", 45_000, "Đang chờ", Color(0xFFF0A040), Color(0xFFFFF0D0)),
    TransactionItem(3, "Dắt chó đi dạo", Icons.Filled.Pets, Color(0xFFFFE8F0), Color(0xFFE06080),
        "22 Th10, 2023", 150_000, "Hoàn tất", Green, GreenBg)
)

// --- Thu nhập ---
private val mockEarningCategories = listOf(
    SpendingCategory(101, "Giao hàng", Icons.Filled.LocalShipping, Color(0xFFE0F0FF), Blue, 3_500_000, 55, Blue),
    SpendingCategory(102, "Dọn dẹp nhà cửa", Icons.Filled.Brush, Color(0xFFFFF0E0), Color(0xFFF0A040), 1_920_000, 30, Orange),
    SpendingCategory(103, "Chăm sóc thú cưng", Icons.Filled.Pets, Color(0xFFFFE8F0), Color(0xFFE06080), 960_000, 15, Color(0xFFE06080))
)

private val mockEarningTransactions = listOf(
    TransactionItem(101, "Giao hàng nhanh Q1", Icons.Filled.LocalShipping, Color(0xFFE0F0FF), Blue,
        "Hôm nay, 10:00", 180_000, "Hoàn tất", Green, GreenBg),
    TransactionItem(102, "Dọn nhà 3 tiếng", Icons.Filled.Brush, Color(0xFFFFF0E0), Color(0xFFF0A040),
        "Hôm qua, 15:30", 350_000, "Hoàn tất", Green, GreenBg),
    TransactionItem(103, "Dắt chó đi dạo", Icons.Filled.Pets, Color(0xFFFFE8F0), Color(0xFFE06080),
        "20 Th10, 2023", 150_000, "Đang chờ", Color(0xFFF0A040), Color(0xFFFFF0D0))
)

private val weeklySpendData = listOf(0.4f, 0.6f, 1.0f, 0.3f)
private val weeklyEarningData = listOf(0.5f, 0.8f, 0.7f, 1.0f)
private val weekLabels = listOf("Tuần 1", "Tuần\n2", "Tuần 3", "Tuần\n4")
private val activeWeek = 2
private val activeEarningWeek = 3

private fun formatVnd(amount: Long): String {
    val fmt = NumberFormat.getNumberInstance(Locale("vi", "VN"))
    return "${fmt.format(kotlin.math.abs(amount))} đ"
}

// ─── Main screen ───────────────────────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FinancialStatsScreen(
    onNavigateBack: () -> Unit = {},
    onCategoryClick: (Int) -> Unit = {},
    onTransactionClick: (Int) -> Unit = {}
) {
    var selectedTab by rememberSaveable { mutableIntStateOf(0) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Thống kê thu chi", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
                    }
                },
                actions = {
                    IconButton(onClick = {}) {
                        Icon(Icons.Filled.DateRange, null, tint = Gray)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = GrayBg
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
        ) {
            // ── Summary card ──────────────────────────────────────────────────
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(horizontal = 20.dp, vertical = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (selectedTab == 0) {
                    Text("Tổng chi tiêu tháng 10", color = Gray, fontSize = 13.sp)
                    Spacer(Modifier.height(4.dp))
                    Text(
                        "4.200.000 đ",
                        fontSize = 30.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF1A1A1A)
                    )
                    Spacer(Modifier.height(6.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.AutoMirrored.Filled.TrendingDown, null,
                            tint = Green, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("Thấp hơn 12% so với tháng trước", color = Green, fontSize = 12.sp)
                    }
                } else {
                    Text("Tổng thu nhập tháng 10", color = Gray, fontSize = 13.sp)
                    Spacer(Modifier.height(4.dp))
                    Text(
                        "6.380.000 đ",
                        fontSize = 30.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Green
                    )
                    Spacer(Modifier.height(6.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.AutoMirrored.Filled.TrendingUp, null,
                            tint = Blue, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("Cao hơn 23% so với tháng trước", color = Blue, fontSize = 12.sp)
                    }
                }
            }

            Spacer(Modifier.height(8.dp))

            // ── Tabs ──────────────────────────────────────────────────────────
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf("Đã chi", "Đã kiếm").forEachIndexed { idx, label ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(50))
                            .clickable { selectedTab = idx },
                        contentAlignment = Alignment.Center
                    ) {
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(50),
                            color = if (selectedTab == idx) Color.White else Color(0xFFF0F0F0),
                            shadowElevation = if (selectedTab == idx) 2.dp else 0.dp
                        ) {
                            Text(
                                label,
                                modifier = Modifier.padding(vertical = 10.dp, horizontal = 16.dp),
                                color = when {
                                    selectedTab != idx -> Gray
                                    idx == 0 -> Orange
                                    else -> Green
                                },
                                fontWeight = if (selectedTab == idx) FontWeight.Bold else FontWeight.Normal,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(8.dp))

            if (selectedTab == 0) {
                SpendingTabContent(
                    onCategoryClick = onCategoryClick,
                    onTransactionClick = onTransactionClick
                )
            } else {
                EarningTabContent(
                    onCategoryClick = onCategoryClick,
                    onTransactionClick = onTransactionClick
                )
            }

            Spacer(Modifier.height(24.dp))
        }
    }
}

// ─── Tab: Đã Chi ───────────────────────────────────────────────────────────────
@Composable
private fun SpendingTabContent(
    onCategoryClick: (Int) -> Unit,
    onTransactionClick: (Int) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically) {
                Text("Biểu đồ chi tiêu", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(Modifier.size(8.dp).background(Orange, CircleShape))
                    Spacer(Modifier.width(4.dp))
                    Text("Tháng này", color = Gray, fontSize = 12.sp)
                }
            }
            Spacer(Modifier.height(16.dp))
            WeeklyBarChart(weeklySpendData, weekLabels, activeWeek, "1.8tr", Orange)
        }
    }

    Spacer(Modifier.height(16.dp))

    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Danh mục chi tiêu", fontWeight = FontWeight.Bold, fontSize = 15.sp)
            Spacer(Modifier.height(12.dp))
            mockCategories.forEach { cat ->
                CategoryRow(cat, isEarning = false, onClick = { onCategoryClick(cat.id) })
                if (cat != mockCategories.last()) Divider(color = Color(0xFFF0F0F0))
            }
        }
    }

    Spacer(Modifier.height(16.dp))

    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically) {
                Text("Lịch sử chi tiêu", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                Text("Xem tất cả", color = Orange, fontSize = 13.sp, modifier = Modifier.clickable {})
            }
            Spacer(Modifier.height(12.dp))
            mockTransactions.forEach { tx ->
                TransactionRow(tx, isEarning = false, onClick = { onTransactionClick(tx.id) })
                if (tx != mockTransactions.last()) Divider(color = Color(0xFFF0F0F0))
            }
        }
    }
}

// ─── Tab: Đã Kiếm ──────────────────────────────────────────────────────────────
@Composable
private fun EarningTabContent(
    onCategoryClick: (Int) -> Unit,
    onTransactionClick: (Int) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically) {
                Text("Biểu đồ thu nhập", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(Modifier.size(8.dp).background(Green, CircleShape))
                    Spacer(Modifier.width(4.dp))
                    Text("Tháng này", color = Gray, fontSize = 12.sp)
                }
            }
            Spacer(Modifier.height(16.dp))
            WeeklyBarChart(weeklyEarningData, weekLabels, activeEarningWeek, "2.1tr", Green)
        }
    }

    Spacer(Modifier.height(16.dp))

    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Danh mục thu nhập", fontWeight = FontWeight.Bold, fontSize = 15.sp)
            Spacer(Modifier.height(12.dp))
            mockEarningCategories.forEach { cat ->
                CategoryRow(cat, isEarning = true, onClick = { onCategoryClick(cat.id) })
                if (cat != mockEarningCategories.last()) Divider(color = Color(0xFFF0F0F0))
            }
        }
    }

    Spacer(Modifier.height(16.dp))

    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically) {
                Text("Lịch sử thu nhập", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                Text("Xem tất cả", color = Green, fontSize = 13.sp, modifier = Modifier.clickable {})
            }
            Spacer(Modifier.height(12.dp))
            mockEarningTransactions.forEach { tx ->
                TransactionRow(tx, isEarning = true, onClick = { onTransactionClick(tx.id) })
                if (tx != mockEarningTransactions.last()) Divider(color = Color(0xFFF0F0F0))
            }
        }
    }
}

// ─── Sub-composables ───────────────────────────────────────────────────────────

@Composable
private fun WeeklyBarChart(
    data: List<Float>,
    labels: List<String>,
    activeIndex: Int,
    highlightValue: String,
    activeColor: Color = Orange
) {
    val chartHeight = 120.dp
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.Bottom
    ) {
        data.forEachIndexed { idx, value ->
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.weight(1f)
            ) {
                if (idx == activeIndex) {
                    Surface(shape = RoundedCornerShape(6.dp), color = Color(0xFF222222)) {
                        Text(
                            highlightValue, color = Color.White, fontSize = 11.sp,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                    Spacer(Modifier.height(4.dp))
                } else {
                    Spacer(Modifier.height(26.dp))
                }

                val animH by animateFloatAsState(targetValue = value, animationSpec = tween(600))
                Box(
                    modifier = Modifier
                        .height((chartHeight.value * animH).dp)
                        .width(28.dp)
                        .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
                        .background(if (idx == activeIndex) activeColor else Color(0xFFEEEEEE))
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    labels[idx], fontSize = 11.sp,
                    color = if (idx == activeIndex) activeColor else Gray,
                    fontWeight = if (idx == activeIndex) FontWeight.Bold else FontWeight.Normal
                )
            }
        }
    }
}

@Composable
private fun CategoryRow(cat: SpendingCategory, isEarning: Boolean, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier.size(44.dp).background(cat.iconBg, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(cat.icon, null, tint = cat.iconTint, modifier = Modifier.size(22.dp))
        }
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(cat.name, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
            Spacer(Modifier.height(4.dp))
            LinearProgressBar(progress = cat.percent / 100f, color = cat.barColor)
            Spacer(Modifier.height(2.dp))
            Text(
                "${cat.percent}% tổng ${if (isEarning) "thu nhập" else "chi tiêu"}",
                color = Gray, fontSize = 11.sp
            )
        }
        Spacer(Modifier.width(8.dp))
        Text(
            "${if (isEarning) "+" else ""}${formatVnd(cat.amount)}",
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            color = if (isEarning) Green else Color(0xFF1A1A1A)
        )
    }
}

@Composable
private fun LinearProgressBar(progress: Float, color: Color) {
    val animProgress by animateFloatAsState(targetValue = progress, animationSpec = tween(800))
    Canvas(modifier = Modifier.fillMaxWidth().height(5.dp)) {
        drawRoundRect(
            color = Color(0xFFEEEEEE),
            size = Size(size.width, size.height),
            cornerRadius = CornerRadius(8f)
        )
        drawRoundRect(
            color = color,
            size = Size(size.width * animProgress, size.height),
            cornerRadius = CornerRadius(8f)
        )
    }
}

@Composable
private fun TransactionRow(tx: TransactionItem, isEarning: Boolean, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier.size(44.dp).background(tx.iconBg, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(tx.icon, null, tint = tx.iconTint, modifier = Modifier.size(22.dp))
        }
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(tx.name, fontWeight = FontWeight.SemiBold, fontSize = 14.sp,
                maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text(tx.dateTime, color = Gray, fontSize = 12.sp)
        }
        Spacer(Modifier.width(8.dp))
        Column(horizontalAlignment = Alignment.End) {
            val sign = if (isEarning) "+" else "-"
            val amtColor = if (isEarning) Green else Color(0xFF1A1A1A)
            Text("$sign${formatVnd(tx.amount)}", fontWeight = FontWeight.Bold,
                fontSize = 13.sp, color = amtColor)
            Spacer(Modifier.height(2.dp))
            Surface(
                shape = RoundedCornerShape(50),
                color = tx.statusBg
            ) {
                Text(
                    tx.status,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                    color = tx.statusColor,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}
