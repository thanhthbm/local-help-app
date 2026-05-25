package com.localhelp.app.model.response
/**
 * Data class ánh xạ JSON response từ GET /api/conversations.
 *
 * id (String/UUID): dùng làm documentPath trên Firebase Firestore:
 *   conversations/{id}/messages – Android SDK lắng nghe realtime tại đây.
 *
 * partner (UserSummary): thông tin đối phương (không phải raw user1/user2),
 *   được backend resolve sẵn để Android hiển thị trực tiếp.
 *
 */
data class ConversationResponse(
    val id: String,
    val partner: UserSummary,
    val createdAt: String
)
