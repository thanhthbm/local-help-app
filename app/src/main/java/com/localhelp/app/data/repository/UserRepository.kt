package com.localhelp.app.data.repository

import com.localhelp.app.data.remote.UserService
import com.localhelp.app.model.response.UserResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
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

    suspend fun getUserById(userId: Long): Result<UserResponse> {
        return try {
            val response = userService.getUserById(userId)
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

    /**
     * Gửi một request duy nhất PUT /api/users/me (multipart/form-data).
     * Cả [data] (JSON text fields) và [avatarPart] (file ảnh) đều là tuỳ chọn.
     */
    suspend fun updateProfile(
        data: RequestBody?,
        avatarPart: MultipartBody.Part?
    ): Result<UserResponse> {
        return try {
            val response = userService.updateProfile(data, avatarPart)
            if (response.isSuccessful && response.body() != null) {
                val apiResponse = response.body()!!
                if (apiResponse.data != null) {
                    Result.success(apiResponse.data)
                } else {
                    Result.failure(Exception(apiResponse.message?.toString() ?: "Cập nhật thất bại"))
                }
            } else {
                Result.failure(Exception("Cập nhật thất bại (${response.code()})"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}