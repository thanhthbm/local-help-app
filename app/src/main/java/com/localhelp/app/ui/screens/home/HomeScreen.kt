package com.localhelp.app.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.localhelp.app.data.local.LocalUser
import com.localhelp.app.ui.common.home.CategorySection
import com.localhelp.app.ui.common.home.FeaturedJobsList
import com.localhelp.app.ui.common.home.HomeHeader
import com.localhelp.app.ui.common.home.RecentJobCard
import com.localhelp.app.ui.common.home.SearchBar
import com.localhelp.app.ui.common.home.SectionHeader
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment

@Composable
fun HomeScreen(
    onSearchClick : () -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val user = LocalUser.current
    val recentJobs by viewModel.recentJobs.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()


    val listState = rememberLazyListState()

    LaunchedEffect(listState) {
        snapshotFlow { listState.layoutInfo.visibleItemsInfo }
            .collect { visibleItems ->
                if (visibleItems.isNotEmpty()){
                    val lastVisibleItem = visibleItems.last()

                    if (lastVisibleItem.index >= recentJobs.size - 1){
                        viewModel.loadMoreJobs()
                    }
                }

            }
    }

    LazyColumn(
        state = listState,
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFDFDFD))
    ) {
        item {
            HomeHeader(user?.fullName ?: "Name", "Hà Nội")
        }

        item {
            SearchBar(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                onSearchClick = onSearchClick
            )
        }

        item { CategorySection() }

        item {
            SectionHeader(title = "Nổi bật quanh bạn", onSeeMore = { /* TODO */})
            FeaturedJobsList()
        }

        item {
            SectionHeader(title = "Việc mới nhất", onSeeMore = {/* TODO */})
        }

        if (isLoading) {
            item {
                Box(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Color(0xFFED7D68))
                }
            }
        } else {
            items(recentJobs) { job ->
                if (job.creatorId != user?.id){
                    RecentJobCard(job = job)
                }
            }
        }

        if (isLoading) {
            item {
                Box(modifier = Modifier.fillMaxWidth().padding(16.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Color(0xFFED7D68))
                }
            }
        }

        item { Spacer(modifier = Modifier.height(80.dp)) }
    }
}