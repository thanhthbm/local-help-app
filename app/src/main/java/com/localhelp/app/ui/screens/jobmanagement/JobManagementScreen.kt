package com.localhelp.app.ui.screens.jobmanagement

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun JobManagementScreen(
    myPostsViewModel: MyPostsViewModel,
    myTasksViewModel: MyTasksViewModel,
    navigateToHelperDetail: (Long) -> Unit,
    navigateToOwnerDetail: (Long) -> Unit
) {
    var selectedTabIndex by rememberSaveable { mutableIntStateOf(0) }
    val tabs = listOf("Việc đã đăng", "Việc đã nhận")

    Column(modifier = Modifier.fillMaxSize().background(Color.White)) {

        Text(
            text = "Quản lý công việc",
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 24.dp, bottom = 16.dp),
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            color = Color.Black
        )

        TabRow(
            selectedTabIndex = selectedTabIndex,
            containerColor = Color.White,
            contentColor = Color(0xFFE04F43),
            indicator = { tabPositions ->
                TabRowDefaults.Indicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                    color = Color(0xFFE04F43),
                    height = 2.dp
                )
            }
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTabIndex == index,
                    onClick = { selectedTabIndex = index },
                    text = {
                        Text(
                            text = title,
                            color = if (selectedTabIndex == index) Color(0xFFE04F43) else Color.Gray,
                            fontWeight = if (selectedTabIndex == index) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 15.sp
                        )
                    }
                )
            }
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF9FAFB))
        ) {
            when (selectedTabIndex) {
                0 -> MyPostsScreen(
                    viewModel = myPostsViewModel,
                    onNavigateToDetail = navigateToOwnerDetail)
                1 -> MyTasksScreen(
                    viewModel = myTasksViewModel,
                    onNavigateToDetail = navigateToHelperDetail)
            }
        }
    }
}