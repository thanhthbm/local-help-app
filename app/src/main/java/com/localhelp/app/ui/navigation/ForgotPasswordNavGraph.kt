package com.localhelp.app.ui.navigation

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
        startDestination = Screen.ResetPassword,
        route = "forgot_password_root"
    ){
        composable(Screen.ResetPassword){ backStackEntry ->
            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry("forgot_password_root")
            }

            val vm: ForgotPasswordViewModel = viewModel(parentEntry)

            ResetPasswordScreen(
                viewModel = vm,
                onOtpSent = {navController.navigate(Screen.OtpVerification)},
                onBack = {navController.popBackStack()},
            )
        }

        composable(Screen.OtpVerification){ backStackEntry ->
            val parentEntry = remember(backStackEntry){
                navController.getBackStackEntry("forgot_password_root")
            }

            val vm: ForgotPasswordViewModel = viewModel(parentEntry)

            OtpVerificationScreen(
                viewModel = vm,
                onVerified = {navController.navigate(Screen.NewPassword)}
            )
        }

        composable(Screen.NewPassword){ backStackEntry ->
            val parentEntry = remember(backStackEntry){
                navController.getBackStackEntry("forgot_password_root")
            }

            val vm: ForgotPasswordViewModel = viewModel(parentEntry)

            NewPasswordScreen(
                viewModel = vm,
                onSuccess = {
                    navController.navigate(Screen.Login){
                        popUpTo("forgot_password_root"){
                            inclusive = true
                        }
                    }
                }
            )
        }
    }
}