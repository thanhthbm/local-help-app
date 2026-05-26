package com.localhelp.app.data.repository

import com.localhelp.app.data.remote.JobService
import com.localhelp.app.model.constant.JobStatus
import com.localhelp.app.model.request.CreateJobRequest
import com.localhelp.app.model.response.JobResponse
import com.localhelp.app.model.response.ResultPaginationDTO
import javax.inject.Inject

/**
 * Repository trung gian giữa ViewModel và JobService.
 *
 * Repository chuẩn hóa response từ Retrofit thành Result để các ViewModel của
 * chức năng đăng việc, cập nhật việc và hủy việc xử lý onSuccess/onFailure.
 */
class JobRepository @Inject constructor(
    private val jobService: JobService

) {
    /**
     * Đăng công việc mới lên backend.
     *
     * @param request Dữ liệu công việc đã validate ở ViewModel.
     * @return Result chứa JobResponse nếu tạo thành công, hoặc exception nếu lỗi mạng/backend.
     */
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

    /**
     * Cập nhật công việc đã đăng theo ID.
     *
     * @param id ID công việc cần cập nhật.
     * @param request Dữ liệu mới của form cập nhật công việc.
     * @return Result chứa JobResponse đã cập nhật hoặc lỗi tương ứng.
     */
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

    /**
     * Hủy hoặc xóa một công việc đã đăng.
     *
     * @param id ID công việc cần hủy.
     * @return Result<Unit> cho biết thao tác thành công hay thất bại.
     */
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

    /**
     * Lấy chi tiết công việc để hiển thị và đổ dữ liệu vào form cập nhật.
     *
     * @param id ID công việc cần lấy chi tiết.
     * @return Result chứa JobResponse chi tiết hoặc lỗi tương ứng.
     */
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

    /**
     * Gửi yêu cầu nhận việc từ phía người giúp.
     *
     * @param id ID công việc muốn nhận.
     * @return Result chứa dữ liệu backend trả về hoặc lỗi tương ứng.
     */
    suspend fun acceptJob(id: Long): Result<Any> {
        return try {
            val response = jobService.acceptJob(id)
            val body = response.body()
            
            if (response.isSuccessful) {
                Result.success(body?.data ?: true)
            } else {
                val errorBody = response.errorBody()?.string()
                val gson = com.google.gson.Gson()
                val errorResponse = try {
                    gson.fromJson(errorBody, com.localhelp.app.model.response.ApiResponse::class.java)
                } catch (e: Exception) { null }

                val message = errorResponse?.message?.toString() ?: ""

                if (response.code() == 400 && message.contains("đã ứng tuyển", ignoreCase = true)) {
                    Result.success(true)
                } else {
                    Result.failure(Exception(if (message.isNotEmpty()) message else "Lỗi: ${response.code()}"))
                }
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Lấy danh sách công việc người dùng hiện tại đã đăng.
     *
     * @param page Trang hiện tại.
     * @param size Số phần tử mỗi trang.
     * @return Result chứa dữ liệu phân trang danh sách job đã đăng.
     */
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

    /**
     * Lấy danh sách công việc người dùng hiện tại đã nhận với vai trò helper.
     *
     * @param page Trang hiện tại.
     * @param size Số phần tử mỗi trang.
     * @param lat Vĩ độ hiện tại nếu muốn tính khoảng cách.
     * @param lng Kinh độ hiện tại nếu muốn tính khoảng cách.
     * @return Result chứa dữ liệu phân trang danh sách job đã nhận.
     */
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
