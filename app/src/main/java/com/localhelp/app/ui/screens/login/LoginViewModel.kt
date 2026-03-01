package com.localhelp.app.ui.screens.login

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.firebase.auth.FirebaseAuth
import com.localhelp.app.data.remote.NetworkModule
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    var email by mutableStateOf("")
    var password by mutableStateOf("")

    var isLoading by mutableStateOf(false)
    var loginError by mutableStateOf<String?>(null)

    fun onLoginClick(onSuccess: () -> Unit) {
        if (email.isEmpty() || password.isEmpty()){
            loginError = "Vui lòng nhập đầy đủ thông tin đăng nhập."
            return
        }

        isLoading = true
        loginError = null

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful){
                    val user = auth.currentUser
                    user?.getIdToken(true)?.addOnCompleteListener { result ->
                        val idToken = result.result?.token
                        if (idToken != null) {
                            syncWithBackend(idToken, onSuccess)
                        }
                    }
                } else {
                    isLoading = false
                    loginError = task.exception?.message?: "Đăng nhập Firebase thất bại"
                }
            }
    }

    private fun syncWithBackend(idToken: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            try {
                val response = NetworkModule.authService.loginSync("Bearer $idToken")

                if (response.isSuccessful) {
                    val userResponse = response.body()
                    if (userResponse != null) {
                        isLoading = false
                        onSuccess()
                    }
                } else {
                    isLoading = false
                    // LẤY CHI TIẾT LỖI TỪ SERVER
                    val errorBody = response.errorBody()?.string()
                    println("DEBUG_AUTH: Server Error Code: ${response.code()}")
                    println("DEBUG_AUTH: Server Error Body: $errorBody")

                    loginError = "Backend lỗi (${response.code()}): $errorBody"
                }
            } catch (e: Exception) {
                isLoading = false
                println("DEBUG_AUTH: Network Exception: ${e.message}")
                loginError = "Lỗi kết nối: ${e.localizedMessage}"
            }
        }
    }

}