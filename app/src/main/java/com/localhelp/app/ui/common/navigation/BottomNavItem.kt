package com.localhelp.app.ui.common.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.automirrored.outlined.Assignment
import androidx.compose.material.icons.automirrored.outlined.Chat
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.ui.graphics.vector.ImageVector
import com.localhelp.app.ui.screens.Screen

sealed class BottomNavItem(
    val route: String,
    val title: String,
    val icon: ImageVector,
    val selectedIcon: ImageVector
) {
    object Home : BottomNavItem(
        route = Screen.Home,
        title = "Khám phá",
        icon = Icons.Outlined.Home,
        selectedIcon = Icons.Filled.Home
    )

    object MyJobs : BottomNavItem(
        route = Screen.PostJob,
        title = "Việc đã đăng",
        icon = Icons.AutoMirrored.Outlined.Assignment,
        selectedIcon = Icons.AutoMirrored.Filled.Assignment
    )

    object PostJob : BottomNavItem(
        route = Screen.PostJob,
        title = "",
        icon = Icons.Default.Add,
        selectedIcon = Icons.Default.Add
    )

    object Messages : BottomNavItem(
        route = Screen.Messages,
        title = "Tin nhắn",
        icon = Icons.AutoMirrored.Outlined.Chat,
        selectedIcon = Icons.AutoMirrored.Filled.Chat
    )

    object Profile : BottomNavItem(
        route = Screen.Profile,
        title = "Cá nhân",
        icon = Icons.Outlined.Person,
        selectedIcon = Icons.Filled.Person
    )
}

val bottomNavItems = listOf(
    BottomNavItem.Home,
    BottomNavItem.MyJobs,
    BottomNavItem.PostJob,
    BottomNavItem.Messages,
    BottomNavItem.Profile
)