package com.localhelp.app.model.response

data class ConversationResponse(
    val id: String,
    val partner: UserSummary,
    val createdAt: String
)
