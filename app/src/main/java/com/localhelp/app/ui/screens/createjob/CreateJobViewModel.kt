package com.localhelp.app.ui.screens.createjob

import android.net.Uri
import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.localhelp.app.data.remote.CloudinaryService
import com.localhelp.app.data.repository.CategoryRepository
import com.localhelp.app.data.repository.JobRepository
import com.localhelp.app.model.request.CreateJobRequest
import com.localhelp.app.model.response.CategoryResponse
import com.localhelp.app.utils.FormatterUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import javax.inject.Inject

/**
 * ViewModel quản lý form đăng công việc và cập nhật công việc.
 *
 * Khi NavGraph truyền jobId, ViewModel chạy ở chế độ chỉnh sửa và tự tải dữ
 * liệu công việc cũ. Nếu không có jobId, form được dùng để đăng công việc mới.
 */
@HiltViewModel
class CreateJobViewModel @Inject constructor(
    private val jobRepository: JobRepository,
    private val categoryRepository: CategoryRepository,
    private val cloudinaryService: CloudinaryService,
    private val savedStateHandle: SavedStateHandle,
    @ApplicationContext private val context: android.content.Context
) : ViewModel() {

    private val jobId: Long? = savedStateHandle.get<String>("jobId")?.toLongOrNull() ?: savedStateHandle.get<Long>("jobId")
    val isEditMode = jobId != null

    // Form states
    val title = MutableStateFlow("")
    val description = MutableStateFlow("")
    val price = MutableStateFlow("")
    val address = MutableStateFlow("")

    val latitude = MutableStateFlow<Double>(20.9800)
    val longitude = MutableStateFlow<Double>(105.7950)

    val categories = MutableStateFlow<List<CategoryResponse>>(emptyList())
    val selectedCategoryId = MutableStateFlow<Long?>(null)

    val selectedImageUris = MutableStateFlow<List<Uri>>(emptyList())
    val existingImageUrls = MutableStateFlow<List<String>>(emptyList())

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _createSuccess = MutableStateFlow(false)
    val createSuccess: StateFlow<Boolean> = _createSuccess.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    /**
     * Cập nhật danh sách ảnh mới người dùng chọn từ thiết bị.
     *
     * @param uris Danh sách URI ảnh được chọn.
     */
    fun updateImages(uris: List<Uri>) {
        selectedImageUris.value = uris
    }

    /**
     * Xóa một ảnh đã có trên server khỏi danh sách ảnh giữ lại khi cập nhật.
     *
     * @param url URL ảnh cũ cần xóa khỏi request cập nhật.
     */
    fun removeExistingImage(url: String) {
        existingImageUrls.value = existingImageUrls.value.filter { it != url }
    }

    /**
     * Xóa một ảnh mới chọn khỏi danh sách upload trước khi submit form.
     *
     * @param uri URI ảnh cần bỏ chọn.
     */
    fun removeSelectedImage(uri: Uri) {
        selectedImageUris.value = selectedImageUris.value.filter { it != uri }
    }

    /**
     * Nhận tọa độ và địa chỉ từ màn chọn vị trí.
     *
     * @param lat Vĩ độ vị trí công việc.
     * @param lng Kinh độ vị trí công việc.
     * @param addr Địa chỉ hiển thị của vị trí đã chọn.
     */
    fun setLocation(lat: Double, lng: Double, addr: String) {
        latitude.value = lat
        longitude.value = lng
        address.value = addr
    }

    /**
     * Chuẩn hóa tiền công khi người dùng nhập.
     *
     * Giá trị hiển thị được format theo nhóm hàng nghìn, còn khi submit sẽ được
     * làm sạch bằng FormatterUtils.cleanPrice().
     *
     * @param newPrice Chuỗi tiền công mới từ ô nhập.
     */
    fun updatePrice(newPrice: String) {
        val clean = FormatterUtils.cleanPrice(newPrice)
        if (clean.isEmpty()) {
            price.value = ""
        } else {
            price.value = FormatterUtils.formatPrice(clean)
        }
    }

    init {
        fetchCategories()
        if (isEditMode) {
            fetchJobDetails()
        }
    }

    /**
     * Tải chi tiết công việc cũ để đổ vào form cập nhật.
     */
    private fun fetchJobDetails() {
        jobId?.let { id ->
            viewModelScope.launch {
                Log.d("CreateJob", "Fetching job details for id: $id")
                val result = jobRepository.getJobById(id)
                result.onSuccess { job ->
                    Log.d("CreateJob", "Fetched job details: $job")
                    title.value = job.title ?: ""
                    description.value = job.description ?: ""
                    price.value = FormatterUtils.formatPrice(job.price ?: 0.0)
                    address.value = job.address ?: ""
                    latitude.value = job.latitude ?: 20.9800
                    longitude.value = job.longitude ?: 105.7950
                    
                    // Cập nhật selectedCategoryId và đảm bảo nó được chọn đúng
                    if (job.categoryId != null && job.categoryId != 0L) {
                        Log.d("CreateJob", "Setting selectedCategoryId to: ${job.categoryId}")
                        selectedCategoryId.value = job.categoryId
                    } else {
                        Log.d("CreateJob", "Job categoryId is null or 0")
                    }
                    
                    existingImageUrls.value = job.images ?: emptyList()
                }.onFailure { error ->
                    Log.e("CreateJob", "Failed to fetch job details: ${error.message}")
                }
            }
        }
    }

    /**
     * Tải danh mục công việc để người dùng chọn khi đăng hoặc cập nhật bài.
     */
    private fun fetchCategories(){
        viewModelScope.launch {
            val result = categoryRepository.getCategories()
            result.onSuccess { list ->
                Log.d("CreateJob", "Lấy thành công ${list.size} danh mục")
                categories.value = list

                // Chỉ tự động chọn danh mục đầu tiên nếu đang tạo mới và chưa có category nào được chọn
                if (!isEditMode && list.isNotEmpty() && selectedCategoryId.value == null){
                    selectedCategoryId.value = list[0].id
                }
            }.onFailure { error ->
                Log.e("CreateJob", "Lỗi fetch category: ${error.message}", error)
                _errorMessage.value = "Không thể tải danh mục: ${error.localizedMessage}"
            }
        }
    }

    /**
     * Submit form đăng/cập nhật công việc.
     *
     * Hàm thực hiện validate dữ liệu, upload ảnh mới lên Cloudinary, ghép với
     * ảnh cũ đang giữ lại và gọi API create/update theo trạng thái isEditMode.
     */
    fun createJob() {
        if (title.value.isBlank() || description.value.isBlank() || price.value.isBlank() || address.value.isBlank()) {
            _errorMessage.value = "Vui lòng nhập đầy đủ thông tin"
            return
        }

        _isLoading.value = true
        _errorMessage.value = null

        viewModelScope.launch {
            try {
                // 1. Upload new images to Cloudinary via CloudinaryService (Direct)
                val uploadedUrls = coroutineScope {
                    selectedImageUris.value.map { uri ->
                        async {
                            val stream = context.contentResolver.openInputStream(uri)
                            val bytes = stream?.readBytes() ?: throw Exception("Cannot read file")
                            stream.close()
                            
                            val filePart = MultipartBody.Part.createFormData(
                                "file", "job_image.jpg",
                                bytes.toRequestBody("image/jpeg".toMediaTypeOrNull())
                            )
                            val presetPart = "localhelp_preset".toRequestBody("text/plain".toMediaTypeOrNull())
                            
                            val response = cloudinaryService.uploadImage(
                                cloudName = "dwtpcdjhe",
                                uploadPreset = presetPart,
                                file = filePart
                            )
                            
                            if (response.isSuccessful) {
                                response.body()?.secure_url ?: throw Exception("URL is null")
                            } else {
                                throw Exception("Upload failed: ${response.code()}")
                            }
                        }
                    }.awaitAll()
                }

                val allImageUrls = existingImageUrls.value + uploadedUrls

                val request = CreateJobRequest(
                    title = title.value,
                    description = description.value,
                    price = FormatterUtils.cleanPrice(price.value).toDoubleOrNull() ?: 0.0,
                    address = address.value,
                    latitude = latitude.value,
                    longitude = longitude.value,
                    categoryId = selectedCategoryId.value ?: 1L,
                    imageUrls = allImageUrls
                )

                val result = if (isEditMode) {
                    Log.d("CreateJob", "Updating job $jobId with request: $request")
                    jobRepository.updateJob(jobId!!, request)
                } else {
                    Log.d("CreateJob", "Creating job with request: $request")
                    jobRepository.createJob(request)
                }

                result.onSuccess {
                    Log.d("CreateJob", "Job operation successful")
                    _createSuccess.value = true
                }.onFailure { error ->
                    Log.e("CreateJob", "Job operation failed: ${error.message}")
                    _errorMessage.value = error.message ?: "Có lỗi xảy ra"
                }
            } catch (e: Exception) {
                Log.e("CreateJob", "Error in createJob", e)
                _errorMessage.value = "Lỗi: ${e.localizedMessage}"
            } finally {
                _isLoading.value = false
            }
        }
    }
}
