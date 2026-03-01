package com.localhelp.app.model

data class Category(
    val id: Long? = null,
    val name: String,
    val iconUrl: String,
    val description: String? = null
)
