package com.localhelp.app.data.remote

import com.localhelp.app.model.request.ReviewRequest
import com.localhelp.app.model.response.ApiResponse
import com.localhelp.app.model.response.ApplicationResponse
import com.localhelp.app.model.response.JobDetailResponse
import com.localhelp.app.model.response.JobImageResponse
import com.localhelp.app.model.response.ReviewResponse
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path

/**
 * Retrofit service khai báo các API chi tiết cho luồng nhận việc và hoàn thành công việc.
 *
 * Bao gồm các endpoint phục vụ lấy chi tiết việc, danh sách ứng tuyển, chấp nhận thợ,
 * cập nhật tiến trình, gửi ảnh bằng chứng, xác nhận hoàn thành và gửi/lấy đánh giá.
 */
interface JobDetailApiService {

    /**
     * Lấy chi tiết công việc kèm timeline tiến trình.
     */
    @GET("api/jobs/{id}/detail")
    suspend fun getJobDetail(@Path("id") jobId: Long): Response<ApiResponse<JobDetailResponse>>

    /**
     * Lấy danh sách các thợ đã ứng tuyển vào một công việc.
     */
    @GET("api/jobs/{jobId}/applications")
    suspend fun getJobApplications(@Path("jobId") jobId: Long): Response<ApiResponse<List<ApplicationResponse>>>

    /**
     * Gửi yêu cầu chấp nhận một đơn ứng tuyển cụ thể.
     */
    @POST("api/jobs/applications/{applicationId}/accept")
    suspend fun acceptApplication(@Path("applicationId") applicationId: Long): Response<ApiResponse<Any>>

    /**
     * Cập nhật tiến trình công việc sang trạng thái đang di chuyển.
     */
    @POST("api/jobs/{jobId}/status/moving")
    suspend fun updateStatusMoving(@Path("jobId") jobId: Long): Response<ApiResponse<Any>>

    /**
     * Cập nhật tiến trình công việc sang trạng thái đã đến nơi.
     */
    @POST("api/jobs/{jobId}/status/arrived")
    suspend fun updateStatusArrived(@Path("jobId") jobId: Long): Response<ApiResponse<Any>>

    /**
     * Gửi danh sách URL ảnh bằng chứng hoàn thành công việc.
     */
    @POST("api/jobs/{jobId}/submit-evidence")
    suspend fun submitJobEvidence(
        @Path("jobId") jobId: Long,
        @Body imageUrls: List<String>
    ): Response<ApiResponse<Unit>>

    /**
     * Gửi xác nhận hoàn thành và thanh toán từ phía chủ việc.
     */
    @POST("api/jobs/{jobId}/confirm-payment")
    suspend fun confirmPayment(@Path("jobId") jobId: Long): Response<ApiResponse<Any>>

    /**
     * Lấy danh sách ảnh bằng chứng của công việc.
     */
    @GET("api/jobs/{jobId}/evidence")
    suspend fun getJobEvidence(@Path("jobId") jobId: Long): Response<ApiResponse<List<JobImageResponse>>>

    /**
     * Lấy đánh giá đã được tạo cho công việc.
     */
    @GET("api/jobs/{jobId}/review")
    suspend fun getJobReview(@Path("jobId") jobId: Long): Response<ApiResponse<ReviewResponse>>

    /**
     * Gửi đánh giá người giúp sau khi công việc hoàn thành.
     */
    @POST("api/jobs/{jobId}/reviews")
    suspend fun submitReview(
        @Path("jobId") jobId: Long,
        @Body request: ReviewRequest
    ): Response<ApiResponse<Any>>
}
