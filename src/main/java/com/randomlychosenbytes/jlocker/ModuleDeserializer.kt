package com.randomlychosenbytes.jlocker

import com.google.gson.*
import com.randomlychosenbytes.jlocker.model.Building
import java.lang.reflect.Type

class ModuleDeserializer<T> : JsonDeserializer<T> {

    private val gson = GsonBuilder().excludeFieldsWithoutExposeAnnotation().create()

    override fun deserialize(
        json: JsonElement,
        typeOfT: Type,
        context: JsonDeserializationContext
    ): T {
        val jsonObject = json.asJsonObject
        val className = (jsonObject["type"] as JsonPrimitive).asString

        val entityPackagePath = Building::class.qualifiedName!!.substringBeforeLast(".")
        val entityName = className.substringAfterLast(".")

        val clazz: Class<*> = Class.forName("$entityPackagePath.$entityName")

        return gson.fromJson(jsonObject, clazz) as T
    }
}