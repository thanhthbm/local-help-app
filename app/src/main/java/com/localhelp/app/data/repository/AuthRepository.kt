package com.localhelp.app.data.repository

import android.util.Log
import com.google.firebase.auth.ActionCodeSettings
import com.google.firebase.auth.FirebaseAuth
import com.localhelp.app.data.remote.AuthService
import com.localhelp.app.model.response.UserResponse
import javax.inject.Inject

/**
 * Repository xử lý xác thực và khôi phục mật khẩu.
 *
 * Các API khôi phục mật khẩu được wrap bằng Result để ViewModel điều khiển
 * trạng thái loading, lỗi và điều hướng từng bước.
 */
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

    /**
     * Gửi email reset password bằng Firebase Auth.
     *
     * Hàm này phục vụ luồng reset qua deep link Firebase, khác với luồng OTP
     * backend đang dùng trong ForgotPasswordViewModel.
     *
     * @param email Email cần gửi liên kết đặt lại mật khẩu.
     * @param onComplete Callback trả trạng thái thành công và thông báo lỗi nếu có.
     */
    fun sendResetEmail(email: String, onComplete: (Boolean, String?) -> Unit){
        val auth = FirebaseAuth.getInstance()

        val actionCodeSettings = ActionCodeSettings.newBuilder()
            .setUrl("localhelp://reset")
            .setHandleCodeInApp(true)
            .setAndroidPackageName(
                "com.localhelp.app",
                true,
                null
            )
            .build()

        auth.sendPasswordResetEmail(email, actionCodeSettings)
            .addOnCompleteListener { task ->
                if (task.isSuccessful){
                    onComplete(true, null)
                } else {
                    onComplete(false, task.exception?.message)
                }
            }
    }

    /**
     * Gửi OTP khôi phục mật khẩu đến email người dùng.
     *
     * @param email Email cần nhận mã OTP.
     * @return Result<Unit> nếu backend gửi OTP thành công.
     */
    suspend fun sendOtp(email: String): Result<Unit> {
        return try {
            val response = authService.sendOtp(email)
            if (response.isSuccessful) Result.success(Unit)
            else Result.failure(Exception("Lỗi gửi OTP: ${response.message()}"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Xác thực OTP và nhận resetToken cho bước đặt mật khẩu mới.
     *
     * @param email Email đã yêu cầu OTP.
     * @param otp Mã OTP người dùng nhập.
     * @return Result chứa resetToken nếu OTP hợp lệ.
     */
    suspend fun verifyOtp(email: String, otp: String): Result<String> {
        return try {
            val response = authService.verifyOtp(email, otp)
            if (response.isSuccessful && response.body()?.data != null) {
                Result.success(response.body()!!.data!!.resetToken)
            } else {
                Result.failure(Exception("OTP không chính xác hoặc đã hết hạn"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Đặt lại mật khẩu sau khi OTP đã được xác thực.
     *
     * @param email Email của tài khoản cần đổi mật khẩu.
     * @param resetToken Token nhận được từ bước xác thực OTP.
     * @param newPassword Mật khẩu mới.
     * @return Result<Unit> nếu đổi mật khẩu thành công.
     */
    suspend fun resetPassword(email: String, resetToken: String, newPassword: String): Result<Unit> {
        return try {
            val response = authService.resetPassword(email, resetToken, newPassword)
            if (response.isSuccessful) Result.success(Unit)
            else Result.failure(Exception("Lỗi đặt lại mật khẩu: ${response.message()}"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
