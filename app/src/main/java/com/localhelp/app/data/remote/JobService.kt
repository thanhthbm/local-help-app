package com.localhelp.app.data.remote

import com.localhelp.app.model.request.CreateJobRequest
import com.localhelp.app.model.response.ApiResponse
import com.localhelp.app.model.response.JobResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface JobService {
    @POST("/api/jobs")
    suspend fun createJob(@Body request: CreateJobRequest): Response<ApiResponse<JobResponse>>
}