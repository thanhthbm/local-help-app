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

/**
 * Retrofit service khai báo các API liên quan đến công việc.
 *
 * Bao gồm các endpoint phục vụ đăng công việc, cập nhật công việc,
 * hủy công việc và lấy thông tin để hiển thị trên các màn hình quản lý.
 */
interface JobService {
    /**
     * Gửi request đăng công việc mới lên backend.
     *
     * @param request Dữ liệu công việc đã nhập trên form đăng việc.
     * @return Response chứa ApiResponse<JobResponse> nếu backend xử lý thành công.
     */
    @POST("/api/jobs")
    suspend fun createJob(@Body request: CreateJobRequest): Response<ApiResponse<JobResponse>>

    /**
     * Cập nhật thông tin công việc theo ID.
     *
     * @param id ID công việc cần cập nhật.
     * @param request Dữ liệu mới của công việc.
     * @return Response chứa công việc sau khi cập nhật.
     */
    @PUT("/api/jobs/{id}")
    suspend fun updateJob(@Path("id") id: Long, @Body request: CreateJobRequest): Response<ApiResponse<JobResponse>>

    /**
     * Hủy hoặc xóa công việc theo ID.
     *
     * @param id ID công việc cần hủy.
     * @return Response rỗng nếu thao tác thành công.
     */
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

    /**
     * Lấy chi tiết công việc để xem thông tin, mở form cập nhật hoặc hủy việc.
     *
     * @param id ID công việc cần lấy chi tiết.
     * @return Response chứa thông tin chi tiết của công việc.
     */
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
