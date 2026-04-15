package com.localhelp.app.model.response

import com.localhelp.app.model.constant.JobStatus

data class JobResponse(
    val id: Long,
    val title: String? = "",
    val description: String? = "",
    val price: Double? = 0.0,
    val address: String? = "",
    val latitude: Double? = null,
    val longitude: Double? = null,
    val displayStatus: String? = "",
    val status: JobStatus ? = JobStatus.OPEN,
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

    val createdAt: String? = ""
)