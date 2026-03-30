package com.localhelp.app.ui.screens.resetpassword

import android.app.Activity
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.localhelp.app.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltViewModel
class ForgotPasswordViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {
    var email by mutableStateOf("")
    var newPassword by mutableStateOf("")
    var confirmPassword by mutableStateOf("")
    var isLoading by mutableStateOf(false)
    var errorMsg by mutableStateOf<String?>(null)

    private var oobCode: String? = null

    fun setOobCode(code: String){
        oobCode = code
    }

    fun sendResetEmail(onResult: (Boolean) -> Unit){
        isLoading = true

        authRepository.sendResetEmail(email){ success, error ->
            isLoading = false

            if (success){
                onResult(true)
            } else {
                errorMsg = error
                onResult(false)
            }

        }
    }

    fun resetPassword(onSuccess: () -> Unit){
        val code = oobCode ?: return

        if (newPassword != confirmPassword){
            errorMsg = "Mật khẩu không khớp"
            return
        }

        isLoading = true

        FirebaseAuth.getInstance()
            .confirmPasswordReset(code, newPassword)
            .addOnCompleteListener { task ->
                isLoading = false

                if (task.isSuccessful){
                    onSuccess()
                } else {
                    errorMsg = task.exception?.message
                }
            }
    }


}

