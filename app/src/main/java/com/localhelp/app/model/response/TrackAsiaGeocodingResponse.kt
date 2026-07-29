package com.localhelp.app.model.response

import com.google.gson.annotations.SerializedName

/**
 * Data class ánh xạ response reverse geocoding từ TrackAsia.
 *
 * features chứa danh sách kết quả địa chỉ phù hợp với cặp tọa độ gửi lên.
 */
data class TrackAsiaGeocodingResponse(
    @SerializedName("features")
    val features: List<Feature> = emptyList()
)

/**
 * Một kết quả địa chỉ riêng lẻ trong response geocoding.
 */
data class Feature(
    @SerializedName("properties")
    val properties: Properties
)

/**
 * Thông tin địa chỉ dễ đọc trả về từ TrackAsia.
 */
data class Properties(
    @SerializedName("label")
    val label: String? = null,
    @SerializedName("name")
    val name: String? = null
)
