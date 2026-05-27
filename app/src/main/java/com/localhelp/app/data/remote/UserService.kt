package com.localhelp.app.data.remote

import com.localhelp.app.model.response.ApiResponse
import com.localhelp.app.model.response.UserResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.PUT
import retrofit2.http.Part

interface UserService {
    /**
     * Lấy hồ sơ của user đang đăng nhập dựa trên Firebase token trong Authorization header.
     */
    @GET("/api/users/me")
    suspend fun getProfile(): Response<ApiResponse<UserResponse>>

    /**
     * Lấy hồ sơ công khai của một user theo id.
     */
    @GET("/api/users/{id}")
    suspend fun getUserById(@retrofit2.http.Path("id") id: Long): Response<ApiResponse<UserResponse>>

    /**
     * Cập nhật hồ sơ (text + avatar tuỳ chọn) bằng multipart/form-data.
     * - [data]   : JSON của UpdateProfileRequest (có thể null nếu chỉ đổi avatar)
     * - [avatar] : file ảnh (có thể null nếu không đổi avatar)
     */
    @Multipart
    @PUT("/api/users/me")
    suspend fun updateProfile(
        @Part("data") data: RequestBody?,
        @Part avatar: MultipartBody.Part?
    ): Response<ApiResponse<UserResponse>>
}
