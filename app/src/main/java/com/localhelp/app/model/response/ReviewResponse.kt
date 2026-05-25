package com.localhelp.app.model.response
/**
 * Data class ánh xạ JSON response một đánh giá từ backend.
 *
 * reviewerAvatar: String? (nullable) – null nếu reviewer chưa có avatar
 *   hoặc tài khoản reviewer đã bị xóa (backend trả null, không crash).
 *
 */
data class ReviewResponse(
    val id: Long,
    val rating: Int,
    val comment: String,
    val reviewerName: String,
    val reviewerAvatar: String?,
    val createdAt: String
)