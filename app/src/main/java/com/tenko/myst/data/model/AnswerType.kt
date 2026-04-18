package com.tenko.myst.data.model

sealed class AnswerType {
    data class SingleChoice(val options: Map<String, String>) : AnswerType()

    data class MultiChoice(val options: Map<String, String>) : AnswerType()

    object Binary : AnswerType() // Sí / No

    object DatePicker : AnswerType()

    object Numeric : AnswerType()

    object Text : AnswerType()
}