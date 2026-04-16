package com.localhelp.app.ui.screens.jobmanagement

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.localhelp.app.model.response.JobResponse
import com.localhelp.app.ui.common.myjobs.TaskActionButton
import com.localhelp.app.ui.common.myjobs.TaskBottomInfo
import com.localhelp.app.ui.common.myjobs.TaskStatusBadge
import java.text.DecimalFormat

@Composable
fun MyTasksScreen(
    viewModel: MyTasksViewModel,
    onNavigateToDetail: (Long) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
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
        if (isAtBottom && !uiState.isLastPage) {
            viewModel.loadMyTasks(isLoadMore = true)
        }
    }

    Column(modifier = Modifier.fillMaxSize().background(Color(0xFFF9FAFB))) {
        if (uiState.isLoading && uiState.jobs.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                state = listState,
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Hiển thị ${uiState.jobs.size} công việc", color = Color.Gray, fontSize = 14.sp)
                        Text(text = "Mới nhất ≡", color = Color.Gray, fontSize = 14.sp)
                    }
                }

                items(uiState.jobs, key = { it.id ?: 0 }) { job ->
                    MyTaskJobCard(job = job, onClickAction = { onNavigateToDetail(job.id) })
                }

                if (uiState.isPaginating) {
                    item {
                        Box(modifier = Modifier.fillMaxWidth().padding(16.dp), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(modifier = Modifier.size(32.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MyTaskJobCard(job: JobResponse, onClickAction: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Text(
                    text = job.title ?: "",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = Color.Black,
                    modifier = Modifier.weight(1f).padding(end = 8.dp)
                )
                TaskStatusBadge(status = job.status?.name ?: "APPLIED")
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "👤 Người thuê: ${job.creatorName ?: "Khách hàng"}",
                color = Color.Gray,
                fontSize = 13.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            val priceFormatted = DecimalFormat("#,###").format(job.price ?: 0)
            Text(
                text = "$priceFormatted đ",
                color = Color(0xFFE04F43),
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TaskBottomInfo(job = job)
                TaskActionButton(status = job.status?.name ?: "APPLIED", onClick = onClickAction)
            }
        }
    }
}