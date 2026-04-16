package com.localhelp.app.ui.screens.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.localhelp.app.data.repository.CategoryRepository
import com.localhelp.app.data.repository.ConversationRepository
import com.localhelp.app.data.repository.JobRepository
import com.localhelp.app.data.repository.LocationRepository
import com.localhelp.app.data.repository.MapRepository
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
    private val locationRepository: LocationRepository,
    private val mapRepository: MapRepository
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
        refreshAll()
    }

    fun refreshAll() {
        if (_isLoading.value) return
        
        viewModelScope.launch {
            _isLoading.value = true
            currentPage = 1
            isLastPage = false
            _recentJobs.value = emptyList() 
            
            // Wait for location if possible to avoid multiple reloads
            loadCurrentLocation()
            loadCategories()
            loadFeaturedJobs()
            
            // Small delay to let loadCurrentLocation start
            kotlinx.coroutines.delay(200)

            val lat = _currentLocation.value?.latitude
            val lng = _currentLocation.value?.longitude
            
            val result = jobRepository.getOpenJobs(
                current = 1,
                pageSize = pageSize,
                categoryId = _selectedCategoryId.value,
                lat = lat,
                lng = lng
            )

            result.onSuccess { paginationData ->
                _recentJobs.value = paginationData.result
                isLastPage = 1 >= paginationData.meta.pages
                currentPage = if (isLastPage) 1 else 2
                
                _isLoading.value = false
            }.onFailure {
                _isLoading.value = false
            }
        }
    }

    fun onCategorySelected(categoryId: Long?) {
        if (_selectedCategoryId.value == categoryId) {
            _selectedCategoryId.value = null // Deselect if clicking the same one
        } else {
            _selectedCategoryId.value = categoryId
        }
        
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
            if (location != null && (_currentLocation.value == null || 
                location.latitude != _currentLocation.value?.latitude || 
                location.longitude != _currentLocation.value?.longitude)) {
                
                _currentLocation.value = location
                // Hiển thị tọa độ tạm thời
                _currentAddress.value = "(${String.format("%.4f", location.latitude)}, ${String.format("%.4f", location.longitude)})"
                
                mapRepository.reverseGeocode(location.latitude, location.longitude)
                    .onSuccess { response ->
                        val label = response.features.firstOrNull()?.properties?.label
                        if (!label.isNullOrEmpty()) {
                            _currentAddress.value = label
                        }
                    }

                // CHỈ load lại nếu đây là lần đầu hoặc refresh
                if (recentJobs.value.isEmpty()) {
                    refreshAll()
                }
            } else if (location == null) {
                _currentAddress.value = "Hà Nội"
                if (recentJobs.value.isEmpty()) {
                    refreshAll()
                }
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