package com.localhelp.app.data.remote

import com.localhelp.app.model.response.ApiResponse
import com.localhelp.app.model.response.UserResponse
import com.localhelp.app.model.response.VerifyOtpResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query

/**
 * Retrofit service khai báo API xác thực và khôi phục mật khẩu.
 */
interface AuthService {
    @POST("/api/auth/login")
    suspend fun loginSync(@Header("Authorization") token: String): Response<ApiResponse<UserResponse>>

    /**
     * Gửi OTP đến email để bắt đầu luồng khôi phục mật khẩu.
     *
     * @param email Email tài khoản cần đặt lại mật khẩu.
     * @return Response rỗng nếu backend gửi OTP thành công.
     */
    @POST("/api/auth/forgot-password/send-otp")
    suspend fun sendOtp(@Query("email") email: String): Response<ApiResponse<Unit>>

    /**
     * Xác thực OTP và lấy resetToken cho bước đặt mật khẩu mới.
     *
     * @param email Email đã yêu cầu OTP.
     * @param otp Mã OTP người dùng nhập.
     * @return Response chứa resetToken nếu OTP hợp lệ.
     */
    @POST("/api/auth/forgot-password/verify-otp")
    suspend fun verifyOtp(
        @Query("email") email: String,
        @Query("otp") otp: String
    ): Response<ApiResponse<VerifyOtpResponse>>

    /**
     * Đặt lại mật khẩu bằng resetToken đã xác thực.
     *
     * @param email Email tài khoản cần đặt lại mật khẩu.
     * @param resetToken Token nhận được sau khi xác thực OTP.
     * @param newPassword Mật khẩu mới người dùng nhập.
     * @return Response rỗng nếu đổi mật khẩu thành công.
     */
    @POST("/api/auth/forgot-password/reset-password")
    suspend fun resetPassword(
        @Query("email") email: String,
        @Query("resetToken") resetToken: String,
        @Query("newPassword") newPassword: String
    ): Response<ApiResponse<Unit>>
}
