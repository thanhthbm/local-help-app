package com.localhelp.app.ui.screens.profile

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.localhelp.app.ui.common.profile.FinanceButton
import com.localhelp.app.ui.common.profile.JobsSection
import com.localhelp.app.ui.common.profile.ProfileCard
import com.localhelp.app.ui.common.profile.ReviewsSection
import com.localhelp.app.ui.common.profile.StatsSection


val OrangePrimary = Color(0xFFF06A50)
val OrangeLight = Color(0xFFFFF0ED)
val GreenLight = Color(0xFFE8F8F0)
val GreenText = Color(0xFF2E9B5B)
val GrayText = Color(0xFF888888)
val GrayBackground = Color(0xFFF7F7F7)

@Composable
fun ProfileScreen(
    modifier: Modifier = Modifier,
    userId: Long? = null,
    onEditProfile: () -> Unit = {},
    onNavigateToStats: () -> Unit = {},
    onJobClick: (Long) -> Unit = {},
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val scrollState = rememberScrollState()
    val user by viewModel.user.collectAsState()
    val isMyProfile = viewModel.isMyProfile
    val jobs by viewModel.jobs.collectAsState()
    val reviews by viewModel.reviews.collectAsState()
    val isLoadingJobs by viewModel.isLoadingJobs.collectAsState()
    val isLoadingReviews by viewModel.isLoadingReviews.collectAsState()
    val isRefreshing by viewModel.isRefreshing.collectAsState()
    val refreshState = rememberPullToRefreshState()

    PullToRefreshBox(
        isRefreshing = isRefreshing,
        onRefresh = { viewModel.refresh() },
        state = refreshState,
        modifier = modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(horizontal = 16.dp)
                .verticalScroll(scrollState)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // --- TIÊU ĐỀ & NÚT ĐĂNG XUẤT ---
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    if (isMyProfile) "Hồ sơ cá nhân" else "Hồ sơ người dùng",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
                if (isMyProfile) {
                    Row {
                        IconButton(onClick = onEditProfile) {
                            Icon(
                                Icons.Filled.Edit,
                                contentDescription = "Chỉnh sửa hồ sơ",
                                tint = Color(0xFFF06A50)
                            )
                        }
                        IconButton(onClick = { viewModel.logout() }) {
                            Icon(
                                Icons.AutoMirrored.Filled.Logout,
                                contentDescription = "Đăng xuất",
                                tint = Color.Red
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // --- THẺ THÔNG TIN CÁ NHÂN ---
            ProfileCard(
                fullName = user?.fullName ?: "Người dùng",
                bio = user?.bio ?: "Chưa có thông tin giới thiệu",
                avatarUrl = user?.avatarUrl
            )

            Spacer(modifier = Modifier.height(16.dp))

            // --- THỐNG KÊ ---
            StatsSection(
                completedJobs = user?.completedJobs?.toString() ?: "0"
            )

            Spacer(modifier = Modifier.height(16.dp))
            if (isMyProfile) {
                FinanceButton(onClick = onNavigateToStats)
                Spacer(modifier = Modifier.height(24.dp))
            }

            // --- PHẦN ĐÁNH GIÁ ---
            ReviewsSection(
                averageRating = user?.averageRating ?: 0.0,
                totalReviews = user?.totalReviews ?: 0,
                reviews = reviews,
                isLoading = isLoadingReviews
            )

            Spacer(modifier = Modifier.height(24.dp))

            // --- PHẦN CÔNG VIỆC GẦN ĐÂY ---
//            if (isLoadingJobs) {
//                Box(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .height(100.dp), contentAlignment = Alignment.Center
//                ) {
//                    CircularProgressIndicator(color = OrangePrimary)
//                }
//            } else {
//                JobsSection(
//                    jobs = jobs,
//                    onJobClick = onJobClick
//                )
//            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
