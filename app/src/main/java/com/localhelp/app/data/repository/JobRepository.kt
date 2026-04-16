package com.localhelp.app.data.repository

import com.localhelp.app.data.remote.JobService
import com.localhelp.app.model.constant.JobStatus
import com.localhelp.app.model.request.CreateJobRequest
import com.localhelp.app.model.response.JobResponse
import com.localhelp.app.model.response.ResultPaginationDTO
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
                val errorMsg = response.errorBody()?.string() ?: ""
                Result.failure(Exception("Lỗi backend (${response.code()} - $errorMsg)"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateJob(id: Long, request: CreateJobRequest): Result<JobResponse> {
        return try {
            val response = jobService.updateJob(id, request)
            if (response.isSuccessful && response.body() != null) {
                val apiResponse = response.body()!!
                if (apiResponse.data != null) {
                    Result.success(apiResponse.data)
                } else {
                    Result.failure(Exception(apiResponse.message?.toString() ?: "Dữ liệu trả về rỗng"))
                }
            } else {
                val errorMsg = response.errorBody()?.string() ?: ""
                Result.failure(Exception("Lỗi backend (${response.code()} - $errorMsg)"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteJob(id: Long): Result<Unit> {
        return try {
            val response = jobService.deleteJob(id)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                val errorMsg = response.errorBody()?.string() ?: ""
                Result.failure(Exception("Lỗi backend (${response.code()} - $errorMsg)"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getMyPosts(current: Int, pageSize: Int, userId: Long? = null): Result<ResultPaginationDTO<List<JobResponse>>> {
        return try {
            val response = jobService.getMyPosts(current, pageSize, userId)
            if (response.isSuccessful && response.body() != null) {
                val apiResponse = response.body()!!
                if (apiResponse.data != null) {
                    Result.success(apiResponse.data)
                } else {
                    Result.failure(Exception("Dữ liệu trả về rỗng"))
                }
            } else {
                val errorMsg = response.errorBody()?.string() ?: ""
                Result.failure(Exception("Lỗi backend (${response.code()} - $errorMsg)"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getMyJobs(status: JobStatus? = null): Result<List<JobResponse>> {
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
                val errorMsg = response.errorBody()?.string() ?: ""
                Result.failure(Exception("Lỗi backend (${response.code()} - $errorMsg)"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getOpenJobs(
        current: Int,
        pageSize: Int,
        categoryId: Long? = null,
        lat: Double? = null,
        lng: Double? = null
    ): Result<ResultPaginationDTO<List<JobResponse>>> {
        return try {
            val response = jobService.getOpenJobs(current, pageSize, categoryId, lat, lng)

            if (response.isSuccessful && response.body() != null) {
                val apiResponse = response.body()!!

                if (apiResponse.data != null) {
                    // Trả về dữ liệu thành công
                    Result.success(apiResponse.data)
                } else {
                    //Nếu API trả về 200 nhưng cục data bị mất, báo lỗi luôn
                    Result.failure(Exception("Dữ liệu trả về bị rỗng"))
                }
            } else {
                val errorMsg = response.errorBody()?.string() ?: ""
                Result.failure(Exception("Lỗi backend (${response.code()} - $errorMsg)"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getFeaturedJobs(): Result<List<JobResponse>> {
        return try {
            val response = jobService.getFeaturedJobs()

            if (response.isSuccessful && response.body() != null) {
                val apiResponse = response.body()!!
                if (apiResponse.data != null) {
                    Result.success(apiResponse.data)
                } else {
                    Result.success(emptyList())
                }
            } else {
                val errorMsg = response.errorBody()?.string() ?: ""
                Result.failure(Exception("Lỗi backend (${response.code()} - $errorMsg)"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getJobById(id: Long): Result<JobResponse> {
        return try {
            val response = jobService.getJobById(id)

            if (response.isSuccessful && response.body() != null){
                val apiResponse = response.body()!!

                if (apiResponse.data != null){
                    Result.success(apiResponse.data)
                } else {
                    Result.failure(Exception("Dữ liệu trả về bị rỗng"))
                }
            } else {
                val errorMsg = response.errorBody()?.string() ?: ""
                Result.failure(Exception("Lỗi backend (${response.code()} - $errorMsg)"))
            }
        } catch (e: Exception){
            Result.failure(e)
        }
    }

    suspend fun acceptJob(id: Long): Result<Any> {
        return try {
            val response = jobService.acceptJob(id)
            if (response.isSuccessful && response.body() != null) {
                val apiResponse = response.body()!!
                if (apiResponse.data != null) {
                    Result.success(apiResponse.data)
                } else {
                    Result.failure(Exception(apiResponse.message?.toString() ?: "Lỗi khi nhận việc"))
                }
            } else {
                Result.failure(Exception("Lỗi : (${response.message()})"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getMyPosts(
        page :Int,
        size : Int
    ) : Result<ResultPaginationDTO<List<JobResponse>>> {
        return try {
            val response = jobService.getMyPosts(page, size)
            if (response.isSuccessful && response.body() != null) {
                val apiResponse = response.body()!!
                if (apiResponse.data != null) {
                    Result.success(apiResponse.data)
                } else {
                    Result.failure(Exception(apiResponse.message?.toString() ?: "Lỗi khi lấy danh sách việc đã nhận"))
                }
            } else {
                Result.failure(Exception("Lỗi máy chủ (${response.code()})"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getMyTasks(
        page: Int,
        size: Int,
        lat: Double? = null,
        lng: Double? = null
    ): Result<ResultPaginationDTO<List<JobResponse>>> {
        return try {
            val response = jobService.getMyTasks(page, size, lat, lng)
            if (response.isSuccessful && response.body() != null) {
                val apiResponse = response.body()!!
                if (apiResponse.data != null) {
                    Result.success(apiResponse.data)
                } else {
                    Result.failure(Exception(apiResponse.message?.toString() ?: "Lỗi khi lấy danh sách việc đã nhận"))
                }
            } else {
                Result.failure(Exception("Lỗi máy chủ (${response.code()})"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}