package com.localhelp.app.model.response

/**
 * Response trả về sau khi xác thực OTP khôi phục mật khẩu thành công.
 *
 * @property resetToken Token tạm thời dùng cho bước đặt mật khẩu mới.
 */
data class VerifyOtpResponse(
    val resetToken: String
)
