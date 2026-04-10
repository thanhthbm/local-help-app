package com.localhelp.app.utils

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.trackasia.navigation.android.navigation.v5.models.ManeuverModifier
import java.lang.reflect.Type

class ManeuverModifierDeserializer : JsonDeserializer<ManeuverModifier.Type?> {
    override fun deserialize(
        json: JsonElement,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): ManeuverModifier.Type? {
        val value = json.asString
        return ManeuverModifier.Type.entries.find { it.text == value }
    }
}