package com.localhelp.app.model.response

data class FirestoreMessage(
    val id: String = "",
    val senderId: Long = 0L,
    val text: String = "",
    val mediaUrls: List<String> = emptyList(),
    val mediaType: String? = null, // "IMAGE", "VIDEO", etc.
    val timestamp: Long = 0L
)
