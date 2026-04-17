package com.localhelp.app.ui.screens.profile

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.localhelp.app.data.local.UserManager
import com.localhelp.app.data.repository.JobRepository
import com.localhelp.app.data.repository.UserRepository
import com.localhelp.app.model.response.JobResponse
import com.localhelp.app.model.response.UserResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userManager: UserManager,
    private val userRepository: UserRepository,
    private val jobRepository: JobRepository,
    private val reviewRepository: com.localhelp.app.data.repository.ReviewRepository,
    savedStateHandle: SavedStateHandle
): ViewModel(){
    private val userId: Long? = savedStateHandle.get<Long>("userId")
    
    private val _user = MutableStateFlow<UserResponse?>(null)
    val user: StateFlow<UserResponse?> = _user.asStateFlow()
    
    val currentUser = userManager.currentUser
    
    val isMyProfile: Boolean = userId == null || userId == userManager.currentUser.value?.id

    private val _jobs = MutableStateFlow<List<JobResponse>>(emptyList())
    val jobs: StateFlow<List<JobResponse>> = _jobs.asStateFlow()

    private val _reviews = MutableStateFlow<List<com.localhelp.app.model.response.ReviewResponse>>(emptyList())
    val reviews: StateFlow<List<com.localhelp.app.model.response.ReviewResponse>> = _reviews.asStateFlow()

    private val _isLoadingJobs = MutableStateFlow(false)
    val isLoadingJobs: StateFlow<Boolean> = _isLoadingJobs.asStateFlow()

    private val _isLoadingReviews = MutableStateFlow(false)
    val isLoadingReviews: StateFlow<Boolean> = _isLoadingReviews.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    init {
        val targetId = userId ?: userManager.currentUser.value?.id
        if (targetId != null) {
            refresh()
        }
        
        // Nếu là profile của mình, lắng nghe sự thay đổi từ UserManager
        if (isMyProfile) {
            viewModelScope.launch {
                userManager.currentUser.collect {
                    if (it != null) {
                        _user.value = it
                    }
                }
            }
        }
    }

    fun refresh() {
        val targetId = userId ?: userManager.currentUser.value?.id ?: return
        viewModelScope.launch {
            _isRefreshing.value = true
            loadUserProfile(targetId)
            loadUserJobs(targetId)
            loadUserReviews(targetId)
            _isRefreshing.value = false
        }
    }

    private fun loadUserProfile(id: Long) {
        viewModelScope.launch {
            userRepository.getUserById(id).onSuccess {
                _user.value = it
            }
        }
    }

    private fun loadUserJobs(id: Long) {
        viewModelScope.launch {
            _isLoadingJobs.value = true
            jobRepository.getMyPosts(1, 20, id).onSuccess {
                _jobs.value = it.result
            }
            _isLoadingJobs.value = false
        }
    }

    private fun loadUserReviews(id: Long) {
        viewModelScope.launch {
            _isLoadingReviews.value = true
            reviewRepository.getReviewsByUser(id, 1, 5).onSuccess {
                _reviews.value = it.result
            }
            _isLoadingReviews.value = false
        }
    }

    fun logout(){
        userManager.logout()
    }
}