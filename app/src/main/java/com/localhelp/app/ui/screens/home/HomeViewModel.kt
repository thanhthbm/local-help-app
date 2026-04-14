package com.localhelp.app.ui.screens.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.localhelp.app.data.repository.CategoryRepository
import com.localhelp.app.data.repository.ConversationRepository
import com.localhelp.app.data.repository.JobRepository
import com.localhelp.app.data.repository.LocationRepository
import com.localhelp.app.model.response.CategoryResponse
import com.localhelp.app.model.response.ConversationResponse
import com.localhelp.app.model.response.JobResponse
import com.trackasia.android.geometry.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val jobRepository: JobRepository,
    private val categoryRepository: CategoryRepository,
    private val conversationRepository: ConversationRepository,
    private val locationRepository: LocationRepository
): ViewModel() {
    val TAG = "HOME-VIEWMODEL"

    private val _recentJobs = MutableStateFlow<List<JobResponse>>(emptyList())
    val recentJobs: StateFlow<List<JobResponse>> = _recentJobs.asStateFlow()

    private val _featuredJobs = MutableStateFlow<List<JobResponse>>(emptyList())
    val featuredJobs: StateFlow<List<JobResponse>> = _featuredJobs.asStateFlow()

    private val _categories = MutableStateFlow<List<CategoryResponse>>(emptyList())
    val categories: StateFlow<List<CategoryResponse>> = _categories.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _currentLocation = MutableStateFlow<LatLng?>(null)
    val currentLocation: StateFlow<LatLng?> = _currentLocation.asStateFlow()

    private val _navigateToChat = MutableSharedFlow<ConversationResponse>()
    val navigateToChat: SharedFlow<ConversationResponse> = _navigateToChat.asSharedFlow()

    private var currentPage = 1
    private var pageSize = 10
    private var isLastPage = false

    private val _currentAddress = MutableStateFlow<String>("Đang tải...")
    val currentAddress: StateFlow<String> = _currentAddress.asStateFlow()

    private val _selectedCategoryId = MutableStateFlow<Long?>(null)
    val selectedCategoryId: StateFlow<Long?> = _selectedCategoryId.asStateFlow()

    init {
        loadCurrentLocation()
        loadCategories()
        loadFeaturedJobs()
        loadMoreJobs()
    }

    fun onCategorySelected(categoryId: Long?) {
        if (_selectedCategoryId.value == categoryId) return
        _selectedCategoryId.value = categoryId
        
        // Reset pagination and clear current jobs
        currentPage = 1
        isLastPage = false
        _recentJobs.value = emptyList()
        
        loadMoreJobs()
    }

    private fun loadCategories() {
        viewModelScope.launch {
            categoryRepository.getCategories().onSuccess { categories ->
                _categories.value = categories
            }
        }
    }

    private fun loadFeaturedJobs() {
        viewModelScope.launch {
            jobRepository.getFeaturedJobs().onSuccess { jobs ->
                _featuredJobs.value = jobs
            }
        }
    }

    fun loadCurrentLocation() {
        viewModelScope.launch {
            val location = locationRepository.getCurrentLocation()
            _currentLocation.value = location
            if (location != null) {
                _currentAddress.value = "Hà Nội (${String.format("%.4f", location.latitude)}, ${String.format("%.4f", location.longitude)})"
                // Refresh jobs with location if they are already loaded or loading for the first time
                if (currentPage == 1) {
                    _recentJobs.value = emptyList()
                    loadMoreJobs()
                }
            } else {
                _currentAddress.value = "Hà Nội"
            }
        }
    }

    fun loadMoreJobs(){
        if (_isLoading.value || isLastPage) return

        _isLoading.value = true

        viewModelScope.launch {
            val result = jobRepository.getOpenJobs(
                current = currentPage,
                pageSize = pageSize,
                categoryId = _selectedCategoryId.value,
                lat = _currentLocation.value?.latitude,
                lng = _currentLocation.value?.longitude
            )

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

    fun startChatWithUser(userId: Long) {
        viewModelScope.launch {
            val result = conversationRepository.startConversation(userId.toString())
            result.onSuccess { conversation ->
                _navigateToChat.emit(conversation)
            }
        }
    }
}