package com.localhelp.app.data.repository

import com.localhelp.app.data.remote.ReviewService
import com.localhelp.app.model.response.ApiResponse
import com.localhelp.app.model.response.ResultPaginationDTO
import com.localhelp.app.model.response.ReviewResponse
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReviewRepository @Inject constructor(
    private val reviewService: ReviewService
) {
    suspend fun getReviewsByUser(userId: Long, page: Int, pageSize: Int): Result<ResultPaginationDTO<List<ReviewResponse>>> {
        return try {
            val response = reviewService.getReviewsByUser(userId, page, pageSize)
            if (response.isSuccessful && response.body()?.data != null) {
                Result.success(response.body()!!.data!!)
            } else {
                Result.failure(Exception("Lỗi khi lấy danh sách đánh giá"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
