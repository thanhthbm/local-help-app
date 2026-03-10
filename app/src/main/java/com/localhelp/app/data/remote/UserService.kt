package com.localhelp.app.data.remote

import com.localhelp.app.model.response.ApiResponse
import com.localhelp.app.model.response.JobResponse
import com.localhelp.app.model.response.UserResponse
import retrofit2.Response
import retrofit2.http.GET

interface UserService {
    @GET("/api/users/me")
    suspend fun getProfile(): Response<ApiResponse<UserResponse>>
}