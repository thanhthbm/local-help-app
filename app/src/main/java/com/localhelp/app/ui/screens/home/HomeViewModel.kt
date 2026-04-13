package com.localhelp.app.ui.screens.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.localhelp.app.data.repository.ConversationRepository
import com.localhelp.app.data.repository.JobRepository
import com.localhelp.app.data.repository.LocationRepository
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
    private val conversationRepository: ConversationRepository,
    private val locationRepository: LocationRepository
): ViewModel() {
    val TAG = "HOME-VIEWMODEL"

    private val _recentJobs = MutableStateFlow<List<JobResponse>>(emptyList())
    val recentJobs: StateFlow<List<JobResponse>> = _recentJobs.asStateFlow()

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

    init {
        loadCurrentLocation()
        loadMoreJobs()
    }

    fun loadCurrentLocation() {
        viewModelScope.launch {
            val location = locationRepository.getCurrentLocation()
            _currentLocation.value = location
            if (location != null) {
                // In a real app, you would use Geocoder here. For now, let's just show coords or a placeholder
                _currentAddress.value = "Hà Nội (${String.format("%.4f", location.latitude)}, ${String.format("%.4f", location.longitude)})"
            } else {
                _currentAddress.value = "Hà Nội"
            }
        }
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

    fun startChatWithUser(userId: Long) {
        viewModelScope.launch {
            val result = conversationRepository.startConversation(userId.toString())
            result.onSuccess { conversation ->
                _navigateToChat.emit(conversation)
            }
        }
    }
}