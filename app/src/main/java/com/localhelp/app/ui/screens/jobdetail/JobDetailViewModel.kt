package com.localhelp.app.ui.screens.jobdetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.localhelp.app.data.repository.JobRepository
import com.localhelp.app.model.response.JobResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class JobDetailViewModel @Inject constructor(
    private val jobRepository: JobRepository,
    savedStateHandle: SavedStateHandle
): ViewModel() {

    private val _job = MutableStateFlow<JobResponse?>(null)
    val job = _job.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading = _isLoading.asStateFlow()

    private val _acceptStatus = MutableStateFlow<Boolean?>(null)
    val acceptStatus = _acceptStatus.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage = _errorMessage.asStateFlow()

    init {
        // Tự động lấy ID từ NavGraph và fetch data
        val jobId = savedStateHandle.get<Long>("id")
        if (jobId != null) {
            fetchJob(jobId)
        }
    }

    private fun fetchJob(id: Long){
        _isLoading.value = true
        viewModelScope.launch {
            val result = jobRepository.getJobById(id)
            result.onSuccess { jobData ->
                _job.value = jobData
            }.onFailure {
                // TODO: Xử lý lỗi (show snackbar, v.v.)
            }
            _isLoading.value = false
        }
    }

    fun acceptJob() {
        val currentJob = _job.value ?: return

        _isLoading.value = true
        _errorMessage.value = null

        viewModelScope.launch {
            val result = jobRepository.acceptJob(currentJob.id)
            result.onSuccess { updatedJob ->
                _job.value = updatedJob // Cập nhật lại UI với trạng thái mới (IN_PROGRESS)
                _acceptStatus.value = true
            }.onFailure { error ->
                _acceptStatus.value = false
                _errorMessage.value = error.message ?: "Có lỗi xảy ra khi nhận việc."
            }
            _isLoading.value = false
        }
    }
}