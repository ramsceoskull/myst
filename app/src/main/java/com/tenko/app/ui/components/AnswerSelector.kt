package com.tenko.app.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tenko.app.data.model.AnswerType
import com.tenko.app.data.model.ClinicalQuestion
import com.tenko.app.ui.theme.AntiFlashWhite
import com.tenko.app.ui.theme.PompAndPower
import com.tenko.app.ui.theme.StarsLove
import com.tenko.app.ui.theme.SweetGrey
import com.tenko.app.ui.theme.Tekhelet
import com.tenko.app.ui.theme.White
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun AnswerSelector(question: ClinicalQuestion, onAnswer: (String) -> Unit) {
    when (val type = question.type) {
        is AnswerType.Binary -> {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                for (answer in listOf("Sí", "No")) {
                    TextButton(
                        onClick = { onAnswer(answer) },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.textButtonColors(
                            containerColor = PompAndPower,
                            contentColor = White
                        ),
                        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp),
                        content = {
                            Text(
                                text = answer,
                                fontFamily = StarsLove,
                                fontSize = 20.sp,
                                modifier = Modifier.offset(y = 4.dp)
                            )
                        }
                    )
                }
            }
        }

        is AnswerType.SingleChoice -> {
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                type.options.forEach { (key, value) ->
                    OutlinedButton(onClick = { onAnswer(value) }) { Text(value) }
                }
            }
        }

        is AnswerType.Text -> {
            ChatInput(onSend = onAnswer, modifier = Modifier.imePadding(), enableWhiteSpace = false)
        }

        is AnswerType.Numeric -> {
            ChatInput(onSend = onAnswer, modifier = Modifier.imePadding(), isNumeric = true)
        }

        is AnswerType.DatePicker -> {
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
            val dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
            var birthDate by remember { mutableStateOf<LocalDate?>(null) }
            var showDialog by remember { mutableStateOf(false) }

            val state = rememberDatePickerState(selectableDates = object : SelectableDates {
                override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                    return utcTimeMillis < System.currentTimeMillis()
                }

                override fun isSelectableYear(year: Int): Boolean {
                    return year <= LocalDate.now().year
                }
            })

            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                DatePickerField(
                    label = "Selecciona la fecha",
                    value = birthDate?.format(dateFormatter) ?: "",
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedTrailingIconColor = SweetGrey,
                        unfocusedPlaceholderColor = SweetGrey,
                        disabledContainerColor = AntiFlashWhite,
                        disabledBorderColor = Color.Transparent
                    ),
                    onClick = {
                        showDialog = true /*showDatePicker(context) { date -> birthDate = date }*/
                    }
                )

                birthDate?.let {
                    Button(
                        onClick = { onAnswer(birthDate!!.format(formatter)) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = PompAndPower,
                            contentColor = White
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(55.dp),
                        content = {
                            Text(
                                text = "Confirmar",
                                fontFamily = StarsLove,
                                fontSize = 20.sp,
                                modifier = Modifier.offset(y = 4.dp)
                            )
                        }
                    )
                }

                if (showDialog) {
                    DatePickerDialog(
                        onDismissRequest = { showDialog = false },
                        dismissButton = {
                            Button(
                                onClick = { showDialog = false },
                                content = { Text("Cancelar") },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color.Transparent,
                                    contentColor = Color.Gray
                                )
                            )
                        },
                        confirmButton = {
                            Button(
                                onClick = {
                                    showDialog = false
                                    birthDate = state.selectedDateMillis?.let { millis ->
                                        LocalDate.ofEpochDay(millis / (24 * 60 * 60 * 1000))
                                    }
                                },
                                content = { Text("Aceptar") },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = PompAndPower,
                                    contentColor = White
                                ),
                                shape = RoundedCornerShape(12.dp),
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = DatePickerDefaults.colors(containerColor = White),
                        content = {
                            DatePicker(
                                state = state,
                                showModeToggle = false,
                                colors = DatePickerDefaults.colors(
                                    containerColor = White,
                                    titleContentColor = SweetGrey,
                                    headlineContentColor = PompAndPower,
                                    weekdayContentColor = Color.DarkGray,
                                    navigationContentColor = Color.DarkGray,
                                    yearContentColor = Color.DarkGray,
                                    currentYearContentColor = Tekhelet,
                                    selectedYearContentColor = White,
                                    disabledSelectedYearContentColor = Color.LightGray,
                                    selectedYearContainerColor = PompAndPower,
                                    dayContentColor = Color.DarkGray,
                                    disabledDayContentColor = Color.LightGray,
                                    selectedDayContentColor = White,
                                    selectedDayContainerColor = PompAndPower,
                                    todayContentColor = Tekhelet,
                                    todayDateBorderColor = Tekhelet,
                                    dividerColor = SweetGrey,
                                )
                            )
                        }
                    )
                }
            }
        }

        is AnswerType.MultiChoice -> {
            // Estado para recordar qué llaves (keys) están seleccionadas
            val selectedOptions = remember { mutableStateListOf<String>() }
            val isNoneSelected = selectedOptions.contains("none")

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    type.options.forEach { (key, value) ->
                        val isSelected = selectedOptions.contains(key)

                        OutlinedButton(
                            onClick = {
                                if (key == "none") {
                                    if (isSelected) selectedOptions.remove(key)
                                    else {
                                        selectedOptions.clear()
                                        selectedOptions.add(key)
                                    }
                                } else {
                                    if (isSelected) selectedOptions.remove(key)
                                    else selectedOptions.add(key)
                                }
                            },
                            enabled = !isNoneSelected || key == "none",
                            modifier = Modifier.padding(vertical = 4.dp),
                            shape = RoundedCornerShape(12.dp),
                            // Cambiamos el color según la selección
                            colors = ButtonDefaults.outlinedButtonColors(
                                containerColor = if (isSelected) PompAndPower else Color.Transparent,
                                contentColor = if (isSelected) White else PompAndPower,
                                disabledContainerColor = AntiFlashWhite,
                                disabledContentColor = Color.Gray
                            ),
                            border = BorderStroke(
                                1.dp,
                                if (!isNoneSelected || key == "none") PompAndPower else Color.Transparent
                            ),
                            content = { Text(value) }
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                }

                // Botón para confirmar la selección múltiple
                if (selectedOptions.isNotEmpty()) {
                    Button(
                        onClick = {
                            // Enviamos las opciones como un string separado por comas o JSON
                            onAnswer(selectedOptions.joinToString(", "))
                            selectedOptions.clear()
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = PompAndPower,
                            contentColor = White
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(55.dp),
                        content = {
                            Text(
                                text = "Confirmar selección",
                                fontFamily = StarsLove,
                                fontSize = 20.sp,
                                modifier = Modifier.offset(y = 4.dp)
                            )
                        }
                    )
                }
            }
        }
    }
}
