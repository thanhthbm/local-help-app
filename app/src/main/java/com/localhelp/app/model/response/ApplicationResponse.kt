package com.localhelp.app.model.response

/**
 * Data class biểu diễn một đơn ứng tuyển hoặc nhận việc của helper.
 *
 * Dùng ở màn chi tiết công việc khi creator xem danh sách thợ đã ứng tuyển
 * và chọn một thợ phù hợp để chấp nhận.
 */
data class ApplicationResponse(
    val applicationId: Long,
    val helperId: Long,
    val helperName: String,
    val helperAvatar: String?,
    val helperRating: Double,
    val status: String,
    val appliedAt: String
)
