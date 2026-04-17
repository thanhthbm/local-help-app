package com.localhelp.app.ui.screens.jobmanagement

import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.localhelp.app.data.remote.CloudinaryService
import com.localhelp.app.data.repository.ConversationRepository
import com.localhelp.app.data.repository.JobDetailRepository
import com.localhelp.app.model.response.JobImageResponse
import com.localhelp.app.model.response.JobResponse
import com.localhelp.app.model.response.ProgressResponse
import com.localhelp.app.model.response.ReviewResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

sealed class JobDetailHelperUiState {
    object Loading : JobDetailHelperUiState()
    data class Error(val message: String) : JobDetailHelperUiState()
    data class Success(
        val jobInfo: JobResponse,
        val progresses: List<ProgressResponse>,
        val evidenceImages: List<JobImageResponse> = emptyList(),
        val review: ReviewResponse? = null,
        val selectedLocalImages: List<Uri> = emptyList(),
        val isActionLoading: Boolean = false,
        val conversationId: String? = null,
    ) : JobDetailHelperUiState()
}

@HiltViewModel
class JobDetailHelperViewModel @Inject constructor(
    private val repository: JobDetailRepository,
    private val savedStateHandle: SavedStateHandle,
    private  val conversationRepository: ConversationRepository,
    private val cloudinaryService: CloudinaryService
) : ViewModel() {

    private val jobId: Long = savedStateHandle.get<Long>("id") ?: 0L

    private val _uiState = MutableStateFlow<JobDetailHelperUiState>(JobDetailHelperUiState.Loading)
    val uiState: StateFlow<JobDetailHelperUiState> = _uiState.asStateFlow()

    private var pollingJob: Job? = null

    init {
        fetchDetail(showLoading = true)
        startPolling()
    }

    fun refresh() = fetchDetail(false)

    private fun startPolling() {
        pollingJob?.cancel()
        pollingJob = viewModelScope.launch {
            while (true) {
                delay(15000L)
                if (_uiState.value is JobDetailHelperUiState.Success) fetchDetail(false)
            }
        }
    }

    private fun fetchDetail(showLoading: Boolean) {
        if (showLoading && _uiState.value !is JobDetailHelperUiState.Success) {
            _uiState.value = JobDetailHelperUiState.Loading
        }

        viewModelScope.launch {
            repository.getJobDetail(jobId).onSuccess { detail ->
                val status = detail.jobInfo.status?.name ?: ""

                var evidences = emptyList<JobImageResponse>()
                if (status == "PENDING_PAYMENT" || status == "COMPLETED") {
                    repository.getJobEvidence(jobId).onSuccess { evidences = it }
                }

                var review: ReviewResponse? = null
                if (status == "COMPLETED") {
                    repository.getJobReview(jobId).onSuccess { review = it }
                }

                _uiState.update { current ->
                    val local = (current as? JobDetailHelperUiState.Success)?.selectedLocalImages ?: emptyList()
                    JobDetailHelperUiState.Success(
                        jobInfo = detail.jobInfo,
                        progresses = detail.progresses,
                        evidenceImages = evidences,
                        review = review,
                        selectedLocalImages = local
                    )
                }
            }.onFailure { e ->
                if (_uiState.value !is JobDetailHelperUiState.Success) {
                    _uiState.value = JobDetailHelperUiState.Error(e.message ?: "Lỗi tải dữ liệu")
                }
            }

            if(_uiState.value is JobDetailHelperUiState.Success){
                val state = _uiState.value as JobDetailHelperUiState.Success
                state.jobInfo.creatorId.let{
                    conversationRepository.startConversation(it.toString()).onSuccess { res ->
                        _uiState.update {
                            (it as JobDetailHelperUiState.Success).copy(conversationId = res.id)
                        }
                    }
                }
            }
        }
    }

    fun updateStatusMoving() = executeAction { repository.updateStatusMoving(jobId) }

    fun updateStatusArrived() = executeAction { repository.updateStatusArrived(jobId) }

    fun submitEvidence(context: Context) {
        val currentState = _uiState.value
        if (currentState !is JobDetailHelperUiState.Success) return

        viewModelScope.launch {
            _uiState.update { currentState.copy(isActionLoading = true) }

            try {
                val uploadedUrls = coroutineScope {
                    currentState.selectedLocalImages.map { uri ->
                        async {
                            val stream = context.contentResolver.openInputStream(uri)
                            val bytes = stream?.readBytes() ?: throw Exception("Cannot read file")
                            stream.close()

                            val filePart = MultipartBody.Part.createFormData(
                                name = "file", filename = "evidence.jpg",
                                body = bytes.toRequestBody("image/jpeg".toMediaTypeOrNull())
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

                repository.submitEvidence(jobId, uploadedUrls)
                    .onSuccess {
                        _uiState.update { currentState.copy(isActionLoading = false, selectedLocalImages = emptyList()) }
                        fetchDetail(false)
                    }
                    .onFailure { throw it }

            } catch (e: Exception) {
                e.printStackTrace()
                _uiState.update { currentState.copy(isActionLoading = false) }
            }
        }
    }

    private fun executeAction(action: suspend () -> Result<Unit>) {
        val current = _uiState.value as? JobDetailHelperUiState.Success ?: return
        _uiState.value = current.copy(isActionLoading = true)
        viewModelScope.launch {
            action().onSuccess { fetchDetail(false) }
                .onFailure { _uiState.value = current.copy(isActionLoading = false) }
        }
    }

    fun addLocalImages(uris: List<Uri>) {
        val current = _uiState.value as? JobDetailHelperUiState.Success ?: return
        _uiState.value = current.copy(selectedLocalImages = current.selectedLocalImages + uris)
    }

    fun removeLocalImage(uri: Uri) {
        val current = _uiState.value as? JobDetailHelperUiState.Success ?: return
        _uiState.value = current.copy(selectedLocalImages = current.selectedLocalImages - uri)
        deleteFile(uri)
    }

    private fun clearLocalImages() {
        val current = _uiState.value as? JobDetailHelperUiState.Success ?: return
        current.selectedLocalImages.forEach { deleteFile(it) }
        _uiState.update { (it as JobDetailHelperUiState.Success).copy(selectedLocalImages = emptyList()) }
    }

    private fun deleteFile(uri: Uri) {
        try { File(uri.path ?: "").delete() } catch (e: Exception) {}
    }

    override fun onCleared() {
        super.onCleared()
        pollingJob?.cancel()
        clearLocalImages()
    }
}