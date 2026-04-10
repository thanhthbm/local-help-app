package com.localhelp.app.utils

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.trackasia.geojson.Point
import java.lang.reflect.Type

class PointDeserializer : JsonDeserializer<Point> {
    override fun deserialize(
        json: JsonElement,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): Point {
        return if (json.isJsonArray) {
            val array = json.asJsonArray

            Point.fromLngLat(
                array[0].asDouble,
                array[1].asDouble
            )
        } else {
            val obj = json.asJsonObject
            Point.fromLngLat(
                obj.get("longitude").asDouble,
                obj.get("latitude").asDouble
            )
        }
    }
}