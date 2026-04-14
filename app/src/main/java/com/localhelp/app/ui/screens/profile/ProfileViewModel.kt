package com.localhelp.app.ui.screens.profile

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.localhelp.app.data.local.UserManager
import com.localhelp.app.data.repository.UserRepository
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
    savedStateHandle: SavedStateHandle
): ViewModel(){
    private val userId: Long? = savedStateHandle.get<Long>("userId")
    
    private val _user = MutableStateFlow<UserResponse?>(null)
    val user: StateFlow<UserResponse?> = _user.asStateFlow()
    
    val currentUser = userManager.currentUser
    
    val isMyProfile: Boolean = userId == null || userId == userManager.currentUser.value?.id

    init {
        if (userId != null && userId != userManager.currentUser.value?.id) {
            loadUserProfile(userId)
        } else {
            // Use current user from UserManager
            viewModelScope.launch {
                userManager.currentUser.collect {
                    _user.value = it
                }
            }
        }
    }

    private fun loadUserProfile(id: Long) {
        viewModelScope.launch {
            userRepository.getUserById(id).onSuccess {
                _user.value = it
            }.onFailure {
                // Handle error
            }
        }
    }

    fun logout(){
        userManager.logout()
    }
}