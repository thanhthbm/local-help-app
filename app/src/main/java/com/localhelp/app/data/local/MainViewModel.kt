package com.localhelp.app.data.local

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.localhelp.app.data.repository.UserRepository
import com.localhelp.app.model.response.UserResponse
import com.localhelp.app.ui.screens.Graph
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val userManager: UserManager,
    private val userRepository: UserRepository
) : ViewModel() {

    val currentUser = userManager.currentUser

    private val _startDestination = MutableStateFlow(Graph.Auth)
    val startDestination: StateFlow<String> = _startDestination.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        checkAutoLogin()
    }

    private fun checkAutoLogin() {
        val firebaseUser = FirebaseAuth.getInstance().currentUser

        if (firebaseUser != null) {
            viewModelScope.launch {
                val result = userRepository.getProfile()

                result.onSuccess { userResponse ->
                    userManager.updateProfile(userResponse)
                    _startDestination.value = Graph.Home
                }.onFailure {
                    userManager.logout()
                    _startDestination.value = Graph.Auth
                }

                _isLoading.value = false
            }
        } else {
            _startDestination.value = Graph.Auth
            _isLoading.value = false
        }
    }

    fun updateUser(user: UserResponse) {
        userManager.updateProfile(user)
    }

    fun logout() {
        userManager.logout()
    }
}