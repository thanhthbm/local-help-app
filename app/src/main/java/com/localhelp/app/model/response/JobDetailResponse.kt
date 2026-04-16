package com.localhelp.app.model.response

data class JobDetailResponse(
    val jobInfo: JobResponse,
    val description: String?,
    val progresses: List<ProgressResponse>,
    val acceptedHelper: ApplicationResponse? = null
)