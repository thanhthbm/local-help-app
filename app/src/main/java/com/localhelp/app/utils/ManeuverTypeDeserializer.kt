package com.localhelp.app.utils

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.trackasia.navigation.android.navigation.v5.models.StepManeuver
import java.lang.reflect.Type

class ManeuverTypeDeserializer : JsonDeserializer<StepManeuver.Type?> {
    override fun deserialize(
        json: JsonElement,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): StepManeuver.Type? {
        val value = json.asString
        return StepManeuver.Type.entries.find{ it.text == value }
    }

}