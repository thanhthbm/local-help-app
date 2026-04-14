package com.localhelp.app.model.request

import com.google.gson.annotations.SerializedName

data class SearchJobRequest(
    @SerializedName("page")
    val page : Int,

    @SerializedName("size")
    val size : Int,

    @SerializedName("maxDistance")
    // km
    val maxDistance: Double = 1.0,

    @SerializedName("minSalary")
    val minSalary: Double? = 0.0,

    @SerializedName("categoryIds")
    val categoryIds: List<Long>? = null,

    @SerializedName("startTime")
    val startTime: String? = null,

    @SerializedName("endTime")
    val endTime: String? = null,

    @SerializedName("longitude")
    val longitude: Double,

    @SerializedName("latitude")
    val latitude: Double,

    @SerializedName("keyword")
    val keyword: String? = null
)