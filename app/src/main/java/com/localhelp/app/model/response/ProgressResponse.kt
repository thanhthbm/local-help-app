package com.localhelp.app.model.response

/**
 * Data class biểu diễn một bước trong timeline tiến trình công việc.
 *
 * stepName là tên trạng thái nghiệp vụ, description là mô tả hiển thị,
 * time là thời điểm xảy ra, còn isCompleted/isCurrent giúp app tô đúng trạng thái trên UI.
 */
data class ProgressResponse(
    val stepName: String,
    val description: String?,
    val time: String?,
    val isCompleted: Boolean,
    val isCurrent: Boolean
)
