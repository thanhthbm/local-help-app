package com.localhelp.app.model.response

data class ApiResponse<T>(
    val statusCode: Int,
    val error: String?,
    val message: Any?,
    val data: T?
)
