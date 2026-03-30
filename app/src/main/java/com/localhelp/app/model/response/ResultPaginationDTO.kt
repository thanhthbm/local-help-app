package com.localhelp.app.model.response

data class ResultPaginationDTO<T>(
    val meta: Meta,
    val result: T
)

data class Meta (
    val page: Int,
    val size: Int,
    val pages: Int,
    val total: Long
)