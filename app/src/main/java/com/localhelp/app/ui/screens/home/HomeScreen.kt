package com.localhelp.app.ui.screens.home

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import com.localhelp.app.data.local.LocalUser
import com.localhelp.app.ui.common.home.CategorySection
import com.localhelp.app.ui.common.home.FeaturedJobsList
import com.localhelp.app.ui.common.home.HomeHeader
import com.localhelp.app.ui.common.home.RecentJobCard
import com.localhelp.app.ui.common.home.SearchBar
import com.localhelp.app.ui.common.home.SectionHeader
import com.trackasia.android.geometry.LatLng

@Composable
fun HomeScreen(
    onDirection : (destination :LatLng) -> Unit,
    onSearchClick : () -> Unit,
    onNavigateToChat: (String, String, String?) -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val user = LocalUser.current
    val recentJobs by viewModel.recentJobs.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val currentLocation by viewModel.currentLocation.collectAsState()
    val currentAddress by viewModel.currentAddress.collectAsState()

    val context = LocalContext.current
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
            permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true) {
            viewModel.loadCurrentLocation()
        }
    }

    LaunchedEffect(Unit) {
        val hasFineLocation = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        val hasCoarseLocation = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED

        if (!hasFineLocation && !hasCoarseLocation) {
            permissionLauncher.launch(arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ))
        } else {
            viewModel.loadCurrentLocation()
        }

        viewModel.navigateToChat.collect { conversation ->
            onNavigateToChat(
                conversation.id,
                conversation.partner.fullName ?: "User",
                conversation.partner.avatarUrl
            )
        }
    }

    val listState = rememberLazyListState()
    val shouldLoadMore by remember {
        derivedStateOf {
            val lastVisibleItem = listState.layoutInfo.visibleItemsInfo.lastOrNull()
            lastVisibleItem != null && lastVisibleItem.index >= listState.layoutInfo.totalItemsCount - 2
        }
    }

    LaunchedEffect(shouldLoadMore) {
        if (shouldLoadMore && !isLoading) {
            viewModel.loadMoreJobs()
        }
    }

    LazyColumn(
        state = listState,
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFDFDFD))
    ) {
        item {
            Button(
                onClick = { viewModel.startChatWithUser(6L) },
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
            ) {
                Text("Test Chat with User 6")
            }
        }

        item {
            Button(
                onClick = { onDirection(LatLng(21.0285, 105.8542)) },
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
            ) {
                Text("Check Map Direction")
            }
        }

        item {
            HomeHeader(user?.fullName ?: "Tài khoản", currentAddress)
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

        // Optimized List: Always show items, append loader at bottom
        items(
            items = recentJobs,
            key = { it.id } // CRITICAL for performance
        ) { job ->
            if (job.creatorId != user?.id){
                RecentJobCard(job = job)
            }
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
        }

        item { Spacer(modifier = Modifier.height(80.dp)) }
    }
}
