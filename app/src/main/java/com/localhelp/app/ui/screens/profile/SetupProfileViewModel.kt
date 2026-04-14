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

data class SetupProfileUiState(
    val isLoading: Boolean = false,
    val isSaved: Boolean = false,
    val errorMessage: String? = null,
    val fullName: String = "",
    val phone: String = "",
    val bio: String = "",
    val gender: GenderEnum? = null,
    val localAvatarUri: Uri? = null
)

@HiltViewModel
class SetupProfileViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val userManager: UserManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(SetupProfileUiState())
    val uiState: StateFlow<SetupProfileUiState> = _uiState.asStateFlow()

    fun onFullNameChange(v: String)  { _uiState.value = _uiState.value.copy(fullName = v) }
    fun onPhoneChange(v: String)     { _uiState.value = _uiState.value.copy(phone = v) }
    fun onBioChange(v: String)       { _uiState.value = _uiState.value.copy(bio = v) }
    fun onGenderChange(v: GenderEnum){ _uiState.value = _uiState.value.copy(gender = v) }
    fun onAvatarSelected(uri: Uri)   { _uiState.value = _uiState.value.copy(localAvatarUri = uri) }
    fun clearError()                  { _uiState.value = _uiState.value.copy(errorMessage = null) }

    fun saveProfile(context: Context) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            val state = _uiState.value

            // Part 1: JSON data
            val json = JSONObject().apply {
                put("fullName", state.fullName)
                put("phone",    state.phone)
                put("bio",      state.bio)
                state.gender?.let { put("gender", it.name) }
            }
            val dataPart = json.toString().toRequestBody("application/json".toMediaTypeOrNull())

            // Part 2: avatar (optional)
            val avatarPart: MultipartBody.Part? = state.localAvatarUri?.let { uri ->
                context.contentResolver.openInputStream(uri)?.use { stream ->
                    val bytes = stream.readBytes()
                    val body = bytes.toRequestBody("image/jpeg".toMediaTypeOrNull())
                    MultipartBody.Part.createFormData("avatar", "avatar.jpg", body)
                }
            }

            val result = userRepository.updateProfile(dataPart, avatarPart)
            if (result.isSuccess) {
                userManager.updateProfile(result.getOrThrow())
                _uiState.value = _uiState.value.copy(isLoading = false, isSaved = true)
            } else {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = result.exceptionOrNull()?.message ?: "Lưu thất bại"
                )
            }
        }
    }
}
