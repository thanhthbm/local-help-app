package com.localhelp.app.model.request

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
