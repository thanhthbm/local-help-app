package com.localhelp.app.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.viewModel
import com.localhelp.app.ui.screens.login.LoginViewModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.localhelp.app.data.local.LocalUser
import com.localhelp.app.data.local.MainViewModel
import com.localhelp.app.model.response.UserResponse
import com.localhelp.app.ui.navigation.authNavGraph
import com.localhelp.app.ui.screens.Screen

@Composable
fun LocalHelpApp(mainViewModel: MainViewModel) {
    val navController = rememberNavController()
    val loginViewModel: LoginViewModel = viewModel()
    val state by mainViewModel.userState.collectAsState()

    CompositionLocalProvider(
        LocalUser provides state.user){
        NavHost(
            navController = navController,
            startDestination = Screen.Login
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

