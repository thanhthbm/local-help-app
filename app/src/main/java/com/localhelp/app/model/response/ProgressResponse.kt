package com.localhelp.app.model.response

data class ProgressResponse(
    val stepName: String,
    val description: String?,
    val time: String?,
    val isCompleted: Boolean,
    val isCurrent: Boolean
)