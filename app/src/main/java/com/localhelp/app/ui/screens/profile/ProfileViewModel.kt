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
    savedStateHandle: SavedStateHandle
): ViewModel(){
    private val userId: Long? = savedStateHandle.get<Long>("userId")
    
    private val _user = MutableStateFlow<UserResponse?>(null)
    val user: StateFlow<UserResponse?> = _user.asStateFlow()
    
    val currentUser = userManager.currentUser
    
    val isMyProfile: Boolean = userId == null || userId == userManager.currentUser.value?.id

    private val _jobs = MutableStateFlow<List<JobResponse>>(emptyList())
    val jobs: StateFlow<List<JobResponse>> = _jobs.asStateFlow()

    private val _isLoadingJobs = MutableStateFlow(false)
    val isLoadingJobs: StateFlow<Boolean> = _isLoadingJobs.asStateFlow()

    init {
        val targetId = userId ?: userManager.currentUser.value?.id
        if (targetId != null) {
            loadUserProfile(targetId)
            loadUserJobs(targetId)
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
            // Sử dụng getMyPosts với userId của người khác
            jobRepository.getMyPosts(1, 20, id).onSuccess {
                _jobs.value = it.result
            }
            _isLoadingJobs.value = false
        }
    }

    fun logout(){
        userManager.logout()
    }
}