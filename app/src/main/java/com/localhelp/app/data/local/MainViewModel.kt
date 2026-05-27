package com.localhelp.app.data.local

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.localhelp.app.data.local.UserManager
import com.localhelp.app.data.repository.UserRepository
import com.localhelp.app.model.response.UserResponse
import com.localhelp.app.ui.graphnav.needsProfileSetup
import com.localhelp.app.ui.screens.Graph
import com.localhelp.app.ui.screens.Screen
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel cấp ứng dụng quản lý phiên đăng nhập và route khởi động.
 *
 * Khi app mở lại, ViewModel kiểm tra Firebase currentUser để auto-login,
 * lấy hồ sơ từ backend và quyết định vào Auth, Home hoặc SetupProfile.
 */
@HiltViewModel
class MainViewModel @Inject constructor(
    private val userManager: UserManager,
    private val userRepository: UserRepository
) : ViewModel() {

    val currentUser = userManager.currentUser

    private val _startDestination = MutableStateFlow(Graph.AUTH)
    val startDestination: StateFlow<String> = _startDestination.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _navEvent = MutableSharedFlow<String>()
    val navEvent = _navEvent.asSharedFlow()

    init {
        checkAutoLogin()
    }

    /**
     * Kiểm tra phiên Firebase còn tồn tại hay không để tự đăng nhập.
     */
    private fun checkAutoLogin() {
        val firebaseUser = FirebaseAuth.getInstance().currentUser

        if (firebaseUser != null) {
            viewModelScope.launch {
                val result = userRepository.getProfile()

                result.onSuccess { userResponse ->
                    userManager.updateProfile(userResponse)
                    _startDestination.value = if (userResponse.needsProfileSetup()) {
                        Screen.SETUP_PROFILE
                    } else {
                        Graph.HOME
                    }
                }.onFailure {
                    userManager.logout()
                    _startDestination.value = Graph.AUTH
                }

                _isLoading.value = false
            }
        } else {
            _startDestination.value = Graph.AUTH
            _isLoading.value = false
        }
    }

    /** Cập nhật user hiện tại trong UserManager. */
    fun updateUser(user: UserResponse) {
        userManager.updateProfile(user)
    }

    /** Lưu user và token sau khi LoginViewModel đăng nhập thành công. */
    fun saveSession(user: UserResponse, token: String) {
        userManager.saveSession(user, token)
    }

    /** Đăng xuất khỏi app bằng cách xóa session trong UserManager. */
    fun logout() {
        userManager.logout()
    }

    fun openResetPassword(code: String) {
        viewModelScope.launch {
            _navEvent.emit("forgot_password_root/new_password?oobCode=$code")
        }
    }
}
