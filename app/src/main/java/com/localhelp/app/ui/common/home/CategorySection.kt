package com.localhelp.app.ui.common.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import coil.compose.AsyncImage
import com.localhelp.app.model.response.CategoryResponse

@Composable
fun CategorySection(
    categories: List<CategoryResponse>,
    selectedCategoryId: Long? = null,
    onCategoryClick: (Long?) -> Unit
) {
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            CategoryItem(
                title = "Tất cả",
                icon = {
                    Icon(
                        Icons.Default.GridView,
                        contentDescription = null,
                        tint = if (selectedCategoryId == null) Color.White else Color(0xFFED7D68),
                        modifier = Modifier.size(18.dp)
                    )
                },
                containerColor = if (selectedCategoryId == null) Color(0xFFED7D68) else Color.White,
                contentColor = if (selectedCategoryId == null) Color.White else Color.Black,
                onClick = { onCategoryClick(null) }
            )
        }

        items(categories) { category ->
            val isSelected = selectedCategoryId == category.id
            val color = try {
                Color(android.graphics.Color.parseColor(category.colorCode))
            } catch (e: Exception) {
                Color(0xFF4CAF50) // Default green
            }

            CategoryItem(
                title = category.name,
                icon = {
                    AsyncImage(
                        model = category.iconUrl,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                },
                containerColor = if (isSelected) color else Color.White,
                contentColor = if (isSelected) Color.White else color,
                onClick = { onCategoryClick(category.id) }
            )
        }
    }
}

@Composable
fun CategoryItem(
    title: String,
    icon: @Composable () -> Unit,
    containerColor: Color,
    contentColor: Color,
    onClick: () -> Unit
){
    Surface(
        shape = RoundedCornerShape(24.dp),
        color = containerColor,
        border = if (containerColor == Color.White) BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.5f)) else null,
        modifier = Modifier
            .height(40.dp)
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            icon()

            Spacer(modifier = Modifier.width(8.dp))

            Text(text = title,
                color = contentColor,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}
