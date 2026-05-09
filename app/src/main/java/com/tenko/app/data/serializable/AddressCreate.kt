package com.tenko.app.data.serializable

import kotlinx.serialization.Serializable

@Serializable
data class AddressCreate(
    val name: String,
    val street: String,
    val city: String,
    val state: String,
    val zip_code: String? = null,
    val phone_number: String? = null,
    val neighborhood: String? = null,
    val is_selected: Boolean = false
)