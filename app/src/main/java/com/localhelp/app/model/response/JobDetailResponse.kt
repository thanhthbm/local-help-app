package com.localhelp.app.model.response

/**
 * Response tổng hợp cho màn hình quản lý chi tiết công việc của chủ việc.
 *
 * @property jobInfo Thông tin chính của công việc.
 * @property description Mô tả bổ sung nếu backend trả riêng với jobInfo.
 * @property progresses Danh sách tiến trình xử lý công việc.
 * @property acceptedHelper Thông tin người nhận việc đã được chọn, nếu có.
 */
data class JobDetailResponse(
    val jobInfo: JobResponse,
    val description: String?,
    val progresses: List<ProgressResponse>,
    val acceptedHelper: ApplicationResponse? = null
)
