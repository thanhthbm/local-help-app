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
import java.util.concurrent.TimeUnit

class ForgotPasswordViewModel: ViewModel() {
    var phoneNumber by mutableStateOf("")
    var verificationId by mutableStateOf("")
    var otpCode by mutableStateOf("")
    var newPassword by mutableStateOf("")
    var confirmPassword by mutableStateOf("")

    var isLoading by mutableStateOf(false)
    var errorMsg by mutableStateOf<String?>(null)

    private val auth = FirebaseAuth.getInstance()


    fun sendOtp(activity: Activity, onSuccess: () -> Unit) {
        isLoading = true
        errorMsg = null

        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(formatPhoneNumber(phoneNumber)) // Định dạng +84
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(activity)
            .setCallbacks(object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                    // Tự động xác thực nếu Firebase nhận diện được tin nhắn (tùy dòng máy)
                    isLoading = false
                }

                override fun onVerificationFailed(e: FirebaseException) {
                    isLoading = false
                    errorMsg = e.localizedMessage
                }

                override fun onCodeSent(id: String, token: PhoneAuthProvider.ForceResendingToken) {
                    isLoading = false
                    verificationId = id
                    onSuccess()
                }
            }).build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    fun verifyOtp(onNext: () -> Unit) {
        isLoading = true

        val credential = PhoneAuthProvider.getCredential(verificationId, otpCode)

        auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                isLoading = false
                if (task.isSuccessful) {
                    onNext()
                } else {
                    errorMsg = task.exception?.localizedMessage
                }
            }
    }

    fun resetPassword(onSuccess: () -> Unit){
        if (newPassword != confirmPassword) {
            errorMsg = "Mật khẩu không khớp"
            return
        }

        if (newPassword.length < 6){
            errorMsg = "Mật khẩu phải có ít nhất 6 ký tự"
            return
        }
    }

    private fun formatPhoneNumber(phone: String): String{
        return if (phone.startsWith("0")) "+84${phone.substring(1)}" else phone
    }
}

