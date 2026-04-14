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
import com.localhelp.app.ui.screens.profile.EditProfileScreen
import com.localhelp.app.ui.screens.profile.EditProfileViewModel
import com.localhelp.app.ui.screens.profile.ProfileScreen

fun NavGraphBuilder.profileNavGraph(navController: NavController) {
    navigation(
        route = Graph.PROFILE,
        startDestination = Screen.PROFILE
    ) {
        composable(Screen.PROFILE) {
            ProfileScreen(
                onEditProfile = { navController.navigate(Screen.EDIT_PROFILE) }
            )
        }
        composable(
            route = "${Screen.PROFILE}/{userId}",
            arguments = listOf(navArgument("userId") { type = NavType.LongType })
        ) {
            ProfileScreen(
                onEditProfile = { navController.navigate(Screen.EDIT_PROFILE) }
            )
        }
        composable(Screen.EDIT_PROFILE) {
            val viewModel: EditProfileViewModel = hiltViewModel()
            EditProfileScreen(
                onNavigateBack = { navController.popBackStack() },
                viewModel = viewModel
            )
        }
    }
}