package com.localhelp.app.ui.graphnav

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.localhelp.app.ui.screens.Graph
import com.localhelp.app.ui.screens.Screen
import com.localhelp.app.ui.screens.profile.CategoryDetailScreen
import com.localhelp.app.ui.screens.profile.EditProfileScreen
import com.localhelp.app.ui.screens.profile.EditProfileViewModel
import com.localhelp.app.ui.screens.profile.FinancialStatsScreen
import com.localhelp.app.ui.screens.profile.ProfileScreen
import com.localhelp.app.ui.screens.profile.TransactionDetailScreen

fun NavGraphBuilder.profileNavGraph(navController: NavController) {
    // Nhóm route hồ sơ: hồ sơ cá nhân, chỉnh sửa hồ sơ, thống kê và chi tiết thống kê.
    navigation(
        route = Graph.PROFILE,
        startDestination = Screen.PROFILE
    ) {
        composable(Screen.PROFILE) {
            ProfileScreen(
                onEditProfile = { navController.navigate(Screen.EDIT_PROFILE) },
                onNavigateToStats = { navController.navigate(Screen.FINANCIAL_STATS) }
            )
        }
        composable(
            route = "${Screen.PROFILE}/{userId}",
            arguments = listOf(navArgument("userId") { type = NavType.LongType })
        ) {
            ProfileScreen(
                onEditProfile = { navController.navigate(Screen.EDIT_PROFILE) },
                onNavigateToStats = { navController.navigate(Screen.FINANCIAL_STATS) }
            )
        }
        composable(Screen.EDIT_PROFILE) {
            val viewModel: EditProfileViewModel = hiltViewModel()
            EditProfileScreen(
                onNavigateBack = { navController.popBackStack() },
                viewModel = viewModel
            )
        }

        // ── Financial statistics screens ────────────────────────────────────
        // Màn tổng quan thống kê thu/chi.
        composable(Screen.FINANCIAL_STATS) {
            FinancialStatsScreen(
                onNavigateBack = { navController.popBackStack() },
                onCategoryClick = { categoryId, isEarning, month, year ->
                    navController.navigate("${Screen.CATEGORY_DETAIL}/$categoryId/$isEarning/$month/$year")
                },
                onTransactionClick = { transactionId, isEarning ->
                    navController.navigate("${Screen.TRANSACTION_DETAIL}/$transactionId/$isEarning")
                }
            )
        }

        // Màn chi tiết một danh mục thống kê theo tháng.
        composable(
            route = "${Screen.CATEGORY_DETAIL}/{categoryId}/{isEarning}/{month}/{year}",
            arguments = listOf(
                navArgument("categoryId") { type = NavType.IntType },
                navArgument("isEarning") { type = NavType.BoolType },
                navArgument("month") { type = NavType.IntType },
                navArgument("year") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val categoryId = backStackEntry.arguments?.getInt("categoryId") ?: 1
            val isEarning = backStackEntry.arguments?.getBoolean("isEarning") ?: false
            val month = backStackEntry.arguments?.getInt("month") ?: 10
            val year = backStackEntry.arguments?.getInt("year") ?: 2023
            CategoryDetailScreen(
                categoryId = categoryId,
                isEarning = isEarning,
                month = month,
                year = year,
                onNavigateBack = { navController.popBackStack() },
                onTransactionClick = { transactionId ->
                    navController.navigate("${Screen.TRANSACTION_DETAIL}/$transactionId/$isEarning")
                }
            )
        }

        // Màn chi tiết một giao dịch; transactionId tương ứng với jobId.
        composable(
            route = "${Screen.TRANSACTION_DETAIL}/{transactionId}/{isEarning}",
            arguments = listOf(
                navArgument("transactionId") { type = NavType.IntType },
                navArgument("isEarning") { type = NavType.BoolType }
            )
        ) { backStackEntry ->
            val transactionId = backStackEntry.arguments?.getInt("transactionId") ?: 1
            val isEarning = backStackEntry.arguments?.getBoolean("isEarning") ?: false
            TransactionDetailScreen(
                transactionId = transactionId,
                isEarning = isEarning,
                onNavigateBack = { navController.popBackStack() },
                onViewProfile = { userId -> navController.navigate("${Screen.PROFILE}/$userId") },
                onViewMap = { lat, lng -> navController.navigate("${Screen.MAP_DIRECTION}/$lat,$lng") }
            )
        }
    }
}
