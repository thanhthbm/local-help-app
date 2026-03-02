package com.localhelp.app.data.local

import androidx.lifecycle.ViewModel
import com.localhelp.app.model.response.UserResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class MainViewModel: ViewModel() {
    private val _userState = MutableStateFlow(UserState())
    val userState: StateFlow<UserState> = _userState.asStateFlow()

    fun updateUser(user: UserResponse){
        _userState.value = _userState.value.copy(user = user)
    }

    fun logout(){
        _userState.value = UserState(user=null, token = null)
    }
}