package com.tenko.app.data.serializable

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

object LocalTimeSerializer : KSerializer<java.time.LocalTime> {
    private val formatter = java.time.format.DateTimeFormatter.ISO_LOCAL_TIME

    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("LocalTime", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: java.time.LocalTime) {
        encoder.encodeString(value.format(formatter))
    }

    override fun deserialize(decoder: Decoder): java.time.LocalTime {
        return java.time.LocalTime.parse(decoder.decodeString(), formatter)
    }
}