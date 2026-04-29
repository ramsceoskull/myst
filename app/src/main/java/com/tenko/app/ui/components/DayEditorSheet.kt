package com.tenko.app.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tenko.app.data.model.CycleEvent
import com.tenko.app.data.model.EventType
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DayEditorSheet(
    date: LocalDate,
    onDismiss: () -> Unit,
    onSave: (CycleEvent) -> Unit
) {
    var selectedType by remember { mutableStateOf(EventType.SYMPTOM) }
    var note by remember { mutableStateOf("") }

    ModalBottomSheet(
        onDismissRequest = onDismiss
    ) {
        Column(
            Modifier.padding(16.dp)
        ) {
            Text(
                date.toString(),
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )

            Spacer(Modifier.height(12.dp))

            Text("Tipo de evento")

            EventType.entries.forEach { type ->
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = selectedType == type,
                        onClick = {
                            selectedType = type
                        }
                    )

                    Text(type.name)
                }
            }

            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = note,
                onValueChange = { note = it },
                label = { Text("Notas") }
            )

            Spacer(Modifier.height(12.dp))

            Button(
                onClick = {
                    onSave(
                        CycleEvent(
                            date = date,
                            type = selectedType,
                            note = note
                        )
                    )
                }
            ) {
                Text("Guardar")
            }

            Spacer(Modifier.height(24.dp))
        }
    }
}