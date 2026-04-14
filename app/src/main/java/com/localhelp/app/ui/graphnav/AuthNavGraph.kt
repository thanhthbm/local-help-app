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
import com.localhelp.app.ui.screens.profile.SetupProfileScreen
import com.localhelp.app.ui.screens.profile.SetupProfileViewModel
import com.localhelp.app.ui.screens.register.RegisterScreen
import com.localhelp.app.ui.screens.register.RegisterViewModel
import com.localhelp.app.ui.graphnav.forgotPasswordGraph

/** Trả về true khi tất cả 5 trường cần thiết đều null/blank/UNKNOWN — cần setup hồ sơ */
fun UserResponse.needsProfileSetup(): Boolean =
    avatarUrl.isNullOrBlank() &&
    fullName.isNullOrBlank() &&
    phone.isNullOrBlank() &&
    (gender == null || gender == com.localhelp.app.model.constant.GenderEnum.UNKNOWN) &&
    bio.isNullOrBlank()

fun NavGraphBuilder.authNavGraph(
    navController: NavController,
    onUserAuthenticated: (UserResponse, String) -> Unit
) {
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
                    // Sử dụng flag isNew từ backend để điều hướng. isNew == false nghĩa là user mới.
                    val destination = if (user.isNew == false) {
                        Screen.SETUP_PROFILE
                    } else {
                        Graph.HOME
                    }
                    navController.navigate(destination) {
                        popUpTo(Graph.AUTH) { inclusive = true }
                    }
                },
                onRegisterNavigate = { navController.navigate(Screen.REGISTER) },
                onForgotPasswordClick = { navController.navigate("forgot_password_root") }
            )
        }

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