package com.localhelp.app.utils

import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*

/**
 * Tập hợp các hàm định dạng dữ liệu hiển thị trên UI.
 *
 * Các chức năng đăng/cập nhật công việc dùng object này để format và làm sạch
 * giá tiền trước khi gửi request lên backend.
 */
object FormatterUtils {
    private val decimalFormat = DecimalFormat("#,###")

    /**
     * Định dạng giá tiền dạng số sang chuỗi có phân tách hàng nghìn.
     *
     * @param price Giá tiền dạng Double.
     * @return Chuỗi giá tiền đã format.
     */
    fun formatPrice(price: Double?): String {
        return if (price == null || price == 0.0) "0" else decimalFormat.format(price)
    }

    /**
     * Định dạng giá tiền từ chuỗi người dùng nhập.
     *
     * @param price Chuỗi giá tiền có thể chứa dấu phân tách.
     * @return Chuỗi giá tiền hiển thị theo định dạng Việt Nam.
     */
    fun formatPrice(price: String): String {
        val cleanString = price.replace(".", "").replace(",", "")
        val parsed = cleanString.toDoubleOrNull() ?: 0.0
        return decimalFormat.format(parsed).replace(",", ".")
    }

    /**
     * Loại bỏ dấu phân tách khỏi chuỗi giá tiền trước khi parse số.
     *
     * @param formattedPrice Chuỗi giá tiền đang hiển thị trên UI.
     * @return Chuỗi chỉ còn chữ số để gửi lên API.
     */
    fun cleanPrice(formattedPrice: String): String {
        return formattedPrice.replace(".", "").replace(",", "")
    }

    /**
     * Định dạng thời gian ISO từ backend sang dạng ngày giờ dễ đọc.
     *
     * @param isoString Chuỗi thời gian ISO.
     * @return Chuỗi ngày giờ theo múi giờ GMT+7, hoặc chuỗi gốc nếu parse lỗi.
     */
    fun formatDateTime(isoString: String?): String {
        if (isoString.isNullOrBlank()) return ""
        return try {
            // Backend sends ISO 8601 (e.g., 2023-10-27T10:00:00Z)
            val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
            inputFormat.timeZone = TimeZone.getTimeZone("UTC")
            val date = inputFormat.parse(isoString)
            
            val outputFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
            outputFormat.timeZone = TimeZone.getTimeZone("GMT+7")
            date?.let { outputFormat.format(it) } ?: isoString
        } catch (e: Exception) {
            isoString
        }
    }
}
