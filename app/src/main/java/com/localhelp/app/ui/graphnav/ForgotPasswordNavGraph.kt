package com.localhelp.app.ui.graphnav

import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.localhelp.app.ui.screens.Screen
import com.localhelp.app.ui.screens.resetpassword.ForgotPasswordViewModel
import com.localhelp.app.ui.screens.resetpassword.NewPasswordScreen
import com.localhelp.app.ui.screens.resetpassword.OtpVerificationScreen
import com.localhelp.app.ui.screens.resetpassword.ResetPasswordScreen

fun NavGraphBuilder.forgotPasswordGraph(navController: NavController) {
    navigation(
        startDestination = Screen.RESET_PASSWORD,
        route = "forgot_password_root"
    ){
        composable(Screen.RESET_PASSWORD){ backStackEntry ->
            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry("forgot_password_root")
            }

            val vm: ForgotPasswordViewModel = viewModel(parentEntry)

            ResetPasswordScreen(
                viewModel = vm,
                onOtpSent = {navController.navigate(Screen.OTP_VERIFICATION)},
                onBack = {navController.popBackStack()},
            )
        }

        composable(Screen.OTP_VERIFICATION){ backStackEntry ->
            val parentEntry = remember(backStackEntry){
                navController.getBackStackEntry("forgot_password_root")
            }

            val vm: ForgotPasswordViewModel = viewModel(parentEntry)

            OtpVerificationScreen(
                viewModel = vm,
                onVerified = {navController.navigate(Screen.NEW_PASSWORD)}
            )
        }

        composable(Screen.NEW_PASSWORD){ backStackEntry ->
            val parentEntry = remember(backStackEntry){
                navController.getBackStackEntry("forgot_password_root")
            }

            val vm: ForgotPasswordViewModel = viewModel(parentEntry)

            NewPasswordScreen(
                viewModel = vm,
                onSuccess = {
                    navController.navigate(Screen.LOGIN){
                        popUpTo("forgot_password_root"){
                            inclusive = true
                        }
                    }
                }
            )
        }
    }
}