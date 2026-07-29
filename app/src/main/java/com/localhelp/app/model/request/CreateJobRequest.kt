package com.localhelp.app.model.request

/**
 * Request body dùng chung cho API đăng công việc mới và cập nhật công việc.
 *
 * @property title Tiêu đề công việc người dùng muốn đăng.
 * @property description Mô tả chi tiết yêu cầu công việc.
 * @property price Thù lao dự kiến cho công việc.
 * @property categoryId ID danh mục công việc đã chọn.
 * @property address Địa chỉ hiển thị của công việc.
 * @property latitude Vĩ độ vị trí thực hiện công việc.
 * @property longitude Kinh độ vị trí thực hiện công việc.
 * @property imageUrls Danh sách URL ảnh minh họa đã upload.
 */
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
