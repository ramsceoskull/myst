package com.tenko.app.data.serializable

import kotlinx.serialization.Serializable

@Serializable
enum class Trend {
    UP,
    DOWN,
    STABLE,
    NONE
}