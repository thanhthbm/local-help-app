package com.localhelp.app.ui.graphnav

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.localhelp.app.ui.screens.Graph
import com.localhelp.app.ui.screens.Screen
import com.localhelp.app.ui.screens.createjob.CreateJobScreen
import com.localhelp.app.ui.screens.home.HomeScreen
import com.localhelp.app.ui.screens.myjobs.MyJobsScreen

fun NavGraphBuilder.homeNavGraph(navController: NavController){
    navigation(
        route = Graph.HOME,
        startDestination = Screen.HOME
    ){
        composable(Screen.HOME){
            HomeScreen(
                onSearchClick = {
                    navController.navigate(Graph.SEARCH)
                }
            )
        }

        composable  (Screen.POST_JOB ){
            CreateJobScreen(
                onBackClick = {navController.popBackStack()},
                onJobCreated = {navController.popBackStack()}
            )
        }

        composable ( Screen.MY_JOBS ){
            MyJobsScreen()
        }
    }
}