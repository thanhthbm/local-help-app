package com.localhelp.app.ui.screens.profile

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.localhelp.app.data.local.UserManager
import com.localhelp.app.data.repository.UserRepository
import com.localhelp.app.model.constant.GenderEnum
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import javax.inject.Inject

data class EditProfileUiState(
    val isLoading: Boolean = false,
    val isSaveSuccess: Boolean = false,
    val errorMessage: String? = null,
    val fullName: String = "",
    val phone: String = "",
    val bio: String = "",
    val gender: GenderEnum? = null,
    val avatarUrl: String? = null,
    val localAvatarUri: Uri? = null
)

@HiltViewModel
class EditProfileViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val userManager: UserManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(EditProfileUiState())
    val uiState: StateFlow<EditProfileUiState> = _uiState.asStateFlow()

    init {
        val user = userManager.currentUser.value
        if (user != null) {
            _uiState.value = EditProfileUiState(
                fullName = user.fullName ?: "",
                phone = user.phone ?: "",
                bio = user.bio ?: "",
                gender = user.gender,
                avatarUrl = user.avatarUrl
            )
        }
    }

    fun onFullNameChange(value: String) {
        _uiState.value = _uiState.value.copy(fullName = value)
    }

    fun onPhoneChange(value: String) {
        _uiState.value = _uiState.value.copy(phone = value)
    }

    fun onBioChange(value: String) {
        _uiState.value = _uiState.value.copy(bio = value)
    }

    fun onGenderChange(value: GenderEnum) {
        _uiState.value = _uiState.value.copy(gender = value)
    }

    fun onAvatarSelected(uri: Uri) {
        _uiState.value = _uiState.value.copy(localAvatarUri = uri)
    }

    fun saveProfile(context: Context) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

            val state = _uiState.value

            // 1. Chuẩn bị phần JSON (data)
            val jsonObject = JSONObject().apply {
                if (state.fullName.isNotBlank()) put("fullName", state.fullName)
                if (state.phone.isNotBlank())    put("phone",    state.phone)
                if (state.bio.isNotBlank())      put("bio",      state.bio)
                state.gender?.let { put("gender", it.name) }
            }
            val dataPart = jsonObject.toString()
                .toRequestBody("application/json".toMediaTypeOrNull())

            // 2. Chuẩn bị phần avatar (tuỳ chọn)
            val avatarPart: MultipartBody.Part? = state.localAvatarUri?.let { uri ->
                context.contentResolver.openInputStream(uri)?.use { stream ->
                    val bytes = stream.readBytes()
                    val body = bytes.toRequestBody("image/*".toMediaTypeOrNull())
                    MultipartBody.Part.createFormData("avatar", "avatar.jpg", body)
                }
            }

            // 3. Một lần gọi API duy nhất
            val result = userRepository.updateProfile(dataPart, avatarPart)
            if (result.isSuccess) {
                userManager.updateProfile(result.getOrThrow())
                _uiState.value = _uiState.value.copy(isLoading = false, isSaveSuccess = true)
            } else {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = result.exceptionOrNull()?.message ?: "Lưu thất bại"
                )
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }
}
