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
    val status: JobStatus ? = JobStatus.OPEN,
    val bio: String? = "",
    val categoryName: String? = "",
    val categoryIcon: String? = "",
    val creatorName: String? = "",
    val creatorAvatar: String? = "",
    val creatorRating: Double? = 0.0,
    val images: List<String>? = emptyList(),

    val createdAt: String? = ""
)