package com.localhelp.app.data.remote

import com.localhelp.app.model.response.CategoryResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header

interface HomeService {
    @GET("/api/categories")
    suspend fun getCategories(@Header("Authorization") token: String): Response<List<CategoryResponse>>
}