package com.localhelp.app.data.remote

import com.localhelp.app.model.response.ApiResponse
import com.localhelp.app.model.response.CategoryDetailResponse
import com.localhelp.app.model.response.FinanceOverviewResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface FinanceService {
    /**
     * Lấy thống kê tổng quan thu/chi theo tháng.
     *
     * type nhận "spending" hoặc "earning"; token được AuthInterceptor gắn vào request.
     */
    @GET("/api/finance/overview")
    suspend fun getFinanceOverview(
        @Query("type") type: String, // "spending" or "earning"
        @Query("month") month: Int,
        @Query("year") year: Int
    ): Response<ApiResponse<FinanceOverviewResponse>>

    /**
     * Lấy thống kê chi tiết của một danh mục trong tháng.
     */
    @GET("/api/finance/categories/{categoryId}/details")
    suspend fun getCategoryDetails(
        @Path("categoryId") categoryId: Long,
        @Query("type") type: String,
        @Query("month") month: Int,
        @Query("year") year: Int
    ): Response<ApiResponse<CategoryDetailResponse>>
}
