package com.localhelp.app.model.response

/**
 * Data class biểu diễn một ảnh gắn với công việc.
 *
 * Trong luồng hoàn thành công việc, object này chủ yếu dùng cho ảnh bằng chứng
 * mà helper đã gửi sau khi làm xong việc.
 */
data class JobImageResponse(
    val id: Long,
    val imageUrl: String,
    val imageType: String
)
