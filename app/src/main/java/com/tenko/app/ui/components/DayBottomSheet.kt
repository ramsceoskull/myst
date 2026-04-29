package com.tenko.app.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DayBottomSheet(
    date: LocalDate,
    onDismiss: () -> Unit,
    onSave: (String, List<String>) -> Unit
) {
    var note by remember { mutableStateOf("") }

    val symptoms = listOf(
        "Dolor",
        "Sangrado",
        "Fatiga",
        "Dolor cabeza",
        "Nauseas"
    )

    val selected = remember {
        mutableStateListOf<String>()
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss
    ) {
        Column(
            Modifier.padding(16.dp)
        ) {
            Text(
                date.toString(),
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.height(8.dp))

            symptoms.forEach { s ->
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = selected.contains(s),
                        onCheckedChange = {
                            if (selected.contains(s))
                                selected.remove(s)
                            else
                                selected.add(s)
                        }
                    )

                    Text(s)
                }
            }

            Spacer(Modifier.height(8.dp))

            OutlinedTextField(
                value = note,
                onValueChange = { note = it },
                label = { Text("Notas") }
            )

            Spacer(Modifier.height(12.dp))

            Button(
                onClick = {
                    onSave(note, selected)
                }
            ) {
                Text("Guardar")
            }

            Spacer(Modifier.height(20.dp))
        }

    }
}