package com.localhelp.app.data.repository

import com.localhelp.app.data.remote.UserService
import com.localhelp.app.model.response.UserResponse
import javax.inject.Inject

class UserRepository @Inject constructor(
    private val userService: UserService
) {
    suspend fun getProfile(): Result<UserResponse> {
        return try {
            val response = userService.getProfile()
            if (response.isSuccessful && response.body() != null) {
                val apiResponse = response.body()!!

                if (apiResponse.data != null) {
                    Result.success(apiResponse.data)
                } else {
                    Result.failure(Exception(apiResponse.message?.toString() ?: "Dữ liệu user rỗng"))
                }
            } else {
                Result.failure(Exception("Không lấy được user (${response.code()})"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}