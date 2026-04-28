package com.tenko.app.data.serializable

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.JsonEncoder
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.jsonPrimitive
import kotlin.collections.map

object StringOrListSerializer: KSerializer<List<String>> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("StringOrList", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): List<String> {
        val input = decoder as? JsonDecoder ?: throw Exception("Solo funciona con JSON")
        val element = input.decodeJsonElement()

        return when (element) {
            is JsonArray -> element.map {it.jsonPrimitive.content}
            is JsonPrimitive -> listOf(element.content)
            else -> emptyList()
        }
    }

    override fun serialize(encoder: Encoder, value: List<String>) {
        val output = encoder as? JsonEncoder ?: throw Exception("Solo funciona con JSON")
        output.encodeJsonElement(JsonArray(value.map {JsonPrimitive(it)}))
    }
}