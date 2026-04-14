package com.localhelp.app.utils

import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*

object FormatterUtils {
    private val decimalFormat = DecimalFormat("#,###")

    fun formatPrice(price: Double?): String {
        return if (price == null || price == 0.0) "0" else decimalFormat.format(price)
    }

    fun formatPrice(price: String): String {
        val cleanString = price.replace(".", "").replace(",", "")
        val parsed = cleanString.toDoubleOrNull() ?: 0.0
        return decimalFormat.format(parsed).replace(",", ".")
    }

    fun cleanPrice(formattedPrice: String): String {
        return formattedPrice.replace(".", "").replace(",", "")
    }

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
