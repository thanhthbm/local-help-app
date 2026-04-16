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

interface JobDetailApiService {

    @GET("api/jobs/{id}/detail")
    suspend fun getJobDetail(@Path("id") jobId: Long): Response<ApiResponse<JobDetailResponse>>

    @GET("api/jobs/{jobId}/applications")
    suspend fun getJobApplications(@Path("jobId") jobId: Long): Response<ApiResponse<List<ApplicationResponse>>>

    @POST("api/jobs/applications/{applicationId}/accept")
    suspend fun acceptApplication(@Path("applicationId") applicationId: Long): Response<ApiResponse<Any>>

    @POST("api/jobs/{jobId}/status/moving")
    suspend fun updateStatusMoving(@Path("jobId") jobId: Long): Response<ApiResponse<Any>>

    @POST("api/jobs/{jobId}/status/arrived")
    suspend fun updateStatusArrived(@Path("jobId") jobId: Long): Response<ApiResponse<Any>>

    @Multipart
    @POST("api/jobs/{jobId}/submit-evidence")
    suspend fun submitEvidence(
        @Path("jobId") jobId: Long,
        @Part images: List<MultipartBody.Part>
    ): Response<ApiResponse<Any>>

    @POST("api/jobs/{jobId}/confirm-payment")
    suspend fun confirmPayment(@Path("jobId") jobId: Long): Response<ApiResponse<Any>>

    @GET("api/jobs/{jobId}/evidence")
    suspend fun getJobEvidence(@Path("jobId") jobId: Long): Response<ApiResponse<List<JobImageResponse>>>

    @GET("api/jobs/{jobId}/review")
    suspend fun getJobReview(@Path("jobId") jobId: Long): Response<ApiResponse<ReviewResponse>>

    @POST("api/jobs/{jobId}/reviews")
    suspend fun submitReview(
        @Path("jobId") jobId: Long,
        @Body request: ReviewRequest
    ): Response<ApiResponse<Any>>
}