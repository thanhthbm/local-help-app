package com.localhelp.app.model.request

// Payload dùng chung cho API đăng công việc mới và cập nhật công việc.
data class CreateJobRequest(
    val title: String,
    val description: String? = null,
    val price: Double,
    val categoryId: Long,
    val address: String,
    val latitude: Double,
    val longitude: Double,
    val imageUrls: List<String>
)
