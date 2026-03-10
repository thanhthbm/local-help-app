package com.localhelp.app.ui.common.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState

@Composable
fun BottomNavigationBar(navController: NavController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    NavigationBar(
        containerColor = Color.White,
        tonalElevation = 8.dp
    ) {
        bottomNavItems.forEach { item ->
            val isSelected = currentDestination?.hierarchy?.any{ it.route == item.route } == true

            // Nếu là nút Thêm việc (+) -> Vẽ nút tròn màu cam
            if (item == BottomNavItem.PostJob) {
                Box(
                    modifier = Modifier.weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    FloatingActionButton(
                        onClick = {
                            // Điều hướng thẳng, không lưu state vì đây là màn hình tạo mới
                            navController.navigate(item.route)
                        },
                        containerColor = Color(0xFFF06A50), // Màu cam chuẩn theo thiết kế
                        shape = CircleShape,
                        modifier = Modifier.size(50.dp),
                        elevation = FloatingActionButtonDefaults.elevation(0.dp) // Tắt bóng đổ cho phẳng
                    ) {
                        Icon(
                            imageVector = item.icon,
                            contentDescription = "Đăng việc mới",
                            tint = Color.White
                        )
                    }
                }
            }
            else {
                NavigationBarItem(
                    icon = {
                        Icon(
                            imageVector = if (isSelected) item.selectedIcon else item.icon,
                            contentDescription = item.title,
                            tint = if (isSelected) Color(0xFFED7D68) else Color.Gray
                        )
                    },
                    label = {
                        if (item.title.isNotEmpty()) {
                            Text(text = item.title, fontSize = 9.sp)
                        }
                    },
                    selected = isSelected,
                    onClick = {
                        navController.navigate(item.route) {
                            popUpTo(navController.graph.findStartDestination().id){
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    colors = NavigationBarItemDefaults.colors(
                        indicatorColor = Color.Transparent
                    )
                )
            }
        }
    }
}