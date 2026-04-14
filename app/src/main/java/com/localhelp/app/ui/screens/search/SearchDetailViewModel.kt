package com.localhelp.app.ui.screens.search

import android.util.Log
import androidx.compose.runtime.key
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.localhelp.app.data.repository.CategoryRepository
import com.localhelp.app.data.repository.LocationRepository
import com.localhelp.app.data.repository.SearchRepository
import com.localhelp.app.model.request.SearchJobRequest
import com.localhelp.app.model.response.CategoryResponse
import com.localhelp.app.model.response.JobResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject


sealed interface SearchDetailUiState {
    object Loading : SearchDetailUiState

    data class Success(
        val listJobs: List<JobResponse>,
        val isPaginating: Boolean = false,
        val isLastPage: Boolean = false
    ) : SearchDetailUiState

    data class Error(val message: String) : SearchDetailUiState
}

@HiltViewModel
class SearchDetailViewModel @Inject constructor(
    private val repository: SearchRepository,
    private val savedStateHandle: SavedStateHandle,
    private val locationRepository: LocationRepository,
    private val categoryRepository: CategoryRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow<SearchDetailUiState>(SearchDetailUiState.Loading)
    val uiState: StateFlow<SearchDetailUiState> = _uiState.asStateFlow()

    private val _listCategory = MutableStateFlow<List<CategoryResponse>>(emptyList())
    val listCategory: StateFlow<List<CategoryResponse>> = _listCategory.asStateFlow()

    private var currentPage = 0
    private var currentKeyword = savedStateHandle.get<String>("keyword")?: ""
    private var currentDistance: Double = 10.0
    private var currentMinSalary: Double = 0.0
    private var currentCategories: List<Long> = emptyList()
    private var currentStartTime: String? = null
    private var currentEndTime: String? = null

    fun initSearch(keyword: String) {
        currentKeyword = keyword
        loadJobs(isLoadMore = false)
    }

    init{
        getCategory()
    }

    fun getCategory(){
        viewModelScope.launch {
            val categories = categoryRepository.getCategories()
            categories.onSuccess {
                _listCategory.update { it }
            }.onFailure {
                Log.d("SearchDetailViewModel", "initSearch: ${it.message}")
            }
        }
    }

    fun applyFilters(
        keyword: String,
        distance: Float,
        minSalary: Float,
        categories: Set<Long>,
        timeFilter: String
    ) {
        currentKeyword = keyword
        currentDistance = distance.toDouble()
        currentMinSalary = (minSalary * 1000).toDouble()
        currentCategories = categories.toList().ifEmpty { emptyList() }

        val (startTime, endTime) = calculateTimeRange(timeFilter)
        currentStartTime = startTime
        currentEndTime = endTime

        currentPage = 0
        _uiState.value = SearchDetailUiState.Loading

        loadJobs(isLoadMore = false)
    }

    fun loadJobs(isLoadMore: Boolean = false) {
        val currentState = _uiState.value

        if (currentState is SearchDetailUiState.Success) {
            if (currentState.isPaginating || (isLoadMore && currentState.isLastPage)) {
                return
            }
        }
        if (isLoadMore && currentState is SearchDetailUiState.Loading) return

        if (!isLoadMore) {
            currentPage = 0
            _uiState.value = SearchDetailUiState.Loading
        } else if (currentState is SearchDetailUiState.Success) {
            _uiState.value = currentState.copy(isPaginating = true)
        }

        viewModelScope.launch {
            val position = locationRepository.getCurrentLocation()
            val request = SearchJobRequest(
                latitude = position?.latitude ?: 20.283,
                longitude = position?.longitude ?: 105.8533,
                page = currentPage,
                size = 20,
                keyword = currentKeyword,
                maxDistance = currentDistance,
                minSalary = currentMinSalary,
                categoryIds = currentCategories,
                startTime = currentStartTime,
                endTime = currentEndTime
            )

            repository.searchJobs(request).collect { result ->
                result.onSuccess { paginationDTO ->
                    val newItems = paginationDTO.result
                    val meta = paginationDTO.meta
                    val isLast = meta.page >= meta.pages

                    if (isLoadMore && currentState is SearchDetailUiState.Success) {
                        _uiState.value = SearchDetailUiState.Success(
                            listJobs = currentState.listJobs + newItems,
                            isPaginating = false,
                            isLastPage = isLast
                        )
                    } else {
                        _uiState.value = SearchDetailUiState.Success(
                            listJobs = newItems,
                            isPaginating = false,
                            isLastPage = isLast
                        )
                    }

                    if (!isLast) currentPage++

                }.onFailure { error ->
                    if (!isLoadMore) {
                        _uiState.value = SearchDetailUiState.Error(error.message ?: "Lỗi không xác định")
                    } else if (currentState is SearchDetailUiState.Success) {
                        _uiState.value = currentState.copy(isPaginating = false)
                    }
                }
            }
        }
    }

    private fun calculateTimeRange(timeFilter: String): Pair<String?, String?> {
        val formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME
        val today = LocalDate.now()

        return when (timeFilter) {
            "TODAY" -> {
                val start = today.atStartOfDay().format(formatter)
                val end = today.atTime(LocalTime.MAX).format(formatter)
                Pair(start, end)
            }
            "TOMORROW" -> {
                val start = today.plusDays(1).atStartOfDay().format(formatter)
                val end = today.plusDays(1).atTime(LocalTime.MAX).format(formatter)
                Pair(start, end)
            }
            "THIS_WEEK" -> {
                val start = today.atStartOfDay().format(formatter)
                val end = today.plusDays(7).atTime(LocalTime.MAX).format(formatter)
                Pair(start, end)
            }
            else -> Pair(null, null)
        }
    }
}