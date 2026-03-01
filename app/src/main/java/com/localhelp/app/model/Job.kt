package com.localhelp.app.model

data class Job(
    val id: Long? = null,
    val title: String,
    val description: String,
    val price: Double,
    val address: String,
    val latitude: Double,
    val longitude: Double,
    val categoryId: Long,
    val imageUrls: List<String> = emptyList()
)