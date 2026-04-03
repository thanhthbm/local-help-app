package com.localhelp.app.ui.screens.search

import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.key
import androidx.compose.ui.tooling.preview.Preview
import com.localhelp.app.model.response.JobResponse
import androidx.compose.runtime.getValue
import com.localhelp.app.ui.theme.LocalHelpTheme
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.WorkOutline
import androidx.compose.material3.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.DecimalFormat

val jobList = listOf<JobResponse>(
//    JobResponse(1, "Tổng vệ sinh căn hộ 2PN", "Minh Tú", 300.000),
//    JobResponse(2, "Lau dọn cửa kính chung cư", "Lan Anh", 150.000),
//    JobResponse(3, "Dọn dẹp nhà bếp chuyên sâu", "Hoàng Nam", 250.000),
//    JobResponse(4, "Hút bụi & giặt thảm phòng khách", "Thanh Thảo", 450.000),
    // Thêm dữ liệu khác ở đây
)

//@Preview(showBackground = true)
//@Composable
//fun PreviewSearchDetailScreen(){
//    LocalHelpTheme(
//        content = {SearchDetailScreen(
//            onBackClick = {},
//            keyword = "abc",
//            listJobs = jobList,
//            onNavigateToJobDetail = {}
//        )},
//        darkTheme = false
//    )
//}

@Composable
fun SearchDetailRoute(
    viewModel: SearchDetailViewModel,
    onBackClick: () -> Unit,
    keyword: String,
    onNavigateToJobDetail: (Long) -> Unit
){
    val uiState by viewModel.uiState.collectAsState()
    when(uiState){
        is SearchDetailUiState.Loading -> CircularProgressIndicator()
        is SearchDetailUiState.Success -> {
            SearchDetailScreen(
                onBackClick = onBackClick,
                keyword = keyword,
                listJobs = (uiState as SearchDetailUiState.Success).listJobs,
                onNavigateToJobDetail = onNavigateToJobDetail
            )
        }
    }
}

@Composable
fun SearchDetailScreen(
    onBackClick: () -> Unit,
    keyword: String,
    listJobs: List<JobResponse>,
    onNavigateToJobDetail: (Long) -> Unit
) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopSearchBar(
                keyword = keyword,
                onBackClick = onBackClick,
                onFilterClick = { /* Xử lý mở bộ lọc */ }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            FilterChipsRow()

            Text(
                text = "Tìm thấy ${listJobs.size} kết quả cho \"$keyword\"",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
            )

            LazyColumn(
                contentPadding = PaddingValues(bottom = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(
                    items = listJobs,
                    key = { it.id }
                ) { job ->
                    JobItemCard(
                        job = job,
                        onClick = { onNavigateToJobDetail(job.id) }
                    )
                }
            }
        }
    }
}

@Composable
private fun TopSearchBar(
    keyword: String,
    onBackClick: () -> Unit,
    onFilterClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .padding(horizontal = 8.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Nút Back
        IconButton(onClick = onBackClick) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Quay lại",
                tint = MaterialTheme.colorScheme.onSurface
            )
        }

        Row(
            modifier = Modifier
                .weight(1f)
                .height(44.dp)
                .background(
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f), // Màu xám nhạt
                    shape = RoundedCornerShape(24.dp)
                )
                .clickable { /* Xử lý khi user muốn sửa từ khóa */ }
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search",
                tint = MaterialTheme.colorScheme.primary // Icon kính lúp màu cam (giống design)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = keyword,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        // Nút Mở bộ lọc (Hình tròn, nền cam nhạt, icon cam đậm)
        IconButton(
            onClick = onFilterClick,
            modifier = Modifier
                .size(44.dp)
                .background(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shape = CircleShape
                )
        ) {
            Icon(
                imageVector = Icons.Default.Tune,
                contentDescription = "Bộ lọc",
                tint = MaterialTheme.colorScheme.primary // Màu cam chính
            )
        }
        Spacer(modifier = Modifier.width(8.dp))
    }
}

@Composable
private fun FilterChipsRow() {
    val filters = listOf("Gần tôi", "Giá tốt nhất", "Đánh giá cao")

    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .padding(bottom = 8.dp),
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(filters) { filterName ->
            Surface(
                modifier = Modifier.clickable {

                }.border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.outlineVariant,
                    shape = RoundedCornerShape(50)
                ),
                shape = RoundedCornerShape(50),
                color = MaterialTheme.colorScheme.surface
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = filterName,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowDown,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun JobItemCard(
    job: JobResponse,
    onClick: () -> Unit
) {
    // Format tiền Việt Nam: 300000 -> 300.000
    val formatter = DecimalFormat("#,###").apply {
        val symbols = decimalFormatSymbols
        symbols.groupingSeparator = '.'
        decimalFormatSymbols = symbols
    }
    val formattedPrice = formatter.format(job.price)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clickable { onClick() }, // Bấm vào cả thẻ để xem chi tiết
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface // Thẻ màu trắng
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Khối chứa Ảnh đại diện và Icon Badge
            Box(modifier = Modifier.size(72.dp)) {

                // NẾU DÙNG COIL, thay Box này bằng AsyncImage:
                /*
                AsyncImage(
                    model = job.imageUrl,
                    contentDescription = "Avatar",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize().clip(CircleShape)
                )
                */
                // Khối vẽ placeholder avatar tạm thời
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Person,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(36.dp)
                    )
                }

                // Icon nhỏ góc trái trên (Badge)
                Box(
                    modifier = Modifier
                        .size(22.dp)
                        .align(Alignment.TopStart)
                        .offset(x = (-2).dp, y = (-2).dp)
                        .background(MaterialTheme.colorScheme.primaryContainer, CircleShape)
                        .border(1.dp, MaterialTheme.colorScheme.surface, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.WorkOutline,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary, // Icon màu cam
                        modifier = Modifier.size(12.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Khối thông tin bên phải
            Column(modifier = Modifier.weight(1f)) {
                // Tiêu đề
                Text(
                    text = job.title ?: "",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    ),
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Tác giả & Khoảng cách
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Outlined.Person,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${job.creatorName}  •  ${job.latitude}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Giá tiền & Nút bấm
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "$formattedPrice đ",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        ),
                        color = MaterialTheme.colorScheme.primary // Tiền màu cam
                    )

                    Button(
                        onClick = onClick,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        ),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 0.dp),
                        modifier = Modifier.height(32.dp),
                        shape = RoundedCornerShape(50) // Bo cong hoàn toàn hai đầu
                    ) {
                        Text(
                            text = "Chi tiết",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                }
            }
        }
    }
}