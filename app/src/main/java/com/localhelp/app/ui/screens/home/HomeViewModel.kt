package com.localhelp.app.ui.screens.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.localhelp.app.data.repository.JobRepository
import com.localhelp.app.model.response.JobResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val jobRepository: JobRepository
): ViewModel() {
    val TAG = "HOME-VIEWMODEL"

    private val _recentJobs = MutableStateFlow<List<JobResponse>>(emptyList())
    val recentJobs: StateFlow<List<JobResponse>> = _recentJobs.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private var currentPage = 1
    private var pageSize = 10
    private var isLastPage = false

    init {
        loadMoreJobs()
    }

    fun loadMoreJobs(){
        if (_isLoading.value || isLastPage) return

        _isLoading.value = true

        viewModelScope.launch {
            val result = jobRepository.getOpenJobs(currentPage, pageSize)

            result.onSuccess { paginationData ->
                val newJobs = paginationData.result
                Log.d(TAG, "New jobs: $newJobs")

                _recentJobs.value = _recentJobs.value + newJobs
                isLastPage = currentPage >= paginationData.meta.pages

                if (!isLastPage) {
                    currentPage++
                }
            }.onFailure {

            }
            _isLoading.value = false
        }
    }
}