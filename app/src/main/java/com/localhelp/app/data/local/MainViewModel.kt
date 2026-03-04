package com.localhelp.app.data.local

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.localhelp.app.model.response.UserResponse
import com.localhelp.app.ui.screens.Graph
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class MainViewModel: ViewModel() {
    private val _userState = MutableStateFlow(UserState())
    val userState: StateFlow<UserState> = _userState.asStateFlow()
    private val _startDestination = MutableStateFlow(Graph.Auth)
    val startDestination: StateFlow<String> = _startDestination.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        checkAutoLogin()
    }

    private fun checkAutoLogin() {
        val currentUser = FirebaseAuth.getInstance().currentUser


        if (currentUser != null) {
            // Nếu Firebase đã lưu phiên đăng nhập -> Vào thẳng Home
            _startDestination.value = Graph.Home

            // TODO: (Tùy chọn) Gọi API lấy thông tin UserResponse từ backend để gán vào _userState
        } else {
            // Chưa đăng nhập -> Vào luồng Auth (Login)
            _startDestination.value = Graph.Auth
        }

        // Đã kiểm tra xong, báo cho UI biết để render
        _isLoading.value = false
    }

    fun updateUser(user: UserResponse){
        _userState.value = _userState.value.copy(user = user)
    }

    fun logout() {
        FirebaseAuth.getInstance().signOut() // Đăng xuất khỏi Firebase
        _userState.value = UserState(user = null, token = null)
        _startDestination.value = Graph.Auth // Đặt lại đích đến
    }
}