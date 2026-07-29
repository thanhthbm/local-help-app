package com.localhelp.app.ui.screens.profile

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.localhelp.app.data.local.UserManager
import com.localhelp.app.data.remote.CloudinaryService
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

/**
 * ViewModel quản lý form chỉnh sửa hồ sơ.
 *
 * Ảnh đại diện được upload trực tiếp lên Cloudinary từ app, sau đó URL được
 * gửi về backend trong multipart part "data".
 */
@HiltViewModel
class EditProfileViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val userManager: UserManager,
    private val cloudinaryService: CloudinaryService
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

    /** Cập nhật họ tên trong state form. */
    fun onFullNameChange(value: String) {
        _uiState.value = _uiState.value.copy(fullName = value)
    }

    /** Cập nhật số điện thoại trong state form. */
    fun onPhoneChange(value: String) {
        _uiState.value = _uiState.value.copy(phone = value)
    }

    /** Cập nhật phần giới thiệu bản thân. */
    fun onBioChange(value: String) {
        _uiState.value = _uiState.value.copy(bio = value)
    }

    /** Cập nhật giới tính được chọn từ dropdown. */
    fun onGenderChange(value: GenderEnum) {
        _uiState.value = _uiState.value.copy(gender = value)
    }

    /** Lưu URI ảnh local do người dùng chọn trước khi upload. */
    fun onAvatarSelected(uri: Uri) {
        _uiState.value = _uiState.value.copy(localAvatarUri = uri)
    }

    /**
     * Lưu hồ sơ.
     *
     * Nếu có ảnh local thì upload lên Cloudinary trước, sau đó gửi JSON data
     * chứa fullName/phone/bio/gender/avatarUrl lên backend.
     */
    fun saveProfile(context: Context) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            val state = _uiState.value

            var uploadedAvatarUrl: String? = null

            // 1. Upload to Cloudinary if a new local image was selected
            state.localAvatarUri?.let { uri ->
                try {
                    context.contentResolver.openInputStream(uri)?.use { stream ->
                        val bytes = stream.readBytes()
                        val filePart = MultipartBody.Part.createFormData(
                            "file", "avatar.jpg",
                            bytes.toRequestBody("image/jpeg".toMediaTypeOrNull())
                        )
                        val presetPart = "localhelp_preset".toRequestBody("text/plain".toMediaTypeOrNull())
                        
                        val cloudResponse = cloudinaryService.uploadImage(
                            cloudName = "dwtpcdjhe",
                            uploadPreset = presetPart,
                            file = filePart
                        )
                        
                        if (cloudResponse.isSuccessful) {
                            uploadedAvatarUrl = cloudResponse.body()?.secure_url
                        } else {
                            _uiState.value = _uiState.value.copy(
                                isLoading = false, 
                                errorMessage = "Lỗi upload ảnh: ${cloudResponse.code()}"
                            )
                            return@launch
                        }
                    }
                } catch (e: Exception) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false, 
                        errorMessage = "Lỗi kết nối Cloudinary: ${e.message}"
                    )
                    return@launch
                }
            }

            // 2. Prepare JSON with the new avatarUrl
            val jsonObject = JSONObject().apply {
                put("fullName", state.fullName)
                put("phone",    state.phone)
                put("bio",      state.bio)
                state.gender?.let { put("gender", it.name) }
                if (uploadedAvatarUrl != null) {
                    put("avatarUrl", uploadedAvatarUrl)
                }
            }
            val dataPart = jsonObject.toString()
                .toRequestBody("application/json".toMediaTypeOrNull())

            // 3. Call backend with null avatarPart (since we sent avatarUrl in JSON)
            val result = userRepository.updateProfile(dataPart, null)
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

    /** Xóa lỗi sau khi UI đã hiển thị snackbar. */
    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }
}
