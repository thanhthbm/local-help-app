package com.localhelp.app.ui.screens.messages

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.localhelp.app.data.local.UserManager
import com.localhelp.app.data.repository.ChatRepository
import com.localhelp.app.model.response.FirestoreMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val chatRepository: ChatRepository,
    private val userManager: UserManager
): ViewModel(){

    val conversationId: String = checkNotNull(savedStateHandle["conversationId"])
    val partnerName: String = checkNotNull(savedStateHandle["partnerName"])
    val partnerAvatar: String = savedStateHandle["partnerAvatar"] ?: ""
    val partnerId: Long = checkNotNull(savedStateHandle["partnerId"])

    // Lấy ID người dùng hiện tại một cách an toàn và phản hồi khi nó thay đổi
    val myUserId: Long
        get() = userManager.currentUser.value?.id ?: 0L

    private val _messages = MutableStateFlow<List<FirestoreMessage>>(emptyList())
    val messages: StateFlow<List<FirestoreMessage>> = _messages.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    init {
        listenForMessages()
    }

    private fun listenForMessages(){
        _isLoading.value = true
        _error.value = null
        viewModelScope.launch {
            chatRepository.getMessagesRealtime(conversationId)
                .catch { e ->
                    Log.e("ChatViewModel", "Error listening for messages: ${e.message}")
                    _error.value = "Lỗi Firestore: ${e.message}\n(Vui lòng kiểm tra cấu hình Firestore Rules: cho phép read/write trên conversations/{id}/messages)"
                    _isLoading.value = false
                }
                .collect { messageList ->
                    _messages.value = messageList
                    _isLoading.value = false
                }
        }
    }

    private val _isSending = MutableStateFlow(false)
    val isSending: StateFlow<Boolean> = _isSending.asStateFlow()

    private val _navigationEvent = MutableSharedFlow<ChatNavEvent>()
    val navigationEvent = _navigationEvent.asSharedFlow()

    sealed class ChatNavEvent {
        data class NavigateToProfile(val userId: Long) : ChatNavEvent()
    }

    fun onPartnerNameClick() {
        // Conversation ID usually is format "userId1_userId2" (sorted)
        // We need to extract the ID that is NOT ours
        val ids = conversationId.split("_")
        val partnerId = ids.find { it != myUserId.toString() }?.toLongOrNull()
        
        partnerId?.let {
            viewModelScope.launch {
                _navigationEvent.emit(ChatNavEvent.NavigateToProfile(it))
            }
        }
    }

    fun sendMessage(text: String, mediaUris: List<android.net.Uri> = emptyList()){
        if (text.isBlank() && mediaUris.isEmpty()) return

        viewModelScope.launch {
            _isSending.value = true
            try {
                var mediaUrls = emptyList<String>()
                var mediaType: String? = null

                if (mediaUris.isNotEmpty()) {
                    // Tạm thời lấy type từ file đầu tiên (hoặc có thể xử lý phức tạp hơn)
                    // Ở đây ta giả định nếu có media thì upload hết
                    mediaUrls = com.localhelp.app.utils.CloudinaryHelper.uploadMultipleMedia(mediaUris)
                    mediaType = "MEDIA" // Hoặc logic để phân biệt IMAGE/VIDEO
                }

                chatRepository.sendMessage(
                    conversationId = conversationId,
                    senderId = myUserId,
                    text = text,
                    mediaUrls = mediaUrls,
                    mediaType = mediaType,
                    onSuccess = {
                        _isSending.value = false
                    },
                    onFailure = { e ->
                        Log.e("ChatViewModel", "Send failed: ${e.message}")
                        _isSending.value = false
                    }
                )
            } catch (e: Exception) {
                Log.e("ChatViewModel", "Upload/Send failed: ${e.message}")
                _isSending.value = false
            }
        }
    }
}