package com.localhelp.app.data.remote

import com.localhelp.app.model.response.ApiResponse
import com.localhelp.app.model.response.CategoryResponse
import retrofit2.Response
import retrofit2.http.GET

interface CategoryService {
    @GET("/api/categories")
    suspend fun getAllCategories(): Response<ApiResponse<List<CategoryResponse>>>
}