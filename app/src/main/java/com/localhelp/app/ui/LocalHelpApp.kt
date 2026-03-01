package com.localhelp.app.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.viewModel
import com.localhelp.app.ui.screens.login.LoginViewModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.localhelp.app.model.response.UserResponse
import com.localhelp.app.ui.navigation.authNavGraph
import com.localhelp.app.ui.screens.Screen

@Composable
fun LocalHelpApp() {
    val navController = rememberNavController()
    val loginViewModel: LoginViewModel = viewModel()
    var currentUser by remember { mutableStateOf<UserResponse?>(null) }

    NavHost(
        navController = navController,
        startDestination = Screen.Login
    ){
        authNavGraph(
            navController = navController,
            loginViewModel = loginViewModel,
            onUserAuthenticated = { user ->
                currentUser = user
            }
        )
    }
}

