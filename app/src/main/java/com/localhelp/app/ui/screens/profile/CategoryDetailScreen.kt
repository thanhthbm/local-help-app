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
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Brush
import androidx.compose.material.icons.filled.LocalShipping
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

// ─── Mock data for category 1: Dọn dẹp nhà cửa ───────────────────────────────
private data class SubCategory(val name: String, val amount: Long, val color: Color)
private data class CategoryTransaction(
    val name: String,
    val icon: ImageVector,
    val iconBg: Color,
    val iconTint: Color,
    val date: String,
    val amount: Long
)

// Dữ liệu mock chi tiêu
private val mockSubCategories = listOf(
    SubCategory("Dọn nhà theo giờ", 1_050_000, Color(0xFFF0A040)),
    SubCategory("Vệ sinh máy lạnh", 630_000, Color(0xFF4A90D9)),
    SubCategory("Giặt rèm", 420_000, Color(0xFF9B59B6))
)

private val mockCategoryTransactions = listOf(
    CategoryTransaction("Dọn nhà theo giờ (3h)", Icons.Filled.Brush,
        Color(0xFFFFF0E0), Color(0xFFF0A040), "28 Th10, 14:30", 350_000),
    CategoryTransaction("Giặt ủi đồ vest", Icons.Filled.LocalShipping,
        Color(0xFFE0F0FF), Color(0xFF4A90D9), "25 Th10, 09:15", 120_000),
    CategoryTransaction("Vệ sinh máy lạnh", Icons.Filled.Brush,
        Color(0xFFFFF0E0), Color(0xFFF0A040), "20 Th10, 10:00", 630_000),
    CategoryTransaction("Dọn nhà theo giờ (3h)", Icons.Filled.Brush,
        Color(0xFFFFF0E0), Color(0xFFF0A040), "15 Th10, 14:00", 350_000)
)

// Dữ liệu mock thu nhập (categoryId = 101 - Giao hàng)
private val mockEarningSubCategories = listOf(
    SubCategory("Giao đồ ăn", 1_750_000, Color(0xFF4A90D9)),
    SubCategory("Giao bưu kiện", 1_050_000, Color(0xFF2E9B5B)),
    SubCategory("Chuyển đồ nặng", 700_000, Color(0xFFF0A040))
)

private val mockEarningTransactions = listOf(
    CategoryTransaction("Giao đồ ăn nhanh Q1", Icons.Filled.LocalShipping,
        Color(0xFFE0F0FF), Color(0xFF4A90D9), "28 Th10, 11:30", 65_000),
    CategoryTransaction("Chuyển bưu kiện nội thành", Icons.Filled.LocalShipping,
        Color(0xFFE0F0FF), Color(0xFF4A90D9), "28 Th10, 09:15", 45_000),
    CategoryTransaction("Giao hàng siêu tốc", Icons.Filled.LocalShipping,
        Color(0xFFE0F0FF), Color(0xFF4A90D9), "27 Th10, 16:00", 120_000),
    CategoryTransaction("Chuyển đồ cồng kềnh", Icons.Filled.LocalShipping,
        Color(0xFFE0F0FF), Color(0xFF4A90D9), "26 Th10, 14:00", 250_000)
)


private fun formatVndAbs(amount: Long): String {
    val v = kotlin.math.abs(amount)
    return "${String.format("%,d", v).replace(",", ".")} đ"
}

// ─── Main screen ───────────────────────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryDetailScreen(
    categoryId: Int = 1,
    onNavigateBack: () -> Unit = {},
    onTransactionClick: (Int) -> Unit = {}
) {
    val isEarning = categoryId > 100
    val accentColor = if (isEarning) Color(0xFF4A90D9) else Color(0xFFF0A040)
    val accentBgColor = if (isEarning) Color(0xFFE0F0FF) else Color(0xFFFFF0E0)
    
    val categoryName = if (isEarning) "Giao hàng (Thu)" else "Dọn dẹp nhà cửa"
    val categoryIcon = if (isEarning) Icons.Filled.LocalShipping else Icons.Filled.Brush
    val totalAmount = if (isEarning) "3.500.000 đ" else "2.100.000 đ"
    val totalLabel = if (isEarning) "Tổng thu nhập tháng 10" else "Tổng chi tiêu tháng 10"
    
    val subCategories = if (isEarning) mockEarningSubCategories else mockSubCategories
    val transactions = if (isEarning) mockEarningTransactions else mockCategoryTransactions

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
                Text(totalAmount, fontSize = 26.sp,
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
                                data = subCategories.map { it.amount.toFloat() },
                                colors = subCategories.map { it.color }
                            )
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("Lớn nhất", color = Color(0xFF888888), fontSize = 10.sp)
                                Text("50%", fontWeight = FontWeight.ExtraBold, fontSize = 22.sp)
                            }
                        }
                        Spacer(Modifier.width(16.dp))
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            subCategories.forEach { sub ->
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(10.dp)
                                            .background(sub.color, CircleShape)
                                    )
                                    Spacer(Modifier.width(6.dp))
                                    Column {
                                        Text(sub.name, fontSize = 12.sp,
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
                    transactions.forEachIndexed { idx, tx ->
                        CategoryTxRow(tx, isEarning)
                        if (idx != transactions.lastIndex)
                            Divider(color = Color(0xFFF0F0F0))
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
                        Text("AI Nhận xét",
                            fontWeight = FontWeight.Bold, fontSize = 14.sp, color = accentColor)
                        Spacer(Modifier.height(4.dp))
                        Text(
                            if (isEarning) "Bạn có thu nhập lớn nhất từ các đơn hàng giao đồ ăn. Hãy cân nhắc mở rộng khu vực giao hàng vào giờ cao điểm để tăng thêm 20% thu nhập."
                            else "Bạn chi nhiều nhất vào cuối tuần cho dịch vụ này. Hãy cân nhắc đặt lịch vào giữa tuần để nhận ưu đãi giảm giá 15%.",
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

// ─── Sub-composables ───────────────────────────────────────────────────────────

@Composable
private fun DonutChart(data: List<Float>, colors: List<Color>) {
    // Return early if empty data to avoid crash
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
                    sweepAngle = sweep - 2f,   // small gap between segments
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
private fun CategoryTxRow(tx: CategoryTransaction, isEarning: Boolean) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(42.dp)
                .background(tx.iconBg, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(tx.icon, null, tint = tx.iconTint, modifier = Modifier.size(20.dp))
        }
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(tx.name, fontWeight = FontWeight.SemiBold, fontSize = 13.sp,
                maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text(tx.date, color = Color(0xFF888888), fontSize = 11.sp)
        }
        Spacer(Modifier.width(8.dp))
        
        val sign = if (isEarning) "+" else "-"
        val textColor = if (isEarning) Color(0xFF2E9B5B) else Color(0xFF1A1A1A)
        
        Text("$sign${formatVndAbs(tx.amount)}", fontWeight = FontWeight.Bold,
            fontSize = 13.sp, color = textColor)
    }
}
