package com.localhelp.app.ui.screens.jobdetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import com.localhelp.app.data.local.JobPreferences
import com.localhelp.app.data.local.UserManager
import com.localhelp.app.data.repository.ChatRepository
import com.localhelp.app.data.repository.JobRepository
import com.localhelp.app.model.constant.JobStatus
import com.localhelp.app.model.response.JobResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel quản lý màn chi tiết công việc.
 *
 * Với chủ công việc, ViewModel phát sự kiện điều hướng sang form cập nhật hoặc
 * gọi repository để hủy công việc. Với người khác, ViewModel xử lý chat và nhận việc.
 */
@HiltViewModel
class JobDetailViewModel @Inject constructor(
    private val jobRepository: JobRepository,
    private val chatRepository: ChatRepository,
    private val userManager: UserManager,
    private val jobPreferences: JobPreferences,
    savedStateHandle: SavedStateHandle
): ViewModel() {

    private val _job = MutableStateFlow<JobResponse?>(null)
    val job = _job.asStateFlow()

    val currentUser = userManager.currentUser

    private val _isLoading = MutableStateFlow(true)
    val isLoading = _isLoading.asStateFlow()

    private val _isApplied = MutableStateFlow(false)
    val isApplied = _isApplied.asStateFlow()

    private val _acceptStatus = MutableStateFlow<Boolean?>(null)
    val acceptStatus = _acceptStatus.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage = _errorMessage.asStateFlow()

    private val _navigationEvent = MutableSharedFlow<JobDetailNavEvent>()
    val navigationEvent = _navigationEvent.asSharedFlow()

    /**
     * Các sự kiện điều hướng một lần phát ra từ màn chi tiết công việc.
     */
    sealed class JobDetailNavEvent {
        data class NavigateToChat(val conversationId: String, val partnerName: String, val partnerAvatar: String?, val partnerId: Long) : JobDetailNavEvent()
        data class NavigateToEditJob(val jobId: Long) : JobDetailNavEvent()
        data class NavigateToUserProfile(val userId: Long) : JobDetailNavEvent()
        object NavigateToSuccess : JobDetailNavEvent()
        object JobDeleted : JobDetailNavEvent()
    }

    init {
        // Tự động lấy ID từ NavGraph và fetch data
        // Thử lấy Long, nếu null thử lấy String rồi convert (đề phòng NavType mismatch)
        val jobId: Long? = savedStateHandle.get<Long>("id") ?: savedStateHandle.get<String>("id")?.toLongOrNull()
        val userId = userManager.currentUser.value?.id
        
        if (jobId != null) {
            if (userId != null) {
                _isApplied.value = jobPreferences.isJobApplied(jobId, userId)
            }
            fetchJob(jobId)
            observeJobStatus(jobId)
        } else {
            _isLoading.value = false
            _errorMessage.value = "Mã công việc không hợp lệ."
        }
    }

    private fun observeJobStatus(jobId: Long) {
        viewModelScope.launch {
            try {
                listenToJobUpdates(jobId).collectLatest { statusStr ->
                    val currentJob = _job.value
                    if (currentJob != null && statusStr != null) {
                        try {
                            val newStatus = JobStatus.valueOf(statusStr)
                            if (currentJob.status != newStatus) {
                                fetchJob(jobId)
                            }
                        } catch (_: Exception) {}
                    }
                }
            } catch (e: Exception) {
                android.util.Log.e("JobDetailVM", "Firestore error ignored: ${e.message}")
            }
        }
    }

    private fun listenToJobUpdates(jobId: Long): Flow<String?> = callbackFlow {
        try {
            val db = FirebaseFirestore.getInstance()
            val docRef = db.collection("job_updates").document(jobId.toString())
            
            val registration = docRef.addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                if (snapshot != null && snapshot.exists()) {
                    val status = snapshot.getString("status")
                    trySend(status)
                }
            }
            awaitClose { registration.remove() }
        } catch (e: Exception) {
            e.printStackTrace()
            close(e)
        }
    }

    /**
     * Tải chi tiết công việc để hiển thị và làm nguồn dữ liệu cho thao tác sửa/hủy.
     *
     * @param id ID công việc cần tải.
     */
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

    /**
     * Bắt đầu cuộc trò chuyện với người đăng công việc hiện tại.
     */
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
                        partnerAvatar = currentJob.creatorAvatar,
                        partnerId = currentJob.creatorId
                    )
                )
            }.onFailure { error ->
                _errorMessage.value = "Không thể bắt đầu trò chuyện: ${error.message}"
            }
            _isLoading.value = false
        }
    }

    /**
     * Gửi yêu cầu nhận công việc hiện tại.
     */
    fun acceptJob() {
        val currentJob = _job.value ?: return
        val userId = userManager.currentUser.value?.id

        _isLoading.value = true
        _errorMessage.value = null

        viewModelScope.launch {
            val result = jobRepository.acceptJob(currentJob.id)
            result.onSuccess { _ ->
                if (userId != null) {
                    jobPreferences.setJobApplied(currentJob.id, userId)
                    _isApplied.value = true
                }
                _navigationEvent.emit(JobDetailNavEvent.NavigateToSuccess)
            }.onFailure { error ->
                _acceptStatus.value = false
                _errorMessage.value = error.message ?: "Có lỗi xảy ra khi nhận việc."
            }
            _isLoading.value = false
        }
    }

    /**
     * Điều hướng sang màn CreateJob ở chế độ cập nhật công việc.
     */
    fun onEditClick() {
        _job.value?.let {
            viewModelScope.launch {
                _navigationEvent.emit(JobDetailNavEvent.NavigateToEditJob(it.id))
            }
        }
    }

    /**
     * Hủy công việc hiện tại và phát sự kiện quay lại khi backend xử lý thành công.
     */
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

    /**
     * Điều hướng sang hồ sơ người đăng công việc.
     */
    fun onUserClick() {
        _job.value?.let {
            viewModelScope.launch {
                _navigationEvent.emit(JobDetailNavEvent.NavigateToUserProfile(it.creatorId))
            }
        }
    }
}
