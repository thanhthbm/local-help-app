package com.localhelp.app.ui.graphnav

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavArgument
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.localhelp.app.ui.screens.Graph
import com.localhelp.app.ui.screens.Screen
import com.localhelp.app.ui.screens.map.MapRoute
import com.localhelp.app.ui.screens.map.MapViewModel
import com.trackasia.android.geometry.LatLng
import kotlinx.serialization.Serializable


fun NavGraphBuilder.mapNavGraph(navController: NavController){
    navigation(
        route = Graph.MAP,
        startDestination = Screen.MAP_DIRECTION
    ){
        composable(
            route = "${Screen.MAP_DIRECTION}/{destination}",
            arguments = listOf(
                navArgument("destination"){
                    type = NavType.StringType
                }
            )
        ){
            MapRoute (
                viewModel = hiltViewModel<MapViewModel>(),
                onBackClick = {navController.popBackStack()}
            )
        }

        composable(
            route = "${Screen.MAP_TRACKING}/{userId}",
            arguments = listOf(
                navArgument("userId"){
                    type = NavType.LongType
                }
            )
        ){

        }
    }

}