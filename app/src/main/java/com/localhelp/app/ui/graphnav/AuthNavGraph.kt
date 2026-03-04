package com.localhelp.app.ui.graphnav

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.localhelp.app.model.response.UserResponse
import com.localhelp.app.ui.screens.Graph
import com.localhelp.app.ui.screens.Screen
import com.localhelp.app.ui.screens.home.HomeScreen
import com.localhelp.app.ui.screens.login.LoginScreen
import com.localhelp.app.ui.screens.login.LoginViewModel
import com.localhelp.app.ui.screens.register.RegisterScreen
import com.localhelp.app.ui.screens.register.RegisterViewModel

fun NavGraphBuilder.authNavGraph(
    navController: NavController,
    onUserAuthenticated: (UserResponse) -> Unit
){

    navigation(
        route = Graph.Auth,
        startDestination = Screen.Login
    ) {
        composable(Screen.Login) {
            val loginViewModel: LoginViewModel = hiltViewModel()
            LoginScreen(
                viewModel = loginViewModel,
                onLoginSuccess = { user ->
                    onUserAuthenticated(user)
                    navController.navigate(Graph.Home) {
                        popUpTo(Graph.Auth) { inclusive = true }
                    }
                },
                onRegisterNavigate = { navController.navigate(Screen.Register) },
                onForgotPasswordClick = { navController.navigate("forgot_password_root") }
            )
        }

        composable(Screen.Register) {
            val registerViewModel: RegisterViewModel = hiltViewModel()
            RegisterScreen(
                viewModel = registerViewModel,
                onRegisterSuccess = { navController.navigate(Screen.Login) },
                onBackClick = { navController.popBackStack() }
            )
        }

        // Gọi forgotPasswordGraph (nó là 1 graph con nằm trong Graph.Auth)
        forgotPasswordGraph(navController)
    }
}