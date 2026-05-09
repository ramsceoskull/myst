package com.tenko.app.data.serializable

import kotlinx.serialization.Serializable

@Serializable
data class AddressUpdate(
    val name: String? = null,
    val street: String? = null,
    val city: String? = null,
    val state: String? = null,
    val zip_code: String? = null,
    val phone_number: String? = null,
    val neighborhood: String? = null,
    val is_selected: Boolean = false
)
