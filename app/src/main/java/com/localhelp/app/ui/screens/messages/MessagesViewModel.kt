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