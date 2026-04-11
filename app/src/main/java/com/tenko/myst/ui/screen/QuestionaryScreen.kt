package com.tenko.myst.ui.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.tenko.myst.data.model.Question
import com.tenko.myst.data.model.QuestionType

@Composable
fun QuestionaryScreen(
    questions: List<Question>,
    onAnswerChanged: (String, Any) -> Unit // (id_pregunta, valor)
) {
    LazyColumn {
        items(questions) { question ->
            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = question.text, style = MaterialTheme.typography.titleMedium)

                when (val type = question.type) {
                    is QuestionType.SingleChoice -> {
                        // Aquí implementas el diseño de "Marque 1 para X, 2 para Y"
                        // o una lista de botones.
                        type.options.forEach { (key, label) ->
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                RadioButton(
                                    selected = false, // Aquí manejarías el estado real
                                    onClick = { onAnswerChanged(question.id, key) }
                                )
                                Text(text = label)
                            }
                        }
                    }
                    is QuestionType.BooleanChoice -> {
                        // Switch o botones de Sí/No
                    }
                    is QuestionType.NumericInput -> {
                        // TextField que solo acepta números
                    }
                }
            }
        }
    }
}