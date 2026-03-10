package com.localhelp.app.ui.screens.profile

import androidx.lifecycle.ViewModel
import com.localhelp.app.data.local.UserManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userManager: UserManager
): ViewModel(){
    val currentUser = userManager.currentUser

    fun logout(){
        userManager.logout()
    }
}