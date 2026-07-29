package com.localhelp.app.ui

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.localhelp.app.data.local.LocalUser
import com.localhelp.app.data.local.MainViewModel
import com.localhelp.app.ui.common.navigation.BottomBarWrapper
import androidx.hilt.navigation.compose.hiltViewModel
import com.localhelp.app.ui.graphnav.authNavGraph
import com.localhelp.app.ui.graphnav.homeNavGraph
import com.localhelp.app.ui.graphnav.mapNavGraph
import com.localhelp.app.ui.graphnav.profileNavGraph
import com.localhelp.app.ui.graphnav.searchNavGraph
import com.localhelp.app.ui.screens.Graph
import com.localhelp.app.ui.screens.Screen
import com.localhelp.app.ui.screens.profile.SetupProfileScreen
import com.localhelp.app.ui.screens.profile.SetupProfileViewModel
import com.localhelp.app.ui.screens.splash.SplashScreen
import kotlinx.coroutines.flow.collectLatest

/**
 * Root composable của app.
 *
 * Kết nối MainViewModel với NavHost: khi đăng nhập thành công thì lưu session,
 * khi currentUser bị xóa bởi logout thì điều hướng về graph xác thực.
 */
@Composable
fun LocalHelpApp(
    mainViewModel: MainViewModel
) {
    val navController = rememberNavController()

    val currentUser by mainViewModel.currentUser.collectAsState()

    val startDestination by mainViewModel.startDestination.collectAsState()
    val isLoading by mainViewModel.isLoading.collectAsState()

    LaunchedEffect(Unit) {
        mainViewModel.navEvent.collectLatest { route ->
            navController.navigate(route)
        }
    }

    if (isLoading) {
        SplashScreen()
        return
    }

    // Khi logout xóa currentUser, reset navigation về màn đăng nhập.
    LaunchedEffect(currentUser) {
        if (currentUser == null) {
            navController.navigate(Graph.AUTH) {
                popUpTo(0) { inclusive = true }
            }
        }
    }

    CompositionLocalProvider(
        LocalUser provides currentUser
    ) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            bottomBar = {
                BottomBarWrapper(navController)
            }
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = startDestination,
                modifier = Modifier.padding(innerPadding).fillMaxSize(),
                enterTransition = { slideInHorizontally(
                                        initialOffsetX = { it },
                                        animationSpec = spring(
                                            stiffness = Spring.StiffnessMedium,
                                            dampingRatio = Spring.DampingRatioNoBouncy
                                        )
                                    ) + fadeIn(animationSpec = tween(200)) },
                exitTransition = { slideOutHorizontally(
                                        targetOffsetX = { -it / 3 },
                                        animationSpec = spring(
                                            stiffness = Spring.StiffnessMedium,
                                            dampingRatio = Spring.DampingRatioNoBouncy
                                        )
                                    ) + fadeOut(animationSpec = tween(200)) },
                popEnterTransition = { slideInHorizontally(
                                            initialOffsetX = { -it / 3 },
                                            animationSpec = spring(
                                                stiffness = Spring.StiffnessMedium,
                                                dampingRatio = Spring.DampingRatioNoBouncy
                                            )
                                        ) + fadeIn(animationSpec = tween(200))
                                    },
                popExitTransition = { slideOutHorizontally(
                                            targetOffsetX = { it },
                                            animationSpec = spring(
                                                stiffness = Spring.StiffnessMedium,
                                                dampingRatio = Spring.DampingRatioNoBouncy
                                            )
                                        ) + fadeOut(animationSpec = tween(200))
                                    }
            ){
                authNavGraph(
                    navController = navController,
                    onUserAuthenticated = { user, token ->
                        mainViewModel.saveSession(user, token)
                    }
                )

                homeNavGraph(navController = navController)
                profileNavGraph(navController = navController)
                searchNavGraph(navController = navController)
                mapNavGraph(navController = navController)

                // Route độc lập cho trường hợp auto-login mà profile chưa setup
                composable(Screen.SETUP_PROFILE) {
                    val setupViewModel: SetupProfileViewModel = hiltViewModel()
                    SetupProfileScreen(
                        onProfileSaved = {
                            navController.navigate(Graph.HOME) {
                                popUpTo(Screen.SETUP_PROFILE) { inclusive = true }
                            }
                        },
                        onSkip = {
                            navController.navigate(Graph.HOME) {
                                popUpTo(Screen.SETUP_PROFILE) { inclusive = true }
                            }
                        },
                        viewModel = setupViewModel
                    )
                }
            }
        }
    }
}
