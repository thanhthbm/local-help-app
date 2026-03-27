package com.localhelp.app.ui.graphnav

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.localhelp.app.ui.screens.Graph
import com.localhelp.app.ui.screens.Screen
import com.localhelp.app.ui.screens.profile.ProfileScreen
import com.localhelp.app.ui.screens.profile.ProfileViewModel

fun NavGraphBuilder.profileNavGraph(navController: NavController){
    navigation(
        route = Graph.SEARCH,
        startDestination = Screen.PROFILE
    ){
        composable(Screen.PROFILE) {
            val viewModel: ProfileViewModel = hiltViewModel()
            ProfileScreen(viewModel = viewModel)
        }
    }
}