package com.localhelp.app.data.repository

import com.localhelp.app.data.remote.JobDetailApiService
import com.localhelp.app.model.request.ReviewRequest
import com.localhelp.app.model.response.ApplicationResponse
import com.localhelp.app.model.response.JobDetailResponse
import com.localhelp.app.model.response.JobImageResponse
import com.localhelp.app.model.response.ReviewResponse
import okhttp3.MultipartBody
import javax.inject.Inject

class JobDetailRepository @Inject constructor(private val apiService: JobDetailApiService) {

    suspend fun getJobDetail(jobId: Long): Result<JobDetailResponse> = runCatching {
        val response = apiService.getJobDetail(jobId)
        if (response.isSuccessful) {
            response.body()?.data ?: throw Exception("Data is null")
        } else {
            throw Exception(response.message())
        }
    }

    suspend fun getJobApplications(jobId: Long): Result<List<ApplicationResponse>> = runCatching {
        val response = apiService.getJobApplications(jobId)
        if (response.isSuccessful) {
            response.body()?.data ?: emptyList()
        } else {
            throw Exception(response.message())
        }
    }

    suspend fun acceptApplication(applicationId: Long): Result<Unit> = runCatching {
        val response = apiService.acceptApplication(applicationId)
        if (!response.isSuccessful) throw Exception(response.message())
    }

    suspend fun updateStatusMoving(jobId: Long): Result<Unit> = runCatching {
        val response = apiService.updateStatusMoving(jobId)
        if (!response.isSuccessful) throw Exception(response.message())
    }

    suspend fun updateStatusArrived(jobId: Long): Result<Unit> = runCatching {
        val response = apiService.updateStatusArrived(jobId)
        if (!response.isSuccessful) throw Exception(response.message())
    }

    suspend fun submitEvidence(jobId: Long, imageParts: List<MultipartBody.Part>): Result<Unit> = runCatching {
        val response = apiService.submitEvidence(jobId, imageParts)
        if (!response.isSuccessful) throw Exception(response.message())
    }

    suspend fun confirmPayment(jobId: Long): Result<Unit> = runCatching {
        val response = apiService.confirmPayment(jobId)
        if (!response.isSuccessful) throw Exception(response.message())
    }

    suspend fun getJobEvidence(jobId: Long): Result<List<JobImageResponse>> = runCatching {
        val response = apiService.getJobEvidence(jobId)
        if (response.isSuccessful) {
            response.body()?.data ?: emptyList()
        } else {
            throw Exception(response.message())
        }
    }

    suspend fun getJobReview(jobId: Long): Result<ReviewResponse> = runCatching {
        val response = apiService.getJobReview(jobId)
        if (response.isSuccessful) {
            response.body()?.data ?: throw Exception("No review found")
        } else {
            throw Exception(response.message())
        }
    }

    suspend fun submitReview(jobId: Long, rating: Int, comment: String): Result<Unit> = runCatching {
        val request = ReviewRequest(rating, comment)
        val response = apiService.submitReview(jobId, request)
        if (!response.isSuccessful) throw Exception(response.message())
    }
}