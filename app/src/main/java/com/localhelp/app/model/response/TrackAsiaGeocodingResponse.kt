package com.localhelp.app.model.response

import com.google.gson.annotations.SerializedName

data class TrackAsiaGeocodingResponse(
    @SerializedName("features")
    val features: List<Feature> = emptyList()
)

data class Feature(
    @SerializedName("properties")
    val properties: Properties
)

data class Properties(
    @SerializedName("label")
    val label: String? = null,
    @SerializedName("name")
    val name: String? = null
)
