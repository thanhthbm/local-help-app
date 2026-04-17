package com.localhelp.app.data.remote

import com.localhelp.app.model.response.ApiResponse
import com.localhelp.app.model.response.ReviewResponse
import com.localhelp.app.model.response.ResultPaginationDTO
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ReviewService {
    @GET("/api/reviews/user/{userId}")
    suspend fun getReviewsByUser(
        @Path("userId") userId: Long,
        @Query("current") page: Int,
        @Query("pageSize") pageSize: Int
    ): Response<ApiResponse<ResultPaginationDTO<List<ReviewResponse>>>>
}
