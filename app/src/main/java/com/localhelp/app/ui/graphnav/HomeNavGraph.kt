package com.localhelp.app.ui.graphnav

import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.localhelp.app.ui.screens.Graph
import com.localhelp.app.ui.screens.Screen
import com.localhelp.app.ui.screens.createjob.CreateJobScreen
import com.localhelp.app.ui.screens.createjob.CreateJobViewModel
import com.localhelp.app.ui.screens.home.HomeScreen
import com.localhelp.app.ui.screens.jobdetail.JobDetailScreen
import com.localhelp.app.ui.screens.jobmanagement.JobDetailHelperScreen
import com.localhelp.app.ui.screens.jobmanagement.JobDetailHelperUiState
import com.localhelp.app.ui.screens.jobmanagement.JobDetailHelperViewModel
import com.localhelp.app.ui.screens.jobmanagement.JobDetailOwnerScreen
import com.localhelp.app.ui.screens.jobmanagement.JobDetailOwnerViewModel
import com.localhelp.app.ui.screens.messages.ChatScreen
import com.localhelp.app.ui.screens.messages.MessagesScreen
import com.localhelp.app.ui.screens.map.SelectLocationScreen
import com.localhelp.app.ui.screens.jobmanagement.JobManagementScreen
import com.localhelp.app.ui.screens.jobmanagement.MyPostsViewModel
import com.localhelp.app.ui.screens.jobmanagement.MyTasksViewModel
import com.localhelp.app.ui.screens.myjobs.JobAcceptSuccessScreen
import dagger.hilt.android.lifecycle.HiltViewModel
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
                },
                onNavigateToChat = { conversationId, partnerName, avatarUrl, partnerId ->
                    val encodedUrl = if (avatarUrl != null) URLEncoder.encode(avatarUrl, "UTF-8") else "none"
                    navController.navigate("chat/$conversationId/$partnerName/$encodedUrl/$partnerId")
                },
                onJobClick = { jobId ->
                    navController.navigate("${Screen.JOB_DETAIL}/$jobId")
                }
            )
        }

        composable(
            route = "${Screen.POST_JOB}?jobId={jobId}",
            arguments = listOf(navArgument("jobId") { nullable = true })
        ) { backStackEntry ->
            val viewModel: CreateJobViewModel = hiltViewModel()

            // Handle location result from SelectLocationScreen
            val selectedLat = backStackEntry.savedStateHandle.get<Double>("lat")
            val selectedLng = backStackEntry.savedStateHandle.get<Double>("lng")
            val selectedAddr = backStackEntry.savedStateHandle.get<String>("address")

            LaunchedEffect(selectedLat, selectedLng, selectedAddr) {
                if (selectedLat != null && selectedLng != null && selectedAddr != null) {
                    viewModel.setLocation(selectedLat, selectedLng, selectedAddr)
                }
            }

            CreateJobScreen(
                onBackClick = { navController.popBackStack() },
                onJobCreated = { navController.popBackStack() },
                onSelectLocation = { lat, lng ->
                    navController.navigate("${Screen.SELECT_LOCATION}/$lat/$lng")
                },
                viewModel = viewModel
            )
        }

        composable(
            route = "${Screen.SELECT_LOCATION}/{lat}/{lng}",
            arguments = listOf(
                navArgument("lat") { type = NavType.FloatType },
                navArgument("lng") { type = NavType.FloatType }
            )
        ) { backStackEntry ->
            val lat = backStackEntry.arguments?.getFloat("lat")?.toDouble() ?: 20.9800
            val lng = backStackEntry.arguments?.getFloat("lng")?.toDouble() ?: 105.7950

            SelectLocationScreen(
                initialLat = lat,
                initialLng = lng,
                onBack = { navController.popBackStack() },
                onLocationConfirmed = { selectedLat, selectedLng, address ->
                    navController.previousBackStackEntry?.savedStateHandle?.set("lat", selectedLat)
                    navController.previousBackStackEntry?.savedStateHandle?.set("lng", selectedLng)
                    navController.previousBackStackEntry?.savedStateHandle?.set("address", address)
                    navController.popBackStack()
                }
            )
        }

        composable(
            route = "${Screen.JOB_DETAIL}/{id}",
            arguments = listOf(navArgument("id") { type = NavType.LongType })
        ) {
            JobDetailScreen(
                onBackClick = { navController.popBackStack() },
                onMessageClick = { conversationId: String, partnerName: String, avatarUrl: String?, partnerId: Long ->
                    val encodedUrl = if (avatarUrl != null) URLEncoder.encode(avatarUrl, "UTF-8") else "none"
                    navController.navigate("chat/$conversationId/$partnerName/$encodedUrl/$partnerId")
                },
                onEditJob = { jobId ->
                    navController.navigate("${Screen.POST_JOB}?jobId=$jobId")
                },
                onUserClick = { userId ->
                    navController.navigate("${Screen.PROFILE}/$userId")
                },
                onJobSuccessCallBack = { navController.navigate(Screen.SUCCESS_SCREEN) }
            )
        }

        composable(
            route = Screen.SUCCESS_SCREEN
        ) {
            JobAcceptSuccessScreen(
                onNavigateHome = {
                    navController.navigate(Graph.HOME){
                        popUpTo(navController.graph.id) {
                            inclusive = true
                        }
                    }
                }
            )
        }

        composable(Screen.MY_JOBS){
            JobManagementScreen(
                myPostsViewModel = hiltViewModel<MyPostsViewModel>(),
                myTasksViewModel = hiltViewModel<MyTasksViewModel>(),
                navigateToHelperDetail = { id ->
                    navController.navigate("${Screen.HELPER_JOB_DETAIL_MANAGEMENT}/$id")
                },
                navigateToOwnerDetail = { id ->
                    navController.navigate("${Screen.OWNER_JOB_DETAIL_MANAGEMENT}/$id")
                }
            )
        }

        composable(
            route = "${Screen.HELPER_JOB_DETAIL_MANAGEMENT}/{id}",
            arguments = listOf(
                navArgument("id") {type = NavType.LongType}
            )
        ){
            JobDetailHelperScreen(
                viewModel = hiltViewModel<JobDetailHelperViewModel>(),
                onNavigateBack = { navController.popBackStack() },
                onNavigateToUserProfile = { userId ->
                    navController.navigate("${Screen.PROFILE}/$userId")
                },
                onOpenGoogleMaps = { lat, lng ->
                    navController.navigate("${Screen.MAP_DIRECTION}/$lat,$lng")
                },
                onNavigateToChat = { conversationId: String?, partnerName: String, avatarUrl: String?, partnerId: Long ->
                    val encodedUrl = if (avatarUrl != null) URLEncoder.encode(avatarUrl, "UTF-8") else "none"
                    navController.navigate("chat/$conversationId/$partnerName/$encodedUrl/$partnerId")
                    navController.navigate("chat/$conversationId/$partnerName/$encodedUrl")
                },
                onNavigateToJobDetail = { jobId ->
                    navController.navigate("${Screen.JOB_DETAIL}/$jobId")
                }
            )
        }

        composable(
            route = "${Screen.OWNER_JOB_DETAIL_MANAGEMENT}/{id}",
            arguments = listOf(
                navArgument("id") {type = NavType.LongType}
            )
        ){
            JobDetailOwnerScreen(
                viewModel = hiltViewModel<JobDetailOwnerViewModel>(),
                onNavigateBack = { navController.popBackStack() },
                onNavigateToUserProfile = { userId ->
                    navController.navigate("${Screen.PROFILE}/$userId")
                },
                onNavigateToChat = { conversationId: String, partnerName: String, avatarUrl: String?, partnerId: Long ->
                    val encodedUrl = if (avatarUrl != null) URLEncoder.encode(avatarUrl, "UTF-8") else "none"
                    navController.navigate("chat/$conversationId/$partnerName/$encodedUrl/$partnerId")
                    navController.navigate("chat/$conversationId/$partnerName/$encodedUrl")
                },
                onNavigateToJobDetail = { jobId ->
                    navController.navigate("${Screen.JOB_DETAIL}/$jobId")
                }
            )
        }

        composable(Screen.MESSAGES){
            MessagesScreen(
                onNavigateToChat = { conversationId: String, partnerName: String, avatarUrl: String?, partnerId: Long ->
                    val encodedUrl = if (avatarUrl != null) URLEncoder.encode(avatarUrl, "UTF-8") else "none"
                    navController.navigate("chat/$conversationId/$partnerName/$encodedUrl/$partnerId")
                }
            )
        }

        composable(
            route = "chat/{conversationId}/{partnerName}/{partnerAvatar}/{partnerId}",
            arguments = listOf(
                navArgument("conversationId") { type = NavType.StringType },
                navArgument("partnerName") { type = NavType.StringType },
                navArgument("partnerAvatar") { type = NavType.StringType },
                navArgument("partnerId") { type = NavType.LongType }
            )
        ) { backStackEntry ->
            ChatScreen(
                onBackClick = { navController.popBackStack() },
                onNavigateToProfile = { userId ->
                    navController.navigate("${Screen.PROFILE}/$userId")
                }
            )
        }
    }
}