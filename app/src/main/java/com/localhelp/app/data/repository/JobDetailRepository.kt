package com.localhelp.app.data.repository

import com.localhelp.app.data.remote.JobDetailApiService
import com.localhelp.app.model.request.ReviewRequest
import com.localhelp.app.model.response.ApplicationResponse
import com.localhelp.app.model.response.JobDetailResponse
import com.localhelp.app.model.response.JobImageResponse
import com.localhelp.app.model.response.ReviewResponse
import javax.inject.Inject

/**
 * Repository trung gian giữa ViewModel và JobDetailApiService cho luồng chi tiết công việc.
 *
 * Repository này xử lý dữ liệu cho các thao tác:
 * - lấy chi tiết việc và timeline;
 * - lấy danh sách thợ ứng tuyển;
 * - chấp nhận thợ;
 * - cập nhật tiến trình;
 * - gửi/lấy ảnh bằng chứng;
 * - xác nhận hoàn thành;
 * - gửi/lấy đánh giá.
 */
class JobDetailRepository @Inject constructor(private val apiService: JobDetailApiService) {

    /**
     * Lấy chi tiết công việc kèm tiến trình hiện tại.
     *
     * @param jobId ID công việc cần xem.
     * @return Result chứa JobDetailResponse nếu thành công.
     */
    suspend fun getJobDetail(jobId: Long): Result<JobDetailResponse> = runCatching {
        val response = apiService.getJobDetail(jobId)
        if (response.isSuccessful) {
            response.body()?.data ?: throw Exception("Data is null")
        } else {
            throw Exception(response.message())
        }
    }

    /**
     * Lấy danh sách thợ đã ứng tuyển vào công việc.
     *
     * @param jobId ID công việc cần xem danh sách ứng tuyển.
     * @return Result chứa List<ApplicationResponse>.
     */
    suspend fun getJobApplications(jobId: Long): Result<List<ApplicationResponse>> = runCatching {
        val response = apiService.getJobApplications(jobId)
        if (response.isSuccessful) {
            response.body()?.data ?: emptyList()
        } else {
            throw Exception(response.message())
        }
    }

    /**
     * Gửi yêu cầu chấp nhận một đơn ứng tuyển cụ thể.
     *
     * @param applicationId ID đơn ứng tuyển được chọn.
     * @return Result<Unit> nếu thao tác thành công.
     */
    suspend fun acceptApplication(applicationId: Long): Result<Unit> = runCatching {
        val response = apiService.acceptApplication(applicationId)
        if (!response.isSuccessful) throw Exception(response.message())
    }

    /**
     * Cập nhật tiến trình công việc sang trạng thái đang di chuyển.
     *
     * @param jobId ID công việc cần cập nhật.
     * @return Result<Unit> nếu backend xử lý thành công.
     */
    suspend fun updateStatusMoving(jobId: Long): Result<Unit> = runCatching {
        val response = apiService.updateStatusMoving(jobId)
        if (!response.isSuccessful) throw Exception(response.message())
    }

    /**
     * Cập nhật tiến trình công việc sang trạng thái đã đến nơi.
     *
     * @param jobId ID công việc cần cập nhật.
     * @return Result<Unit> nếu backend xử lý thành công.
     */
    suspend fun updateStatusArrived(jobId: Long): Result<Unit> = runCatching {
        val response = apiService.updateStatusArrived(jobId)
        if (!response.isSuccessful) throw Exception(response.message())
    }

    /**
     * Gửi danh sách URL ảnh bằng chứng hoàn thành công việc.
     *
     * @param jobId ID công việc cần nộp bằng chứng.
     * @param imageUrls Danh sách URL ảnh đã upload.
     * @return Result<Unit> nếu gửi thành công.
     */
    suspend fun submitEvidence(jobId: Long, imageUrls: List<String>): Result<Unit> {
        return runCatching {
            val response = apiService.submitJobEvidence(jobId, imageUrls)
            if (!response.isSuccessful) throw Exception("Lỗi: ${response.message()}")
        }
    }

    /**
     * Gửi xác nhận hoàn thành và thanh toán từ phía chủ việc.
     *
     * @param jobId ID công việc cần xác nhận hoàn thành.
     * @return Result<Unit> nếu backend cập nhật thành công.
     */
    suspend fun confirmPayment(jobId: Long): Result<Unit> = runCatching {
        val response = apiService.confirmPayment(jobId)
        if (!response.isSuccessful) throw Exception(response.message())
    }

    /**
     * Lấy danh sách ảnh bằng chứng của công việc.
     *
     * @param jobId ID công việc cần xem ảnh.
     * @return Result chứa List<JobImageResponse>.
     */
    suspend fun getJobEvidence(jobId: Long): Result<List<JobImageResponse>> = runCatching {
        val response = apiService.getJobEvidence(jobId)
        if (response.isSuccessful) {
            response.body()?.data ?: emptyList()
        } else {
            throw Exception(response.message())
        }
    }

    /**
     * Lấy đánh giá đã có của công việc.
     *
     * @param jobId ID công việc cần xem review.
     * @return Result chứa ReviewResponse nếu đã có đánh giá.
     */
    suspend fun getJobReview(jobId: Long): Result<ReviewResponse> = runCatching {
        val response = apiService.getJobReview(jobId)
        if (response.isSuccessful) {
            response.body()?.data ?: throw Exception("No review found")
        } else {
            throw Exception(response.message())
        }
    }

    /**
     * Tạo request đánh giá và gửi lên backend.
     *
     * @param jobId ID công việc cần đánh giá.
     * @param rating Số sao đánh giá.
     * @param comment Nội dung nhận xét.
     * @return Result<Unit> nếu gửi thành công.
     */
    suspend fun submitReview(jobId: Long, rating: Int, comment: String): Result<Unit> = runCatching {
        val request = ReviewRequest(rating, comment)
        val response = apiService.submitReview(jobId, request)
        if (!response.isSuccessful) throw Exception(response.message())
    }
}
