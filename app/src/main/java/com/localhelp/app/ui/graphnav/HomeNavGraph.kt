package com.localhelp.app.ui.graphnav

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.localhelp.app.ui.screens.Graph
import com.localhelp.app.ui.screens.Screen
import com.localhelp.app.ui.screens.home.HomeScreen

fun NavGraphBuilder.homeNavGraph(navController: NavController){
    navigation(
        route = Graph.Home,
        startDestination = Screen.Home
    ){
        composable(Screen.Home){
            HomeScreen()
        }

    }
}