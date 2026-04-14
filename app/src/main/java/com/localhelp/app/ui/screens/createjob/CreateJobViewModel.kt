package com.localhelp.app.ui.screens.createjob

import android.net.Uri
import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.localhelp.app.data.repository.CategoryRepository
import com.localhelp.app.data.repository.JobRepository
import com.localhelp.app.model.request.CreateJobRequest
import com.localhelp.app.model.response.CategoryResponse
import com.localhelp.app.utils.CloudinaryHelper
import com.localhelp.app.utils.FormatterUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
@HiltViewModel
class CreateJobViewModel @Inject constructor(
    private val jobRepository: JobRepository,
    private val categoryRepository: CategoryRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val jobId: Long? = savedStateHandle.get<String>("jobId")?.toLongOrNull()
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

    fun updateImages(uris: List<Uri>) {
        selectedImageUris.value = uris
    }

    fun setLocation(lat: Double, lng: Double, addr: String) {
        latitude.value = lat
        longitude.value = lng
        address.value = addr
    }

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

    private fun fetchJobDetails() {
        jobId?.let { id ->
            viewModelScope.launch {
                val result = jobRepository.getJobById(id)
                result.onSuccess { job ->
                    title.value = job.title ?: ""
                    description.value = job.description ?: ""
                    price.value = FormatterUtils.formatPrice(job.price ?: 0.0)
                    address.value = job.address ?: ""
                    latitude.value = job.latitude ?: 20.9800
                    longitude.value = job.longitude ?: 105.7950
                    
                    // Cập nhật selectedCategoryId và đảm bảo nó được chọn đúng
                    if (job.categoryId != null && job.categoryId != 0L) {
                        selectedCategoryId.value = job.categoryId
                    }
                    
                    existingImageUrls.value = job.images ?: emptyList()
                }
            }
        }
    }

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

    fun createJob() {
        if (title.value.isBlank() || description.value.isBlank() || price.value.isBlank() || address.value.isBlank()) {
            _errorMessage.value = "Vui lòng nhập đầy đủ thông tin"
            return
        }

        _isLoading.value = true
        _errorMessage.value = null

        viewModelScope.launch {
            try {
                val uploadedUrls = coroutineScope {
                    selectedImageUris.value.map { uri ->
                        async { CloudinaryHelper.uploadMedia(uri) }
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
                    jobRepository.updateJob(jobId!!, request)
                } else {
                    jobRepository.createJob(request)
                }

                result.onSuccess {
                    _createSuccess.value = true
                }.onFailure { error ->
                    _errorMessage.value = error.message ?: "Có lỗi xảy ra"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Lỗi: ${e.localizedMessage}"
            } finally {
                _isLoading.value = false
            }
        }
    }
}