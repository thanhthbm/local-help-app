package com.localhelp.app.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import com.localhelp.app.ui.screens.login.LoginViewModel
import androidx.compose.runtime.getValue
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.localhelp.app.data.local.LocalUser
import com.localhelp.app.data.local.MainViewModel
import com.localhelp.app.ui.common.navigation.BottomNavigationBar
import com.localhelp.app.ui.navigation.authNavGraph
import com.localhelp.app.ui.screens.Screen

@Composable
fun LocalHelpApp(mainViewModel: MainViewModel) {
    val navController = rememberNavController()
    val loginViewModel: LoginViewModel = viewModel()
    val state by mainViewModel.userState.collectAsState()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val showBottomBar = currentDestination?.route in listOf(Screen.Home, Screen.MyJobs, Screen.Messages,
        Screen.Profile)

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
                startDestination = Screen.Login,
                modifier = androidx.compose.ui.Modifier.padding(innerPadding)
            ){
                authNavGraph(
                    navController = navController,
                    loginViewModel = loginViewModel,
                    onUserAuthenticated = { user ->
                        mainViewModel.updateUser(user)
                    }
                )
            }
        }
    }

}

