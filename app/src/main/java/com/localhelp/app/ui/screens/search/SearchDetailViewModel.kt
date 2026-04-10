package com.localhelp.app.ui.screens.search

import android.util.Log
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.localhelp.app.data.repository.SearchRepository
import com.localhelp.app.model.response.JobResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


sealed interface SearchDetailUiState{
    object Loading : SearchDetailUiState
    data class Success(val listJobs: List<JobResponse>) : SearchDetailUiState
}

@HiltViewModel
class SearchDetailViewModel @Inject constructor(
    repository: SearchRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val _uiState = MutableStateFlow<SearchDetailUiState>(SearchDetailUiState.Loading)
    val uiState: StateFlow<SearchDetailUiState> = _uiState.asStateFlow()

    init{
        viewModelScope.launch{
            val keyword: String = savedStateHandle.get<String>("keyword") ?: ""

            val listJobsResult = repository.searchJob(keyword)
            listJobsResult.onSuccess { listJobs ->
                _uiState.update{ SearchDetailUiState.Success(listJobs) }
            }.onFailure { exception ->
                Log.d("Search Job", exception.message.toString())
            }
        }
    }
}