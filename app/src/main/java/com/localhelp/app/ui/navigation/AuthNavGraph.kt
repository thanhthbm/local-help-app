package com.localhelp.app.ui.navigation

import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.localhelp.app.model.response.UserResponse
import com.localhelp.app.ui.screens.Screen
import com.localhelp.app.ui.screens.home.HomeScreen
import com.localhelp.app.ui.screens.login.LoginScreen
import com.localhelp.app.ui.screens.login.LoginViewModel
import com.localhelp.app.ui.screens.register.RegisterScreen
import com.localhelp.app.ui.screens.register.RegisterViewModel

fun NavGraphBuilder.authNavGraph(
    navController: NavController,
    loginViewModel: LoginViewModel,
    onUserAuthenticated: (UserResponse) -> Unit
){
    composable(Screen.Login){
        LoginScreen(
            viewModel = loginViewModel,
            onLoginSuccess = { user ->
                onUserAuthenticated(user)
                navController.navigate(Screen.Home){
                    popUpTo(Screen.Login) { inclusive = true }
                }
            },
            onRegisterNavigate = {
                navController.navigate(Screen.Register)
            },
            onForgotPasswordClick = {
                navController.navigate("forgot_password_root")
            }
        )
    }

    composable(Screen.Register) {
        val registerViewModel: RegisterViewModel = viewModel()
        RegisterScreen(
            viewModel = registerViewModel,
            onRegisterSuccess = {
                navController.popBackStack()
            },
            onBackClick = {
                navController.popBackStack()
            }
        )
    }

    composable(Screen.Home) {
        HomeScreen()

    }

    forgotPasswordGraph(navController)
}