package com.localhelp.app.ui.graphnav

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.localhelp.app.ui.screens.Graph
import com.localhelp.app.ui.screens.Screen
import com.localhelp.app.ui.screens.createjob.CreateJobScreen
import com.localhelp.app.ui.screens.home.HomeScreen
import com.localhelp.app.ui.screens.messages.ChatScreen
import com.localhelp.app.ui.screens.messages.MessagesScreen
import com.localhelp.app.ui.screens.myjobs.MyJobsScreen
import java.net.URLEncoder

fun NavGraphBuilder.homeNavGraph(navController: NavController){
    navigation(
        route = Graph.HOME,
        startDestination = Screen.HOME
    ){
        composable(Screen.HOME){
            HomeScreen(
                onSearchClick = {
                    navController.navigate(Graph.SEARCH)
                },
                onDirection = { destination ->
                    navController.navigate("${Screen.MAP_DIRECTION}/${destination.latitude},${destination.longitude}")
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

        composable(Screen.MESSAGES){
            MessagesScreen(
                onNavigateToChat = { conversationId, partnerName, avatarUrl ->
                    val encodedUrl = URLEncoder.encode(avatarUrl, "UTF-8")
                    navController.navigate("chat/$conversationId/$partnerName/$encodedUrl")
                }
            )
        }

        composable(
            route = "chat/{conversationId}/{partnerName}/{partnerAvatar}",
            arguments = listOf(
                navArgument("conversationId") { type = NavType.StringType },
                navArgument("partnerName") { type = NavType.StringType },
                navArgument("partnerAvatar") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            // Các arguments đã được ViewModel tự động lấy (savedStateHandle),
            // nên ta chỉ cần truyền onBackClick
            ChatScreen(
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}