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
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Brush
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.localhelp.app.model.response.TransactionItemDTO
import java.text.NumberFormat
import java.util.Locale

private fun formatVndAbs(amount: Double): String {
    val v = kotlin.math.abs(amount)
    val fmt = NumberFormat.getNumberInstance(Locale("vi", "VN"))
    return "${fmt.format(v)} đ"
}

private fun parseColor(hex: String?, defaultColor: Color): Color {
    if (hex.isNullOrEmpty()) return defaultColor
    return try { Color(AndroidColor.parseColor(hex)) } catch (e: Exception) { defaultColor }
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

// ─── Main screen ───────────────────────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryDetailScreen(
    categoryId: Int = 1,
    isEarning: Boolean = false,
    month: Int = 10,
    year: Int = 2023,
    viewModel: CategoryDetailViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit = {},
    onTransactionClick: (Int) -> Unit = {}
) {
    LaunchedEffect(categoryId, isEarning, month, year) {
        viewModel.fetchDetails(categoryId.toLong(), isEarning, month, year)
    }

    val state by viewModel.uiState.collectAsState()

    val accentColor = if (isEarning) Color(0xFF4A90D9) else Color(0xFFF0A040)
    val accentBgColor = if (isEarning) Color(0xFFE0F0FF) else Color(0xFFFFF0E0)
    
    val categoryName = state.data?.categoryName ?: "Tải danh mục..."
    val categoryIcon = getIconByName(categoryName)
    val totalAmount = state.data?.totalAmount ?: 0.0
    val totalLabel = if (isEarning) "Tổng thu nhập tháng $month" else "Tổng chi tiêu tháng $month"

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Chi tiết Danh mục", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
                    }
                },
                actions = {
                    IconButton(onClick = {}) {
                        Icon(Icons.Filled.MoreVert, null, tint = Color(0xFF888888))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = Color(0xFFF7F7F7)
    ) { innerPadding ->
        if (state.isLoading) {
            Box(modifier = Modifier.fillMaxSize().padding(innerPadding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = accentColor)
            }
        } else if (state.data != null) {
            val data = state.data!!
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState())
            ) {
                // ── Category header ───────────────────────────────────────────────
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White)
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .background(accentBgColor, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(categoryIcon, null,
                            tint = accentColor, modifier = Modifier.size(32.dp))
                    }
                    Spacer(Modifier.height(12.dp))
                    Text(categoryName, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(4.dp))
                    Text(formatVndAbs(totalAmount), fontSize = 26.sp,
                        fontWeight = FontWeight.ExtraBold, color = accentColor)
                    Text(totalLabel, color = Color(0xFF888888), fontSize = 12.sp)
                }

                Spacer(Modifier.height(12.dp))

                // ── Donut chart card ──────────────────────────────────────────────
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(if (isEarning) "Phân bổ thu nhập" else "Phân bổ chi tiêu", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        Spacer(Modifier.height(16.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier.size(150.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                DonutChart(
                                    data = data.subCategories.map { it.amount.toFloat() },
                                    colors = data.subCategories.map { parseColor(it.colorCode, accentColor) }
                                )
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("Lớn nhất", color = Color(0xFF888888), fontSize = 10.sp)
                                    val maxPercent = data.subCategories.maxOfOrNull { it.percentage } ?: 0.0
                                    Text("${maxPercent.toInt()}%", fontWeight = FontWeight.ExtraBold, fontSize = 22.sp)
                                }
                            }
                            Spacer(Modifier.width(16.dp))
                            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                data.subCategories.forEach { sub ->
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier
                                                .size(10.dp)
                                                .background(parseColor(sub.colorCode, accentColor), CircleShape)
                                        )
                                        Spacer(Modifier.width(6.dp))
                                        Column {
                                            Text(sub.subName, fontSize = 12.sp,
                                                fontWeight = FontWeight.Medium,
                                                maxLines = 1, overflow = TextOverflow.Ellipsis)
                                            Text(formatVndAbs(sub.amount),
                                                fontSize = 12.sp, color = Color(0xFF888888))
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(Modifier.height(12.dp))

                // ── Transaction history ───────────────────────────────────────────
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Lịch sử giao dịch", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        Spacer(Modifier.height(12.dp))
                        if (data.transactions.isEmpty()) Text("Chưa có giao dịch.", color = Color(0xFF888888), fontSize = 13.sp)
                        data.transactions.forEachIndexed { idx, tx ->
                            CategoryTxRow(tx, isEarning, onClick = { onTransactionClick(tx.id.toInt()) })
                            if (idx != data.transactions.lastIndex)
                                HorizontalDivider(color = Color(0xFFF0F0F0))
                        }
                    }
                }

                Spacer(Modifier.height(12.dp))

                // ── AI insight card ───────────────────────────────────────────────
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = if (isEarning) Color(0xFFF0F8FF) else Color(0xFFFFF8F0)),
                    elevation = CardDefaults.cardElevation(0.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Icon(Icons.Filled.AutoAwesome, null,
                            tint = accentColor, modifier = Modifier.size(20.dp))
                        Spacer(Modifier.width(10.dp))
                        Column {
                            Text("Nhận xét",
                                fontWeight = FontWeight.Bold, fontSize = 14.sp, color = accentColor)
                            Spacer(Modifier.height(4.dp))
                            Text(
                                data.aiInsight,
                                fontSize = 13.sp,
                                color = Color(0xFF555555),
                                lineHeight = 18.sp
                            )
                        }
                    }
                }

                Spacer(Modifier.height(24.dp))
            }
        }
    }
}

// ─── Sub-composables ───────────────────────────────────────────────────────────

@Composable
private fun DonutChart(data: List<Float>, colors: List<Color>) {
    if (data.isEmpty() || data.sum() == 0f) return
    
    val total = data.sum()
    val animProgress by animateFloatAsState(targetValue = 1f, animationSpec = tween(900))

    Canvas(modifier = Modifier.size(150.dp)) {
        val stroke = 28f
        val diameter = size.minDimension - stroke
        val topLeft = Offset((size.width - diameter) / 2f, (size.height - diameter) / 2f)
        var startAngle = -90f

        data.zip(colors).forEach { (value, color) ->
            val sweep = (value / total) * 360f * animProgress
            if (sweep > 0f) {
                drawArc(
                    color = color,
                    startAngle = startAngle,
                    sweepAngle = sweep - 2f,
                    useCenter = false,
                    topLeft = topLeft,
                    size = Size(diameter, diameter),
                    style = Stroke(width = stroke, cap = StrokeCap.Round)
                )
                startAngle += sweep
            }
        }
    }
}

@Composable
private fun CategoryTxRow(tx: TransactionItemDTO, isEarning: Boolean, onClick: () -> Unit) {
    val txColor = parseColor(tx.colorCode, if(isEarning) Color(0xFF4A90D9) else Color(0xFFF0A040))
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(42.dp)
                .background(txColor.copy(alpha=0.2f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(getIconByName(tx.serviceName), null, tint = txColor, modifier = Modifier.size(20.dp))
        }
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(tx.name, fontWeight = FontWeight.SemiBold, fontSize = 13.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text(tx.dateStr, color = Color(0xFF888888), fontSize = 11.sp)
        }
        Spacer(Modifier.width(8.dp))
        
        val sign = if (isEarning) "+" else "-"
        val textColor = if (isEarning) Color(0xFF2E9B5B) else Color(0xFF1A1A1A)
        
        Text("$sign${formatVndAbs(tx.amount)}", fontWeight = FontWeight.Bold,
            fontSize = 13.sp, color = textColor)
    }
}
