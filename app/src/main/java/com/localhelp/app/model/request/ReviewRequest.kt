package com.localhelp.app.model.request
/**
 * Data class gửi lên khi tạo đánh giá mới (POST /api/jobs/{jobId}/review).
 *
 * rating: Int trong khoảng 1–5 (backend validate @Min(1) @Max(5)).
 * comment: String không được rỗng (backend validate @NotBlank).
 *
 */
data class ReviewRequest(
    val rating: Int,
    val comment: String
)