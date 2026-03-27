package com.localhelp.app.ui.screens.messages

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.localhelp.app.data.local.UserManager
import com.localhelp.app.data.repository.ChatRepository
import com.localhelp.app.model.response.FirestoreMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
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

    val myUserId = userManager.currentUser.value?.id ?: 0L

    private val _messages = MutableStateFlow<List<FirestoreMessage>>(emptyList())
    val messages: StateFlow<List<FirestoreMessage>> = _messages.asStateFlow()

    init {
        listenForMessages()
    }

    private fun listenForMessages(){
        viewModelScope.launch {
            chatRepository.getMessagesRealtime(conversationId).collect { messageList ->
                _messages.value = messageList
            }
        }
    }

    fun sendMessage(text: String){
        if (text.isBlank()) return

        chatRepository.sendMessage(
            conversationId = conversationId,
            senderId = myUserId,
            text = text,
            onSuccess = {},
            onFailure = {}
        )
    }
}