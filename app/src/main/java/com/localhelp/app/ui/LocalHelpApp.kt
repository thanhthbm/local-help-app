package com.localhelp.app.ui

import androidx.compose.animation.core.FastOutSlowInEasing
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
import androidx.compose.runtime.LaunchedEffect // Import thêm cái này
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.localhelp.app.data.local.LocalUser
import com.localhelp.app.data.local.MainViewModel
import com.localhelp.app.ui.common.navigation.BottomBarWrapper
import com.localhelp.app.ui.common.navigation.BottomNavigationBar
import com.localhelp.app.ui.graphnav.authNavGraph
import com.localhelp.app.ui.graphnav.homeNavGraph
import com.localhelp.app.ui.graphnav.profileNavGraph
import com.localhelp.app.ui.graphnav.searchNavGraph
import com.localhelp.app.ui.screens.Graph
import com.localhelp.app.ui.screens.Screen

@Composable
fun LocalHelpApp(
    mainViewModel: MainViewModel
) {
    val navController = rememberNavController()

    val currentUser by mainViewModel.currentUser.collectAsState()

    val startDestination by mainViewModel.startDestination.collectAsState()
    val isLoading by mainViewModel.isLoading.collectAsState()



    if (isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

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
            }
        }
    }
}