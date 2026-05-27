package com.localhelp.app.model.constant

/**
 * Các trạng thái vòng đời của một công việc trong hệ thống.
 *
 * Được dùng khi hiển thị danh sách việc, xem chi tiết, hủy việc và xác định
 * hành động tiếp theo mà chủ việc hoặc người nhận việc được phép thực hiện.
 */
enum class JobStatus {
    OPEN,
    ACCEPTED,
    ON_THE_WAY,
    WORKING,
    PENDING_PAYMENT,
    COMPLETED,
    CANCELLED,
    APPLIED,
    REJECTED
}
