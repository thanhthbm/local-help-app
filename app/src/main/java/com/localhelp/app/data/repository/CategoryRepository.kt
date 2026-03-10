package com.localhelp.app.data.repository

import com.localhelp.app.data.remote.CategoryService
import com.localhelp.app.model.response.CategoryResponse
import javax.inject.Inject

class CategoryRepository @Inject constructor(
    private val categoryService: CategoryService
) {
    suspend fun getCategories(): Result<List<CategoryResponse>> {
        return try {
            val response = categoryService.getAllCategories()

            if (response.isSuccessful && response.body() != null) {
                val apiResponse = response.body()!!

                if (apiResponse.data != null) {
                    Result.success(apiResponse.data)
                } else {
                    Result.success(emptyList())
                }
            } else {
                Result.failure(Exception("Lỗi máy chủ: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}