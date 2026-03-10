package com.localhelp.app.ui.screens.createjob

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.localhelp.app.data.repository.CategoryRepository
import com.localhelp.app.data.repository.JobRepository
import com.localhelp.app.model.request.CreateJobRequest
import com.localhelp.app.model.response.CategoryResponse
import com.localhelp.app.utils.CloudinaryHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import android.util.Log
@HiltViewModel
class CreateJobViewModel @Inject constructor(
    private val jobRepository: JobRepository,
    private val categoryRepository: CategoryRepository
) : ViewModel() {

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

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _createSuccess = MutableStateFlow(false)
    val createSuccess: StateFlow<Boolean> = _createSuccess.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    fun updateImages(uris: List<Uri>) {
        selectedImageUris.value = uris
    }

    init {
        fetchCategories()
    }

    private fun fetchCategories(){
        viewModelScope.launch {
            val result = categoryRepository.getCategories()
            result.onSuccess { list ->
                Log.d("CreateJob", "Lấy thành công ${list.size} danh mục")
                categories.value = list

                if (list.isNotEmpty() && selectedCategoryId.value == null){
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
                val uploadedUrls = selectedImageUris.value.map { uri ->
                    async { CloudinaryHelper.uploadImage(uri) }
                }.awaitAll()

                val request = CreateJobRequest(
                    title = title.value,
                    description = description.value,
                    price = price.value.replace(".", "").toDoubleOrNull() ?: 0.0,
                    address = address.value,
                    latitude = latitude.value,
                    longitude = longitude.value,
                    categoryId = selectedCategoryId.value ?: 1L,
                    imageUrls = uploadedUrls
                )

                val result = jobRepository.createJob(request)
                result.onSuccess {
                    _createSuccess.value = true
                }.onFailure { error ->
                    _errorMessage.value = error.message ?: "Có lỗi xảy ra khi đăng việc"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Lỗi tải ảnh: ${e.localizedMessage}"
            } finally {
                _isLoading.value = false
            }
        }
    }
}