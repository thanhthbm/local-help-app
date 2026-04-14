package com.localhelp.app.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.localhelp.app.model.response.FirestoreMessage
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import com.localhelp.app.data.remote.ConversationService
import com.localhelp.app.model.response.ConversationResponse
import javax.inject.Inject

class ChatRepository @Inject constructor(
    private val conversationService: ConversationService
){
    private val db = FirebaseFirestore.getInstance()

    suspend fun startConversation(targetUserId: String): Result<ConversationResponse> {
        return try {
            val response = conversationService.startConversation(targetUserId)
            if (response.isSuccessful && response.body() != null) {
                val apiResponse = response.body()!!
                if (apiResponse.data != null) {
                    Result.success(apiResponse.data)
                } else {
                    Result.failure(Exception(apiResponse.message?.toString() ?: "Lỗi bắt đầu trò chuyện"))
                }
            } else {
                Result.failure(Exception("Lỗi backend (${response.code()})"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun getMessagesRealtime(conversationId: String): Flow<List<FirestoreMessage>> = callbackFlow {
        val listenerRegistration = try {
            db.collection("conversations")
                .document(conversationId)
                .collection("messages")
                .orderBy("timestamp", Query.Direction.ASCENDING)
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        // Thay vì close(error), ta log và đóng flow một cách êm đẹp
                        // Hoặc ta có thể gửi lỗi qua một kênh khác nếu muốn.
                        // Ở đây ta log để debug và đóng flow để tránh crash.
                        android.util.Log.e("ChatRepository", "Firestore error: ${error.message}")
                        close(error) 
                        return@addSnapshotListener
                    }

                    if (snapshot != null) {
                        val messages = snapshot.documents.mapNotNull { doc ->
                            try {
                                doc.toObject(FirestoreMessage::class.java)?.copy(id = doc.id)
                            } catch (e: Exception) {
                                null
                            }
                        }
                        trySend(messages)
                    }
                }
        } catch (e: Exception) {
            android.util.Log.e("ChatRepository", "Failed to add listener: ${e.message}")
            close(e)
            null
        }

        awaitClose { listenerRegistration?.remove() }
    }

    fun sendMessage(
        conversationId: String,
        senderId: Long,
        text: String,
        mediaUrls: List<String> = emptyList(),
        mediaType: String? = null,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val messageMap = mutableMapOf<String, Any>(
            "senderId" to senderId,
            "text" to text,
            "timestamp" to System.currentTimeMillis()
        )
        if (mediaUrls.isNotEmpty()) {
            messageMap["mediaUrls"] = mediaUrls
        }
        if (mediaType != null) {
            messageMap["mediaType"] = mediaType
        }

        db.collection("conversations")
            .document(conversationId)
            .collection("messages")
            .add(messageMap)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { e -> onFailure(e) }
    }
}