package com.localhelp.app.ui.graphnav

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.localhelp.app.model.response.UserResponse
import com.localhelp.app.ui.screens.Graph
import com.localhelp.app.ui.screens.Screen
import com.localhelp.app.ui.screens.login.LoginScreen
import com.localhelp.app.ui.screens.login.LoginViewModel
import com.localhelp.app.ui.screens.register.RegisterScreen
import com.localhelp.app.ui.screens.register.RegisterViewModel
import com.localhelp.app.ui.graphnav.forgotPasswordGraph

fun NavGraphBuilder.authNavGraph(
    navController: NavController,
    onUserAuthenticated: (UserResponse, String) -> Unit
){
    navigation(
        route = Graph.AUTH,
        startDestination = Screen.LOGIN
    ) {
        composable(Screen.LOGIN) {
            val loginViewModel: LoginViewModel = hiltViewModel()
            LoginScreen(
                viewModel = loginViewModel,
                onLoginSuccess = { user: UserResponse, token: String ->
                    onUserAuthenticated(user, token)
                    navController.navigate(Graph.HOME) {
                        popUpTo(Graph.AUTH) { inclusive = true }
                    }
                },
                onRegisterNavigate = { navController.navigate(Screen.REGISTER) },
                onForgotPasswordClick = { navController.navigate("forgot_password_root") }
            )
        }

        composable(Screen.REGISTER) {
            val registerViewModel: RegisterViewModel = hiltViewModel()
            RegisterScreen(
                viewModel = registerViewModel,
                onRegisterSuccess = { navController.navigate(Screen.LOGIN) },
                onBackClick = { navController.popBackStack() }
            )
        }

        // Gọi forgotPasswordGraph (nó là 1 graph con nằm trong Graph.Auth)
        forgotPasswordGraph(navController)
    }
}