package com.localhelp.app.model.response

data class ResultPaginationDTO(
    val meta: Meta,
    val result: Object
)

data class Meta (
    val page: Int,
    val size: Int,
    val pages: Int,
    val total: Long
)