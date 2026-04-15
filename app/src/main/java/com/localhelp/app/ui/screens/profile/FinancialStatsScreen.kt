package com.localhelp.app.ui.screens.profile

import android.graphics.Color as AndroidColor
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
import androidx.compose.material.icons.filled.Info
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
import androidx.hilt.navigation.compose.hiltViewModel
import com.localhelp.app.model.response.CategoryItemDTO
import com.localhelp.app.model.response.TransactionItemDTO
import java.text.NumberFormat
import java.util.Locale
import java.util.Calendar

// ─── Color tokens ──────────────────────────────────────────────────────────────
private val Orange = Color(0xFFF06A50)
private val Green = Color(0xFF2E9B5B)
private val GreenBg = Color(0xFFE8F8F0)
private val Gray = Color(0xFF888888)
private val GrayBg = Color(0xFFF7F7F7)
private val Blue = Color(0xFF4A90D9)
private val Purple = Color(0xFF9B59B6)

private fun parseColor(hex: String?, defaultColor: Color): Color {
    if (hex.isNullOrEmpty()) return defaultColor
    return try {
        Color(AndroidColor.parseColor(hex))
    } catch (e: Exception) {
        defaultColor
    }
}

private fun getIconByName(name: String?): ImageVector {
    val low = name?.lowercase() ?: ""
    return when {
        low.contains("dọn") -> Icons.Filled.Brush
        low.contains("giao") || low.contains("thuê") -> Icons.Filled.LocalShipping
        low.contains("chó") || low.contains("chăm") -> Icons.Filled.Pets
        else -> Icons.Filled.Info
    }
}

private val weekLabels = listOf("Tuần 1", "Tuần\n2", "Tuần 3", "Tuần\n4")

private fun formatVnd(amount: Double): String {
    val fmt = NumberFormat.getNumberInstance(Locale("vi", "VN"))
    return "${fmt.format(kotlin.math.abs(amount))} đ"
}

// ─── Main screen ───────────────────────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FinancialStatsScreen(
    viewModel: FinanceStatsViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit = {},
    onCategoryClick: (Int, Boolean, Int, Int) -> Unit = { _, _, _, _ -> }, // id, isEarning, month, year
    onTransactionClick: (Int) -> Unit = {}
) {
    var selectedTab by rememberSaveable { mutableIntStateOf(0) }
    
    val currentMonth = Calendar.getInstance().get(Calendar.MONTH) + 1
    val currentYear = Calendar.getInstance().get(Calendar.YEAR)

    LaunchedEffect(Unit) {
        viewModel.fetchData(currentMonth, currentYear)
    }

    val spendingState by viewModel.spendingState.collectAsState()
    val earningState by viewModel.earningState.collectAsState()

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
                    Text("Tổng chi tiêu tháng $currentMonth", color = Gray, fontSize = 13.sp)
                    Spacer(Modifier.height(4.dp))
                    Text(
                        formatVnd(spendingState.totalAmount),
                        fontSize = 30.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF1A1A1A)
                    )
                    Spacer(Modifier.height(6.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (spendingState.trend == "DOWN") {
                            Icon(Icons.AutoMirrored.Filled.TrendingDown, null, tint = Green, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(4.dp))
                            Text("Thấp hơn ${spendingState.percentageChange}% so với tháng trước", color = Green, fontSize = 12.sp)
                        } else {
                            Icon(Icons.AutoMirrored.Filled.TrendingUp, null, tint = Orange, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(4.dp))
                            Text("Cao hơn ${spendingState.percentageChange}% so với tháng trước", color = Orange, fontSize = 12.sp)
                        }
                    }
                } else {
                    Text("Tổng thu nhập tháng $currentMonth", color = Gray, fontSize = 13.sp)
                    Spacer(Modifier.height(4.dp))
                    Text(
                        formatVnd(earningState.totalAmount),
                        fontSize = 30.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Green
                    )
                    Spacer(Modifier.height(6.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (earningState.trend == "UP") {
                            Icon(Icons.AutoMirrored.Filled.TrendingUp, null, tint = Blue, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(4.dp))
                            Text("Cao hơn ${earningState.percentageChange}% so với tháng trước", color = Blue, fontSize = 12.sp)
                        } else {
                            Icon(Icons.AutoMirrored.Filled.TrendingDown, null, tint = Orange, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(4.dp))
                            Text("Thấp hơn ${earningState.percentageChange}% so với tháng trước", color = Orange, fontSize = 12.sp)
                        }
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
                if (spendingState.isLoading) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally).padding(20.dp), color = Orange)
                } else {
                    SpendingTabContent(
                        state = spendingState,
                        onCategoryClick = { id -> onCategoryClick(id, false, currentMonth, currentYear) },
                        onTransactionClick = onTransactionClick
                    )
                }
            } else {
                if (earningState.isLoading) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally).padding(20.dp), color = Green)
                } else {
                    EarningTabContent(
                        state = earningState,
                        onCategoryClick = { id -> onCategoryClick(id, true, currentMonth, currentYear) },
                        onTransactionClick = onTransactionClick
                    )
                }
            }

            Spacer(Modifier.height(24.dp))
        }
    }
}

// ─── Tab: Đã Chi ───────────────────────────────────────────────────────────────
@Composable
private fun SpendingTabContent(
    state: FinanceOverviewUiState,
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
            WeeklyBarChart(state.weeklyChart.map { it.toFloat() }, weekLabels, -1, formatVnd(state.totalAmount), Orange)
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
            if (state.categories.isEmpty()) Text("Chưa có danh mục nào.", color = Gray, fontSize = 13.sp)
            state.categories.forEach { cat ->
                CategoryRow(cat, isEarning = false, onClick = { onCategoryClick(cat.id.toInt()) })
                if (cat != state.categories.last()) HorizontalDivider(color = Color(0xFFF0F0F0))
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
            if (state.recentTransactions.isEmpty()) Text("Chưa có giao dịch.", color = Gray, fontSize = 13.sp)
            state.recentTransactions.forEach { tx ->
                TransactionRow(tx, isEarning = false, onClick = { onTransactionClick(tx.id.toInt()) })
                if (tx != state.recentTransactions.last()) HorizontalDivider(color = Color(0xFFF0F0F0))
            }
        }
    }
}

// ─── Tab: Đã Kiếm ──────────────────────────────────────────────────────────────
@Composable
private fun EarningTabContent(
    state: FinanceOverviewUiState,
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
            WeeklyBarChart(state.weeklyChart.map { it.toFloat() }, weekLabels, -1, formatVnd(state.totalAmount), Green)
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
            if (state.categories.isEmpty()) Text("Chưa có danh mục nào.", color = Gray, fontSize = 13.sp)
            state.categories.forEach { cat ->
                CategoryRow(cat, isEarning = true, onClick = { onCategoryClick(cat.id.toInt()) })
                if (cat != state.categories.last()) HorizontalDivider(color = Color(0xFFF0F0F0))
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
            if (state.recentTransactions.isEmpty()) Text("Chưa có giao dịch.", color = Gray, fontSize = 13.sp)
            state.recentTransactions.forEach { tx ->
                TransactionRow(tx, isEarning = true, onClick = { onTransactionClick(tx.id.toInt()) })
                if (tx != state.recentTransactions.last()) HorizontalDivider(color = Color(0xFFF0F0F0))
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
    if (data.isEmpty()) return
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
private fun CategoryRow(cat: CategoryItemDTO, isEarning: Boolean, onClick: () -> Unit) {
    val baseColor = parseColor(cat.colorCode, if(isEarning) Blue else Orange)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier.size(44.dp).background(baseColor.copy(alpha = 0.2f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(getIconByName(cat.name), null, tint = baseColor, modifier = Modifier.size(22.dp))
        }
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(cat.name, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
            Spacer(Modifier.height(4.dp))
            LinearProgressBar(progress = (cat.percentage / 100).toFloat(), color = baseColor)
            Spacer(Modifier.height(2.dp))
            Text(
                "${cat.percentage}% tổng ${if (isEarning) "thu nhập" else "chi tiêu"}",
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
    val animProgress by animateFloatAsState(targetValue = progress.takeIf { it > 0f } ?: 0.01f, animationSpec = tween(800))
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
private fun TransactionRow(tx: TransactionItemDTO, isEarning: Boolean, onClick: () -> Unit) {
    val baseColor = parseColor(tx.colorCode, if (isEarning) Blue else Orange)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier.size(44.dp).background(baseColor.copy(alpha=0.2f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(getIconByName(tx.serviceName), null, tint = baseColor, modifier = Modifier.size(22.dp))
        }
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(tx.name, fontWeight = FontWeight.SemiBold, fontSize = 14.sp,
                maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text(tx.dateStr, color = Gray, fontSize = 12.sp)
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
                color = if (tx.status == "COMPLETED") GreenBg else Color(0xFFFFF0D0)
            ) {
                Text(
                    if (tx.status == "COMPLETED") "Hoàn tất" else "Chờ xử lý",
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                    color = if (tx.status == "COMPLETED") Green else Orange,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}
