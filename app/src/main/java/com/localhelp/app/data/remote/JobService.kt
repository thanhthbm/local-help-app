package com.localhelp.app.data.remote

import com.localhelp.app.model.constant.JobStatus
import com.localhelp.app.model.request.CreateJobRequest
import com.localhelp.app.model.request.SearchJobRequest
import com.localhelp.app.model.response.ApiResponse
import com.localhelp.app.model.response.JobResponse
import com.localhelp.app.model.response.ResultPaginationDTO
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

import retrofit2.http.DELETE
import retrofit2.http.PATCH
import retrofit2.http.PUT

interface JobService {
    // API đăng công việc mới.
    @POST("/api/jobs")
    suspend fun createJob(@Body request: CreateJobRequest): Response<ApiResponse<JobResponse>>

    // API cập nhật thông tin công việc theo id.
    @PUT("/api/jobs/{id}")
    suspend fun updateJob(@Path("id") id: Long, @Body request: CreateJobRequest): Response<ApiResponse<JobResponse>>

    // API hủy/xóa công việc theo id.
    @DELETE("/api/jobs/{id}")
    suspend fun deleteJob(@Path("id") id: Long): Response<ApiResponse<Unit>>

    @GET("/api/jobs/my-posts")
    suspend fun getMyPosts(
        @Query("current") current: Int,
        @Query("pageSize") pageSize: Int,
        @Query("userId") userId: Long? = null
    ): Response<ApiResponse<ResultPaginationDTO<List<JobResponse>>>>

    @GET("/api/jobs/my-jobs")
    suspend fun getMyJobs(
        @Query("status") status: JobStatus? = null
    ): Response<ApiResponse<List<JobResponse>>>

    @POST("api/jobs/search")
    suspend fun searchJobs(
        @Body request: SearchJobRequest
    ): Response<ApiResponse<ResultPaginationDTO<List<JobResponse>>>>

    @GET("/api/jobs")
    suspend fun getOpenJobs(
        @Query("current") current: Int,
        @Query("pageSize") pageSize: Int,
        @Query("categoryId") categoryId: Long? = null,
        @Query("lat") latitude: Double? = null,
        @Query("lng") longitude: Double? = null
    ): Response<ApiResponse<ResultPaginationDTO<List<JobResponse>>>>

    @GET("/api/jobs/featured")
    suspend fun getFeaturedJobs(): Response<ApiResponse<List<JobResponse>>>

    // API lấy chi tiết công việc, dùng khi xem chi tiết hoặc mở form cập nhật.
    @GET("/api/jobs/{id}")
    suspend fun getJobById(@Path("id") id: Long): Response<ApiResponse<JobResponse>>

    @POST("/api/jobs/{id}/apply")
    suspend fun acceptJob(@Path("id") id: Long): Response<ApiResponse<Any>>

    @GET("api/jobs/my-posts")
    suspend fun getMyPosts(
        @Query("current") page: Int = 1,
        @Query("pageSize") size: Int = 10
    ): Response<ApiResponse<ResultPaginationDTO<List<JobResponse>>>>

    @GET("api/jobs/my-tasks")
    suspend fun getMyTasks(
        @Query("current") page: Int = 1,
        @Query("pageSize") size: Int = 10,
        @Query("lat") lat: Double? = null,
        @Query("lng") lng: Double? = null
    ): Response<ApiResponse<ResultPaginationDTO<List<JobResponse>>>>
}
