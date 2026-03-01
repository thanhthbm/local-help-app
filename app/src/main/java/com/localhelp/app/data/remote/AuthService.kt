package com.localhelp.app.data.remote

import com.localhelp.app.model.response.UserResponse
import retrofit2.Response
import retrofit2.http.Header
import retrofit2.http.POST

interface AuthService {
    @POST("/api/auth/login")
    suspend fun loginSync(@Header("Authorization") token: String): Response<UserResponse>
}