package com.localhelp.app.data.repository

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.localhelp.app.data.remote.AuthService
import com.localhelp.app.model.response.UserResponse
import javax.inject.Inject

class AuthRepository @Inject constructor(
    private val authService: AuthService
) {
    private val firebaseAuth = FirebaseAuth.getInstance()

    fun loginFirebase(
        email: String,
        password: String,
        onResult: (Result<String>) -> Unit
    ) {
        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    firebaseAuth.currentUser?.getIdToken(true)?.addOnCompleteListener { tokenTask ->
                        val token = tokenTask.result?.token
                        if (token != null) {
                            onResult(Result.success(token))
                        } else {
                            onResult(Result.failure(Exception("Không lấy được Firebase Token")))
                        }
                    }
                } else {
                    onResult(Result.failure(task.exception ?: Exception("Đăng nhập Firebase thất bại")))
                }
            }
    }

    suspend fun syncWithBackend(token: String): Result<UserResponse> {
        return try {
            val response = authService.loginSync("Bearer $token")
            Log.d("Debug", response.body().toString() ?: "" )
            if (response.isSuccessful && response.body() != null) {
                val apiResponse = response.body()!!

                if (apiResponse.data != null) {
                    // IN RA XEM RETROFIT CÓ PARSE ĐÚNG DỮ LIỆU KHÔNG
                    Log.d("DEBUG_USER", "Retrofit bóc data thành công: ${apiResponse.data}")
                    Result.success(apiResponse.data)
                } else {
                    Log.e("DEBUG_USER", "API trả về 200 OK nhưng cục 'data' bị rỗng!")
                    Result.failure(Exception(apiResponse.message?.toString() ?: "Dữ liệu user rỗng"))
                }
            } else {
                Log.e("DEBUG_USER", "Lỗi Backend: ${response.code()} - ${response.errorBody()?.string()}")
                Result.failure(Exception("Lỗi backend (${response.code()})"))
            }
        } catch (e: Exception) {
            Log.e("DEBUG_USER", "Lỗi Crash/Network: ${e.message}", e)
            Result.failure(e)
        }
    }

    fun registerFirebase(
        email: String,
        password: String,
        onResult: (Result<Unit>) -> Unit
    ) {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                onResult(Result.success(Unit))
            }
            .addOnFailureListener { exception ->
                onResult(Result.failure(exception))
            }
    }
}