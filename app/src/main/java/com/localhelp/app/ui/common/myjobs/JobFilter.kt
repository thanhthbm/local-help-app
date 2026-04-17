package com.localhelp.app.ui.common.myjobs

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.localhelp.app.model.response.CategoryResponse

@Composable
fun JobFilterSheet(
    availableCategories: List<CategoryResponse>,
    onCloseClick: () -> Unit,
    onApplyClick: (distance: Float, minSalary: Float, categories: Set<Long>, timeFilter: String) -> Unit
) {
    var distance by remember { mutableFloatStateOf(5.0f) }
    var minSalary by remember { mutableFloatStateOf(150f) }
    var selectedCategories by remember { mutableStateOf(setOf<Long>()) }
    var selectedTime by remember { mutableStateOf("ALL") }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface) // Đổi sang surface
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onCloseClick) {
                Icon(Icons.Default.Close, contentDescription = "Đóng", tint = MaterialTheme.colorScheme.onSurface)
            }
            Text(
                text = "Bộ lọc tìm kiếm",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 18.sp
            )
            TextButton(onClick = {
                distance = 10f
                minSalary = 0f
                selectedCategories = emptySet()
                selectedTime = "ALL"
            }) {
                Text(text = "Đặt lại", color = MaterialTheme.colorScheme.primary) // Chữ màu cam
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Column(
            modifier = Modifier
                .weight(1f, fill = false)
                .verticalScroll(rememberScrollState())
        ) {
            FilterSliderSection(
                title = "Khoảng cách",
                icon = Icons.Default.Navigation,
                value = distance,
                valueText = "${String.format("%.1f", distance)} km",
                valueRange = 0f..100f,
                onValueChange = { distance = it },
                minLabel = "0km",
                maxLabel = "100km"
            )

            HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp), color = MaterialTheme.colorScheme.outlineVariant)

            FilterSliderSection(
                title = "Mức thù lao tối thiểu",
                icon = Icons.Default.Payments,
                value = minSalary,
                valueText = if (minSalary == 0f) "0đ" else "${minSalary.toInt()}k+",
                valueRange = 0f..1000f,
                onValueChange = { minSalary = it },
                minLabel = "0đ",
                maxLabel = "1tr"
            )

            HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp), color = MaterialTheme.colorScheme.outlineVariant)

            Text(
                text = "Danh mục",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(bottom = 12.dp)
            )
            CategoryGrid(
                categories = availableCategories,
                selectedIds = selectedCategories,
                onCategoryToggle = { clickedId ->

                    val newSet = selectedCategories.toMutableSet()
                    if (newSet.contains(clickedId)) {
                        newSet.remove(clickedId)
                    } else {
                        newSet.add(clickedId)
                    }
                    selectedCategories = newSet
                }
            )
            HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp), color = MaterialTheme.colorScheme.outlineVariant)

            Text(
                text = "Thời gian",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(bottom = 12.dp)
            )
            TimeFilterOptions(
                selectedTime = selectedTime,
                onTimeSelect = { selectedTime = it }
            )

            Spacer(modifier = Modifier.height(16.dp))
        }

        Button(
            onClick = { onApplyClick(distance, minSalary, selectedCategories, selectedTime) },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary, // Nền nút cam
                contentColor = MaterialTheme.colorScheme.onPrimary  // Chữ trắng
            ),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text(text = "Áp dụng bộ lọc", fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }
    }
}


@Composable
fun FilterSliderSection(
    title: String,
    icon: ImageVector,
    value: Float,
    valueText: String,
    valueRange: ClosedFloatingPointRange<Float>,
    onValueChange: (Float) -> Unit,
    minLabel: String,
    maxLabel: String
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = icon, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = title, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = MaterialTheme.colorScheme.onSurface)
            }
            Text(text = valueText, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
        }

        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = valueRange,
            colors = SliderDefaults.colors(
                thumbColor = MaterialTheme.colorScheme.primary,
                activeTrackColor = MaterialTheme.colorScheme.primary,
                inactiveTrackColor = MaterialTheme.colorScheme.surfaceVariant // Trục xám nhạt
            ),
            modifier = Modifier.padding(vertical = 0.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = minLabel, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp)
            Text(text = maxLabel, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp)
        }
    }
}


@Composable
fun CategoryGrid(
    categories: List<CategoryResponse>,
    selectedIds: Set<Long>,
    onCategoryToggle: (Long) -> Unit
) {
    categories.chunked(2).forEach { rowItems ->
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            rowItems.forEach { item ->
                CategoryItem(
                    category = item,
                    isSelected = selectedIds.contains(item.id),
                    modifier = Modifier.weight(1f),
                    onClick = { onCategoryToggle(item.id) }
                )
            }
            // Điền khoảng trống nếu dòng cuối bị lẻ
            if (rowItems.size == 1) {
                Spacer(modifier = Modifier.weight(1f))
            }
        }
    }
}

@Composable
fun CategoryItem(
    category: CategoryResponse,
    isSelected: Boolean,
    modifier: Modifier,
    onClick: () -> Unit
) {
    val dbColor = try {
        Color(android.graphics.Color.parseColor(category.colorCode))
    } catch (e: Exception) {
        MaterialTheme.colorScheme.primary
    }

    val borderColor = if (isSelected) dbColor else MaterialTheme.colorScheme.outlineVariant
    val textColor = if (isSelected) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant
    val boxBgColor = if (isSelected) dbColor else Color.Transparent

    Row(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .border(width = 1.dp, color = borderColor, shape = RoundedCornerShape(8.dp))
            .clickable { onClick() }
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = category.iconUrl,
            contentDescription = category.name,
            modifier = Modifier.size(24.dp)
        )

        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = category.name,
            fontSize = 14.sp,
            modifier = Modifier.weight(1f),
            color = textColor,
            maxLines = 1
        )

        Box(
            modifier = Modifier
                .size(18.dp)
                .border(width = 1.5.dp, color = borderColor, shape = RoundedCornerShape(4.dp))
                .background(boxBgColor, RoundedCornerShape(4.dp))
        )
    }
}

@Composable
fun TimeFilterOptions(selectedTime: String, onTimeSelect: (String) -> Unit) {
    val times = listOf(
        Triple("ALL", "Tất cả thời gian", "Hiển thị mọi công việc có sẵn"),
        Triple("TODAY", "Hôm nay", "Các việc cần làm ngay trong ngày"),
        Triple("TOMORROW", "Ngày mai", "Lên lịch trước cho ngày mai"),
        Triple("THIS_WEEK", "Tuần này", "Các việc trong 7 ngày tới")
    )

    Column {
        times.forEach { time ->
            val isSelected = selectedTime == time.first
            val borderColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant
            val boxBgColor = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onTimeSelect(time.first) }
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = time.second, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurface)
                    Text(text = time.third, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp)
                }

                Box(
                    modifier = Modifier
                        .size(20.dp)
                        .border(width = 1.5.dp, color = borderColor, shape = RoundedCornerShape(4.dp))
                        .background(boxBgColor, RoundedCornerShape(4.dp))
                )
            }
        }
    }
}