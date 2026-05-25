package com.localhelp.app.ui.screens.messages

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.localhelp.app.data.repository.ConversationRepository
import com.localhelp.app.model.response.ConversationResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
/**
 * ViewModel quản lý UI state cho màn hình danh sách hội thoại (Messages).
 *
 * Được inject bởi Hilt (@HiltViewModel + @Inject constructor).
 * Khởi tạo tự động gọi fetchConversations() trong init{} để load dữ liệu ngay.
 *
 * State được expose qua StateFlow (immutable) thay vì MutableStateFlow
 * để View không thể modify trực tiếp (đúng nguyên tắc unidirectional data flow).
 *
 */

@HiltViewModel
class MessagesViewModel @Inject constructor(
    private val conversationRepository: ConversationRepository
): ViewModel() {

    private val _conversations = MutableStateFlow<List<ConversationResponse>>(emptyList())
    val conversations: StateFlow<List<ConversationResponse>> = _conversations.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        fetchConversations()
    }
    /**
     * Tải danh sách hội thoại của user hiện tại từ backend REST API.
     *
     * Gọi conversationRepository.getMyConversations() trong viewModelScope.launch {}
     * để không block UI thread (coroutine trên Dispatchers.IO mặc định của Retrofit).
     *
     * Cập nhật _conversations và _isLoading StateFlow để View re-compose.
     * onSuccess: cập nhật danh sách hội thoại.
     * onFailure: log error (hiện tại chưa expose error state, cải thiện sau).
     */
    fun fetchConversations(){
        _isLoading.value = true
        viewModelScope.launch {
            val result = conversationRepository.getMyConversations()
            result.onSuccess{list ->
                _conversations.value = list
            }
            _isLoading.value = false
        }
    }
}