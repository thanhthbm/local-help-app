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
import com.localhelp.app.ui.screens.search.SearchDetailRoute
import com.localhelp.app.ui.screens.search.SearchDetailViewModel
import com.localhelp.app.ui.screens.search.SearchRoute
import com.localhelp.app.ui.screens.search.SearchViewModel

fun NavGraphBuilder.searchNavGraph(navController: NavController) {
    navigation(
        route = Graph.SEARCH,
        startDestination = Screen.SEARCH
    ){
        composable(Screen.SEARCH) {
            val searchViewModel = hiltViewModel<SearchViewModel>()
            SearchRoute(
                onBackClick = {navController.popBackStack()},
                viewModel = searchViewModel,
                onNavigateToSearchDetail = {keyword ->
                    navController.navigate("${Screen.SEARCH_DETAIL}/$keyword")
                }
            )
        }

        composable(
            route = "${Screen.SEARCH_DETAIL}/{keyword}",
            arguments = listOf(
                navArgument("keyword") {type = NavType.StringType}
            )
        ) { backStackEntry ->
            val keyword = backStackEntry.arguments?.getString("keyword") ?: ""

            val searchDetailViewModel = hiltViewModel<SearchDetailViewModel>()
            SearchDetailRoute (
                onBackClick = {navController.popBackStack()},
                viewModel = searchDetailViewModel,
                keyword = keyword,
                onNavigateToJobDetail = {id ->
                    navController.navigate("${Screen.JOB_DETAIL}/$id")
                }
            )
        }

        composable (
            route = "${Screen.JOB_DETAIL}/{id}",
            arguments = listOf(
                navArgument("id") {type = NavType.LongType}
            )
        ){}
    }
}