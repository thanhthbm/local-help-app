package com.localhelp.app.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.localhelp.app.data.local.LocalUser
import com.localhelp.app.data.local.MainViewModel
import com.localhelp.app.ui.common.navigation.BottomNavigationBar
import com.localhelp.app.ui.graphnav.authNavGraph
import com.localhelp.app.ui.graphnav.homeNavGraph
import com.localhelp.app.ui.screens.Graph
import com.localhelp.app.ui.screens.Screen

@Composable
fun LocalHelpApp(mainViewModel: MainViewModel) {
    val navController = rememberNavController()
    val state by mainViewModel.userState.collectAsState()

    val startDestination by mainViewModel.startDestination.collectAsState()
    val isLoading by mainViewModel.isLoading.collectAsState()

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val showBottomBar = currentDestination?.route in listOf(
        Screen.Home, Screen.MyJobs, Screen.Messages, Screen.Profile
    )

    if (isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    CompositionLocalProvider(
        LocalUser provides state.user){
        Scaffold(
            bottomBar = {
                if (showBottomBar){
                    BottomNavigationBar(navController)
                }
            }
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = startDestination,
                modifier = Modifier.padding(innerPadding)
            ){
                authNavGraph(
                    navController = navController,
                    onUserAuthenticated = { user ->
                        mainViewModel.updateUser(user)
                    }
                )

                homeNavGraph(navController = navController)
            }
        }
    }

}

