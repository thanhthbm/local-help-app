package com.localhelp.app.data.repository

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
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                val errorBody = response.errorBody()?.string()
                Result.failure(Exception("Lỗi backend (${response.code()}): $errorBody"))
            }
        } catch (e: Exception) {
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