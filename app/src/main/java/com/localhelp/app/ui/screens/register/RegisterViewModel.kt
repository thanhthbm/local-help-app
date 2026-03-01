package com.localhelp.app.ui.screens.register

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import com.google.firebase.auth.FirebaseAuth


class RegisterViewModel: ViewModel() {
    var email by mutableStateOf("")
    var password by mutableStateOf("")
    var confirmPassword by mutableStateOf("")
    var isLoading by mutableStateOf(false)
    var errorMsg by mutableStateOf<String?>(null)

    fun onRegisterClick(onSuccess: () -> Unit){
        if (password != confirmPassword){
            errorMsg = "Mật khẩu không khớp"
            return
        }

        if (password.length < 6){
            errorMsg = "Mật khẩu phải có ít nhất 6 ký tự"
            return
        }

        isLoading = true

        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                isLoading = false
                onSuccess()
            }

            .addOnFailureListener {
                isLoading = false
                errorMsg = it.localizedMessage ?: "Đăng ký thất bại"
            }

    }

}