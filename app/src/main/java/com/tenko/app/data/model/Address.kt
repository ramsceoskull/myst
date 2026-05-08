package com.tenko.app.data.model

data class Address(
    val id: Int,
    val name: String,
    val street: String,
    val city: String,
    val state: String,
    val zipCode: String,
    val phoneNumber: String,
    val neighborhood: String,
    val isSelected: Boolean = false
)