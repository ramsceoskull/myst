package com.tenko.app.data.serializable

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class Trend {
    @SerialName("UP")
    UP,
    @SerialName("DOWN")
    DOWN,
    @SerialName("STABLE")
    STABLE,
    @SerialName("NONE")
    NONE
}