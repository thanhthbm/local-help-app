package com.localhelp.app.model.response

import com.localhelp.app.model.constant.JobStatus

/**
 * Model dữ liệu công việc trả về từ backend.
 *
 * Dùng cho danh sách việc, chi tiết việc, form cập nhật việc và các màn hình
 * quản lý/hủy công việc.
 */
data class JobResponse(
    val id: Long,
    val title: String? = "",
    val description: String? = "",
    val price: Double? = 0.0,
    val address: String? = "",
    val latitude: Double? = null,
    val longitude: Double? = null,
    val status: JobStatus? = JobStatus.OPEN,
    val bio: String? = "",
    val categoryId: Long? = 0L,
    val categoryName: String? = "",
    val categoryIcon: String? = "",
    val creatorName: String? = "",
    val creatorId: Long,
    val creatorAvatar: String? = "",
    val creatorRating: Double? = 0.0,
    val images: List<String>? = emptyList(),
    val distance: Double? = null,

    val createdAt: String? = "",
    val helperId: Long? = null,
    val helperName: String? = null,
    val helperAvatar: String? = null,
    val helperRating: Double? = null
)
