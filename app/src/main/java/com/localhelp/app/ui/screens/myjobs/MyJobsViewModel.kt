package com.localhelp.app.ui.screens.myjobs

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.localhelp.app.data.repository.JobRepository
import com.localhelp.app.model.constant.JobStatus
import com.localhelp.app.model.response.JobResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyJobsViewModel @Inject constructor(
    private val jobRepository: JobRepository
) : ViewModel() {

    val TAG = "FETCH_MY_JOBS"

    val filters = listOf("Tất cả", "Đang tìm người", "Đang thực hiện", "Đã hoàn thành")

    private val _selectedFilter = MutableStateFlow(filters[0])
    val selectedFilter: StateFlow<String> = _selectedFilter.asStateFlow()

    private val _jobs = MutableStateFlow<List<JobResponse>>(emptyList())
    val jobs: StateFlow<List<JobResponse>> = _jobs.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        fetchMyJobs(null)
    }

    fun setFilter(filter: String) {
        if (_selectedFilter.value == filter) return

        _selectedFilter.value = filter

        val statusQuery = when (filter) {
            "Đang tìm người" -> JobStatus.OPEN
            "Đang thực hiện" -> JobStatus.ACCEPTED
            "Đã hoàn thành" -> JobStatus.COMPLETED
            else -> null
        }

        fetchMyJobs(statusQuery)
    }

    fun fetchMyJobs(status: JobStatus?) {
        _isLoading.value = true
        viewModelScope.launch {
            val result = jobRepository.getMyJobs(status)
            Log.d(TAG, "fetchMyJobs: $result")

            result.onSuccess { list ->
                _jobs.value = list
            }.onFailure {
                _jobs.value = emptyList()
            }

            _isLoading.value = false
        }
    }

    fun deleteJob(jobId: Long) {
        viewModelScope.launch {
            val result = jobRepository.deleteJob(jobId)
            result.onSuccess {
                fetchMyJobs(null)
            }
        }
    }
}