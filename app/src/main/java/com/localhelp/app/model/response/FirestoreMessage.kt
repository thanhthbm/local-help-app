package com.localhelp.app.model.response

data class FirestoreMessage(
    val id: String = "",
    val senderId: Long = 0L,
    val text: String = "",
    val timestamp: Long = 0L
)
