package com.tenko.app.data.serializable

import kotlinx.serialization.Serializable

@Serializable
data class Token(
    val access_token: String,
    val token_type: String
)
