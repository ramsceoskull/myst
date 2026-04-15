package com.tenko.myst.data.serializable

import kotlinx.serialization.Serializable

@Serializable
data class UserDelete(
    val password: String
)