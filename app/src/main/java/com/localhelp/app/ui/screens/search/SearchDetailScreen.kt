package com.localhelp.app.ui.screens.search

import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import com.localhelp.app.model.response.JobResponse
import androidx.compose.runtime.getValue
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.localhelp.app.model.response.CategoryResponse
import com.localhelp.app.ui.common.myjobs.JobFilterSheet
import com.localhelp.app.ui.common.myjobs.MyJobCard
import com.localhelp.app.ui.theme.LocalHelpTheme
import java.text.DecimalFormat

@Composable
fun SearchDetailRoute(
    viewModel: SearchDetailViewModel,
    onBackClick: () -> Unit,
    keyword: String,
    onNavigateToJobDetail: (Long) -> Unit
){
    LaunchedEffect(Unit) {
        viewModel.initSearch(keyword = keyword)
    }

    val uiState by viewModel.uiState.collectAsState()
    val listCategories by viewModel.listCategory.collectAsState()

    Box(modifier = Modifier.fillMaxSize()) {
        when (uiState) {
            is SearchDetailUiState.Loading -> {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
            is SearchDetailUiState.Success -> {
                val successState = uiState as SearchDetailUiState.Success
                SearchDetailScreen(
                    keyword = keyword,
                    listJobs = successState.listJobs,
                    isPaginating = successState.isPaginating,
                    onBackClick = onBackClick,
                    onNavigateToJobDetail = onNavigateToJobDetail,
                    onLoadMore = {
                        viewModel.loadJobs(isLoadMore = true)
                    },
                    applyFilters = viewModel::applyFilters,
                    availableCategories = listCategories
                )
            }
            is SearchDetailUiState.Error -> {
                val error = uiState as SearchDetailUiState.Error
                Box(modifier = Modifier.fillMaxSize()){
                    Text(text = error.message, modifier = Modifier.align(Alignment.Center))
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchDetailScreen(
    keyword: String,
    listJobs: List<JobResponse>,
    isPaginating: Boolean,
    onBackClick: () -> Unit,
    onNavigateToJobDetail: (Long) -> Unit,
    onLoadMore: () -> Unit,
    applyFilters: (keyword: String,
                   distance: Float,
                   minSalary: Float,
                   categories: Set<Long>,
                   timeFilter: String) -> Unit,
    availableCategories: List<CategoryResponse>
) {
    val listState = rememberLazyListState()

    val isAtBottom by remember {
        derivedStateOf {
            val layoutInfo = listState.layoutInfo
            val totalItems = layoutInfo.totalItemsCount
            val lastVisibleItem = layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0

            lastVisibleItem >= totalItems - 1 && totalItems > 0
        }
    }

    LaunchedEffect(isAtBottom) {
        if (isAtBottom && !isPaginating) {
            onLoadMore()
        }
    }
    var showFilterSheet by remember { mutableStateOf(false) }

    if (showFilterSheet) {
        ModalBottomSheet(
            onDismissRequest = { showFilterSheet = false },
            containerColor = Color.White
        ) {
            JobFilterSheet(
                onCloseClick = { showFilterSheet = false },
                onApplyClick = { dist, salary, categories, time ->
                    showFilterSheet = false
                    applyFilters(
                        keyword,
                        dist,
                        salary,
                        categories,
                        time
                    )
                },
                availableCategories = availableCategories
            )
        }
    }

    Scaffold(
        containerColor = Color.White,
        topBar = {
            TopSearchBar(
                keyword = keyword,
                onBackClick = onBackClick,
                onFilterClick = { showFilterSheet = true}
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
                text = "Tìm thấy kết quả cho \"$keyword\"",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )

            LazyColumn(
                state = listState,
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(
                    items = listJobs,
                    key = { it.id }
                ) { job ->
                    MyJobCard(
                        job = job,
                        onClick = { onNavigateToJobDetail(job.id) }
                    )
                }

                if (isPaginating) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(32.dp),
                                color = Color(0xFFED7D68),
                                strokeWidth = 3.dp
                            )
                        }
                    }
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
            .background(Color.White)
            .padding(horizontal = 8.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onBackClick) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Quay lại",
                tint = Color.Black
            )
        }

        Row(
            modifier = Modifier
                .weight(1f)
                .height(44.dp)
                .background(
                    color = Color(0xFFF8F8F8),
                    shape = RoundedCornerShape(24.dp)
                )
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search",
                tint = Color(0xFFED7D68)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = keyword,
                style = MaterialTheme.typography.bodyLarge,
                color = Color.Black,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        IconButton(
            onClick = onFilterClick,
            modifier = Modifier
                .size(44.dp)
                .background(
                    color = Color(0xFFFFF0ED),
                    shape = CircleShape
                )
        ) {
            Icon(
                imageVector = Icons.Default.Tune,
                contentDescription = "Bộ lọc",
                tint = MaterialTheme.colorScheme.primary
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
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(modifier = Modifier.size(72.dp)) {

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
                        .background(Color(0xFFF5F5F5)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Person,
                        contentDescription = null,
                        tint = Color.Gray,
                        modifier = Modifier.size(36.dp)
                    )
                }

                // Icon nhỏ góc trái trên (Badge)
                Box(
                    modifier = Modifier
                        .size(22.dp)
                        .align(Alignment.TopStart)
                        .offset(x = (-2).dp, y = (-2).dp)
                        .background(Color(0xFFFFF0ED), CircleShape)
                        .border(1.dp, Color.White, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.WorkOutline,
                        contentDescription = null,
                        tint = Color(0xFFED7D68), // Icon màu cam
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
                    color = Color.Black,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Tác giả & Khoảng cách
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Outlined.Person,
                        contentDescription = null,
                        tint = Color.Gray,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${job.creatorName}  •  ${job.latitude}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
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
                        color = Color(0xFFED7D68) // Tiền màu cam
                    )

                    Button(
                        onClick = onClick,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFED7D68),
                            contentColor = Color.White
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