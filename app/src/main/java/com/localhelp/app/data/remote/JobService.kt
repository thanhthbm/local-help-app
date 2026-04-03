package com.localhelp.app.data.remote

import com.localhelp.app.model.constant.JobStatus
import com.localhelp.app.model.request.CreateJobRequest
import com.localhelp.app.model.response.ApiResponse
import com.localhelp.app.model.response.JobResponse
import com.localhelp.app.model.response.ResultPaginationDTO
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface JobService {
    @POST("/api/jobs")
    suspend fun createJob(@Body request: CreateJobRequest): Response<ApiResponse<JobResponse>>

    @GET("/api/jobs/my-jobs")
    suspend fun getMyJobs(@Query("status") status: JobStatus? = null): Response<ApiResponse<List<JobResponse>>>

    @GET("/api/jobs/search")
    suspend fun searchJobs(@Query("keyword") keyword: String) : Response<ApiResponse<List<JobResponse>>>

    @GET("/api/jobs")
    suspend fun getOpenJobs(
        @Query("current") current: Int,
        @Query("pageSize") pageSize: Int
    ): Response<ApiResponse<ResultPaginationDTO<List<JobResponse>>>>

    @GET("/api/jobs/{id}")
    suspend fun getJobById(@Path("id") id: Long): Response<ApiResponse<JobResponse>>
}
