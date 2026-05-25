package com.localhelp.app.data.remote

import com.localhelp.app.model.response.ApiResponse
import com.localhelp.app.model.response.UserResponse
import com.localhelp.app.model.response.VerifyOtpResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query

interface AuthService {
    @POST("/api/auth/login")
    suspend fun loginSync(@Header("Authorization") token: String): Response<ApiResponse<UserResponse>>

    // API gửi OTP đến email để bắt đầu luồng đổi mật khẩu.
    @POST("/api/auth/forgot-password/send-otp")
    suspend fun sendOtp(@Query("email") email: String): Response<ApiResponse<Unit>>

    // API xác thực OTP và trả về resetToken cho bước đặt mật khẩu mới.
    @POST("/api/auth/forgot-password/verify-otp")
    suspend fun verifyOtp(
        @Query("email") email: String,
        @Query("otp") otp: String
    ): Response<ApiResponse<VerifyOtpResponse>>

    // API đặt lại mật khẩu bằng resetToken đã xác thực.
    @POST("/api/auth/forgot-password/reset-password")
    suspend fun resetPassword(
        @Query("email") email: String,
        @Query("resetToken") resetToken: String,
        @Query("newPassword") newPassword: String
    ): Response<ApiResponse<Unit>>
}
