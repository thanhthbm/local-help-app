package com.localhelp.app.ui.screens.jobmanagement

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.localhelp.app.data.repository.ConversationRepository
import com.localhelp.app.data.repository.JobDetailRepository
import com.localhelp.app.model.response.ApplicationResponse
import com.localhelp.app.model.response.JobImageResponse
import com.localhelp.app.model.response.JobResponse
import com.localhelp.app.model.response.ProgressResponse
import com.localhelp.app.model.response.ReviewResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

sealed class JobDetailOwnerUiState {
    object Loading : JobDetailOwnerUiState()
    data class Error(val message: String) : JobDetailOwnerUiState()
    data class Success(
        val jobInfo: JobResponse,
        val progresses: List<ProgressResponse>,
        val applications: List<ApplicationResponse> = emptyList(),
        val evidenceImages: List<JobImageResponse> = emptyList(),
        val review: ReviewResponse? = null,
        val isActionLoading: Boolean = false,
        val conversationId: String? = null
    ) : JobDetailOwnerUiState()
}


@HiltViewModel
class JobDetailOwnerViewModel @Inject constructor(
    private val repository: JobDetailRepository,
    private val savedStateHandle: SavedStateHandle,
    private val conversationRepository: ConversationRepository
) : ViewModel() {

    private val jobId: Long = savedStateHandle.get<Long>("id") ?: 0L

    private val _uiState = MutableStateFlow<JobDetailOwnerUiState>(JobDetailOwnerUiState.Loading)
    val uiState: StateFlow<JobDetailOwnerUiState> = _uiState.asStateFlow()

    private var pollingJob: Job? = null

    init {
        fetchDetail(showLoading = true)
        startPolling()
    }

    fun refresh() = fetchDetail(showLoading = false)

    private fun startPolling() {
        pollingJob?.cancel()
        pollingJob = viewModelScope.launch {
            while (true) {
                delay(15000L)
                if (_uiState.value is JobDetailOwnerUiState.Success) {
                    fetchDetail(showLoading = false)
                }
            }
        }
    }

    private fun fetchDetail(showLoading: Boolean) {
        if (showLoading && _uiState.value !is JobDetailOwnerUiState.Success) {
            _uiState.value = JobDetailOwnerUiState.Loading
        }

        viewModelScope.launch {
            repository.getJobDetail(jobId).onSuccess { detail ->
                val status = detail.jobInfo.status?.name ?: ""
                val isJobOpen = status == "OPEN"

                var apps = emptyList<ApplicationResponse>()
                if (status == "OPEN") {
                    repository.getJobApplications(jobId).onSuccess { apps = it }
                }

                var evidences = emptyList<JobImageResponse>()
                if (status == "PENDING_PAYMENT" || status == "COMPLETED") {
                    repository.getJobEvidence(jobId).onSuccess { evidences = it }
                }

                var review: ReviewResponse? = null
                if (status == "COMPLETED") {
                    repository.getJobReview(jobId).onSuccess { review = it }
                }

                val initialProgress = ProgressResponse(
                    stepName = "OPEN",
                    description = "Hệ thống đang tìm kiếm thợ phù hợp",
                    time = detail.jobInfo.createdAt,
                    isCompleted = !isJobOpen,
                    isCurrent = isJobOpen
                )

                val fullProgresses = mutableListOf<ProgressResponse>()
                fullProgresses.add(initialProgress)

                fullProgresses.addAll(detail.progresses)

                _uiState.update {
                    JobDetailOwnerUiState.Success(
                        jobInfo = detail.jobInfo,
                        progresses = fullProgresses,
                        applications = apps,
                        evidenceImages = evidences,
                        review = review
                    )
                }
            }.onFailure { e ->
                if (_uiState.value !is JobDetailOwnerUiState.Success) {
                    _uiState.value = JobDetailOwnerUiState.Error(e.message ?: "Lỗi hệ thống")
                }
            }

            if(_uiState.value is JobDetailOwnerUiState.Success){
                val state = _uiState.value as JobDetailOwnerUiState.Success
                state.jobInfo.helperId?.let{
                    conversationRepository.startConversation(it.toString()).onSuccess { res ->
                        _uiState.update {
                            (it as JobDetailOwnerUiState.Success).copy(conversationId = res.id)
                        }
                    }
                }
            }
        }
    }

    fun acceptApplication(appId: Long) = executeAction { repository.acceptApplication(appId) }

    fun confirmPayment() = executeAction { repository.confirmPayment(jobId) }

    fun submitReview(rating: Int, comment: String) = executeAction {
        repository.submitReview(jobId, rating, comment)
    }

    private fun executeAction(action: suspend () -> Result<Unit>) {
        val current = _uiState.value as? JobDetailOwnerUiState.Success ?: return
        _uiState.value = current.copy(isActionLoading = true)
        viewModelScope.launch {
            action().onSuccess { fetchDetail(false) }
                .onFailure { _uiState.value = current.copy(isActionLoading = false) }
        }
    }

    override fun onCleared() {
        super.onCleared()
        pollingJob?.cancel()
    }
}