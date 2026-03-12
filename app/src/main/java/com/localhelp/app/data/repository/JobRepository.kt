package com.localhelp.app.data.repository

import com.localhelp.app.data.remote.JobService
import com.localhelp.app.model.constant.JobStatus
import com.localhelp.app.model.request.CreateJobRequest
import com.localhelp.app.model.response.JobResponse
import javax.inject.Inject

class JobRepository @Inject constructor(
    private val jobService: JobService

) {
    suspend fun createJob(request: CreateJobRequest): Result<JobResponse> {
        return try {
            val response = jobService.createJob(request)
            if (response.isSuccessful && response.body() != null) {
                val apiResponse = response.body()!!

                if (apiResponse.data != null) {
                    Result.success(apiResponse.data)
                } else {
                    Result.failure(Exception(apiResponse.message?.toString() ?: "Dữ liệu trả về rỗng"))
                }
            } else {
                Result.failure(Exception("Lỗi backend (${response.code()})"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getMyJobs(status: JobStatus?): Result<List<JobResponse>> {
        return try {
            val response = jobService.getMyJobs(status)
            if (response.isSuccessful && response.body() != null) {
                val apiResponse = response.body()!!
                if (apiResponse.data != null) {
                    Result.success(apiResponse.data)
                } else {
                    Result.success(emptyList())
                }
            } else {
                Result.failure(Exception("Lỗi backend (${response.code()})"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}