package com.localhelp.app.data.repository

import com.localhelp.app.data.remote.ReviewService
import com.localhelp.app.model.response.ApiResponse
import com.localhelp.app.model.response.ResultPaginationDTO
import com.localhelp.app.model.response.ReviewResponse
import javax.inject.Inject
import javax.inject.Singleton
/**
 * Repository trung gian giữa ViewModel và ReviewService (Retrofit API).
 *
 * Wrap kết quả trong Result<T> để ViewModel không cần xử lý exception trực tiếp.
 *
 * Return type phức tạp: Result<ResultPaginationDTO<List<ReviewResponse>>>
 * – ResultPaginationDTO là wrapper phân trang generic từ backend.
 *
 */
@Singleton
class ReviewRepository @Inject constructor(
    private val reviewService: ReviewService
) {
    /**
     * Lấy danh sách đánh giá phân trang của người dùng.
     *
     * @param userId    ID người dùng cần xem đánh giá
     * @param page      Trang hiện tại (bắt đầu từ 1)
     * @param pageSize  Số đánh giá mỗi trang
     * @return          Result<ResultPaginationDTO<List<ReviewResponse>>>
     */
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
