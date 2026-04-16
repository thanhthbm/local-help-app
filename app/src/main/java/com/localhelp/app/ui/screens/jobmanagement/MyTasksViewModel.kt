package com.localhelp.app.ui.screens.jobmanagement

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.localhelp.app.data.repository.JobRepository
import com.localhelp.app.model.response.JobResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class MyTasksUiState(
    val isLoading: Boolean = false,
    val isPaginating: Boolean = false,
    val jobs: List<JobResponse> = emptyList(),
    val error: String? = null,
    val isLastPage: Boolean = false
)

@HiltViewModel
class MyTasksViewModel @Inject constructor(private val repository: JobRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(MyTasksUiState())
    val uiState: StateFlow<MyTasksUiState> = _uiState.asStateFlow()

    private var currentPage = 1

    init {
        loadMyTasks(isLoadMore = false)
    }

    fun loadMyTasks(isLoadMore: Boolean) {
        val currentState = _uiState.value
        if (currentState.isLoading || currentState.isPaginating) return
        if (isLoadMore && currentState.isLastPage) return

        if (isLoadMore) {
            _uiState.update { it.copy(isPaginating = true) }
        } else {
            currentPage = 1
            _uiState.update { it.copy(isLoading = true, error = null) }
        }

        viewModelScope.launch {
            val response = repository.getMyTasks(page = currentPage, size = 10, lat = null, lng = null)

            response.onFailure { exception ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        isPaginating = false,
                        error = exception.message ?: "Không thể tải dữ liệu."
                    )
                }
            }.onSuccess { paginationData ->
                val newJobs = paginationData.result ?: emptyList()
                val meta = paginationData.meta

                val isLast = meta == null || currentPage >= meta.pages

                _uiState.update { state ->
                    state.copy(
                        isLoading = false,
                        isPaginating = false,
                        jobs = if (isLoadMore) state.jobs + newJobs else newJobs,
                        isLastPage = isLast
                    )
                }

                if (!isLast) currentPage++
            }
        }
    }
}