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
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.vector.ImageVector
import com.localhelp.app.ui.screens.Screen

@Immutable
sealed class BottomNavItem(
    val route: String,
    val title: String,
    val icon: ImageVector,
    val selectedIcon: ImageVector
) {
    object Home : BottomNavItem(
        route = Screen.HOME,
        title = "Khám phá",
        icon = Icons.Outlined.Home,
        selectedIcon = Icons.Filled.Home
    )

    object MyJobs : BottomNavItem(
        route = Screen.MY_JOBS,
        title = "Việc đã đăng",
        icon = Icons.AutoMirrored.Outlined.Assignment,
        selectedIcon = Icons.AutoMirrored.Filled.Assignment
    )

    object PostJob : BottomNavItem(
        route = Screen.POST_JOB,
        title = "",
        icon = Icons.Default.Add,
        selectedIcon = Icons.Default.Add
    )

    object Messages : BottomNavItem(
        route = Screen.MESSAGES,
        title = "Tin nhắn",
        icon = Icons.AutoMirrored.Outlined.Chat,
        selectedIcon = Icons.AutoMirrored.Filled.Chat
    )

    object Profile : BottomNavItem(
        route = Screen.PROFILE,
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