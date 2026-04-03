package com.localhelp.app.ui.screens.jobdetail

import androidx.lifecycle.ViewModel
import com.localhelp.app.data.repository.JobRepository
import com.localhelp.app.model.response.JobResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class JobDetailViewModel @Inject constructor(
    private val jobRepository: JobRepository
): ViewModel() {
    private val _job = MutableStateFlow<JobResponse?>(null)
    val job = _job.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    fun fetchJob(){

    }
}