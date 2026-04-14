package com.localhelp.app.ui.screens.jobdetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.localhelp.app.data.repository.ChatRepository
import com.localhelp.app.data.repository.JobRepository
import com.localhelp.app.model.response.JobResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class JobDetailViewModel @Inject constructor(
    private val jobRepository: JobRepository,
    private val chatRepository: ChatRepository,
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

    private val _navigationEvent = MutableSharedFlow<JobDetailNavEvent>()
    val navigationEvent = _navigationEvent.asSharedFlow()

    sealed class JobDetailNavEvent {
        data class NavigateToChat(val conversationId: String, val partnerName: String, val partnerAvatar: String?) : JobDetailNavEvent()
        data class NavigateToEditJob(val jobId: Long) : JobDetailNavEvent()
        data class NavigateToUserProfile(val userId: Long) : JobDetailNavEvent()
        object JobDeleted : JobDetailNavEvent()
    }

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
            }.onFailure { e ->
                _errorMessage.value = e.message
            }
            _isLoading.value = false
        }
    }

    fun onChatClick() {
        val currentJob = _job.value ?: return
        viewModelScope.launch {
            _isLoading.value = true
            val result = chatRepository.startConversation(currentJob.creatorId.toString())
            result.onSuccess { conversation ->
                _navigationEvent.emit(
                    JobDetailNavEvent.NavigateToChat(
                        conversationId = conversation.id,
                        partnerName = currentJob.creatorName ?: "Người dùng",
                        partnerAvatar = currentJob.creatorAvatar
                    )
                )
            }.onFailure { error ->
                _errorMessage.value = "Không thể bắt đầu trò chuyện: ${error.message}"
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

    fun onEditClick() {
        _job.value?.let {
            viewModelScope.launch {
                _navigationEvent.emit(JobDetailNavEvent.NavigateToEditJob(it.id))
            }
        }
    }

    fun deleteJob() {
        val jobId = _job.value?.id ?: return
        _isLoading.value = true
        viewModelScope.launch {
            val result = jobRepository.deleteJob(jobId)
            result.onSuccess {
                _navigationEvent.emit(JobDetailNavEvent.JobDeleted)
            }.onFailure { e ->
                _errorMessage.value = "Không thể xóa công việc: ${e.message}"
            }
            _isLoading.value = false
        }
    }

    fun onUserClick() {
        _job.value?.let {
            viewModelScope.launch {
                _navigationEvent.emit(JobDetailNavEvent.NavigateToUserProfile(it.creatorId))
            }
        }
    }
}
