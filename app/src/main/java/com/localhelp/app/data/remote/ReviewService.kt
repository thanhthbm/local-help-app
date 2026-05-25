package com.localhelp.app.data.remote

import com.localhelp.app.model.response.ApiResponse
import com.localhelp.app.model.response.ReviewResponse
import com.localhelp.app.model.response.ResultPaginationDTO
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
/**
 * Retrofit2 interface định nghĩa HTTP call để lấy danh sách đánh giá.
 *
 */
interface ReviewService {
    /**
     * Gọi GET /api/reviews/user/{userId} để lấy danh sách đánh giá phân trang.
     *
     * @param userId    @Path – ID người dùng cần xem đánh giá
     * @param current   @Query – Số trang (bắt đầu từ 1)
     * @param pageSize  @Query – Số đánh giá mỗi trang
     * @return          ApiResponse<ResultPaginationDTO<List<ReviewResponse>>>
     */
    @GET("/api/reviews/user/{userId}")
    suspend fun getReviewsByUser(
        @Path("userId") userId: Long,
        @Query("current") page: Int,
        @Query("pageSize") pageSize: Int
    ): Response<ApiResponse<ResultPaginationDTO<List<ReviewResponse>>>>
}
