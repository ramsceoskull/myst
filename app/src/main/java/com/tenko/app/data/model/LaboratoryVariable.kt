package com.tenko.app.data.model

data class LaboratoryVariable(
    var parameter: String = "",
    var value: String = "",
    var unit: String = "",
    var expanded: Boolean = false,
    var hasError: Boolean = false,

    var parameterError: String? = null,
    var valueError: String? = null,
    var unitError: String? = null
)
