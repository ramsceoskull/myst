package com.tenko.app.ui.screen

import android.app.DatePickerDialog
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.tenko.app.data.model.LaboratoryVariable
import com.tenko.app.data.serializable.LabResultBase
import com.tenko.app.data.serializable.LabStudyCreate
import com.tenko.app.data.view.LabViewModel
import com.tenko.app.ui.components.LaboratoryStudyDropdown
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddLaboratoryStudyScreen(
    navController: NavController,
    viewModel: LabViewModel = viewModel()
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    var studyName by remember { mutableStateOf("") }
    var studyDate by remember { mutableStateOf("") }

    val variables = remember { mutableStateListOf(LaboratoryVariable()) }

    val units = listOf(
        "mg/dL",
        "g/dL",
        "mmol/L",
        "UI/L",
        "mEq/L",
        "%",
        "ng/mL",
        "pg/mL",
        "cells/µL"
    )

    val calendar = Calendar.getInstance()

    val datePickerDialog = DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->

            val selectedCalendar = Calendar.getInstance().apply {
                set(year, month, dayOfMonth)
            }

            val formatter = SimpleDateFormat(
                "dd/MM/yyyy",
                Locale.getDefault()
            )

            studyDate = formatter.format(
                selectedCalendar.time
            )
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )

    Scaffold(
        snackbarHost = {
            SnackbarHost(snackbarHostState)
        },
        topBar = {
            TopAppBar(
                title = {
                    Text("Resultados de laboratorio")
                }
            )
        },
        bottomBar = {

            Button(
                onClick = {
                    // Mapeo exhaustivo y validación de datos hacia el Serializable final
                    val validResults = variables.mapNotNull { variable ->
                        val parsedValue = variable.value.toDoubleOrNull()

                        if (variable.parameter.isNotBlank() && parsedValue != null) {
                            LabResultBase(
                                parameter = variable.parameter.trim(),
                                value = parsedValue,
                                unit = variable.unit.ifBlank { null },
                                reference_range = null
                            )
                        } else null
                    }

                    if (studyName.isBlank()) {
                        Toast.makeText(
                            context,
                            "Por favor ingresa el nombre del estudio",
                            Toast.LENGTH_SHORT
                        ).show()
                        return@Button
                    }

                    if (studyDate.isBlank()) {
                        Toast.makeText(
                            context,
                            "Selecciona la fecha del estudio",
                            Toast.LENGTH_SHORT
                        ).show()

                        return@Button
                    }

                    if (validResults.isEmpty()) {
                        Toast.makeText(
                            context,
                            "Agrega al menos una variable válida con valor numérico",
                            Toast.LENGTH_SHORT
                        ).show()
                        return@Button
                    }

                    val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")

                    val parsedDate = LocalDate.parse(
                        studyDate,
                        formatter
                    )

                    // Construcción del Payload esperado por tu ApiClient
                    val payload = LabStudyCreate(
                        laboratory_name = studyName,
                        test_date = parsedDate,
                        results = validResults
                    )

                    // Despacho al ViewModel
                    viewModel.createLabStudy(payload, context) {
                        navController.popBackStack() // Regresa al éxito
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .navigationBarsPadding()
            ) {

                Text("Guardar estudio clínico")
            }
        }
    ) { padding ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            item {

                Spacer(modifier = Modifier.height(16.dp))

                LaboratoryStudyDropdown(
                    scope = scope,
                    selected = "",
                    onSelected = {
                        studyName = it
                    },
                    modifier = Modifier
                )

                OutlinedTextField(
                    value = studyName,
                    onValueChange = {
                        studyName = it
                    },
                    modifier = Modifier.fillMaxWidth(),
                    label = {
                        Text("Nombre del estudio")
                    },
                    singleLine = true
                )
            }

            item {

                OutlinedTextField(
                    value = studyDate,
                    onValueChange = {},
                    modifier = Modifier.fillMaxWidth(),
                    readOnly = true,
                    label = {
                        Text("Fecha del estudio")
                    },
                    trailingIcon = {

                        IconButton(
                            onClick = {
                                datePickerDialog.show()
                            }
                        ) {

                            Icon(
                                imageVector = Icons.Default.CalendarMonth,
                                contentDescription = null
                            )
                        }
                    }
                )
            }

            itemsIndexed(variables) { index, variable ->

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(
                        defaultElevation = 4.dp
                    )
                ) {

                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {

                            Text(
                                text = if (
                                    variable.parameter.isBlank()
                                ) {
                                    "Variable ${index + 1}"
                                } else {
                                    variable.parameter
                                },
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier.weight(1f)
                            )

                            IconButton(
                                onClick = {

                                    variables[index] = variable.copy(
                                        expanded = !variable.expanded
                                    )
                                }
                            ) {

                                Icon(
                                    imageVector = if (
                                        variable.expanded
                                    ) {
                                        Icons.Default.ExpandLess
                                    } else {
                                        Icons.Default.ExpandMore
                                    },
                                    contentDescription = null
                                )
                            }
                        }

                        AnimatedVisibility(
                            visible = variable.expanded
                        ) {

                            Column {

                                Spacer(
                                    modifier = Modifier.height(12.dp)
                                )

                                OutlinedTextField(
                                    value = variable.parameter,
                                    onValueChange = {

                                        variables[index] = variable.copy(
                                            parameter = it
                                        )
                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                    label = {
                                        Text("Nombre de la variable")
                                    },
                                    singleLine = true
                                )

                                AnimatedVisibility(
                                    visible = variable.parameter.isNotBlank()
                                ) {

                                    Column {

                                        Spacer(
                                            modifier = Modifier.height(12.dp)
                                        )

                                        OutlinedTextField(
                                            value = variable.value,
                                            onValueChange = {

                                                variables[index] = variable.copy(
                                                    value = it.filter { char ->
                                                        char.isDigit() || char == '.'
                                                    }
                                                )
                                            },
                                            modifier = Modifier.fillMaxWidth(),
                                            label = {
                                                Text("Resultado")
                                            },
                                            keyboardOptions = KeyboardOptions(
                                                keyboardType = KeyboardType.Decimal
                                            ),
                                            singleLine = true
                                        )

                                        Spacer(
                                            modifier = Modifier.height(12.dp)
                                        )

                                        var expanded by remember {
                                            mutableStateOf(false)
                                        }

                                        ExposedDropdownMenuBox(
                                            expanded = expanded,
                                            onExpandedChange = {
                                                expanded = !expanded
                                            }
                                        ) {

                                            OutlinedTextField(
                                                value = variable.unit,
                                                onValueChange = {},
                                                modifier = Modifier
                                                    .menuAnchor()
                                                    .fillMaxWidth(),
                                                readOnly = true,
                                                label = {
                                                    Text("Unidad de medida")
                                                },
                                                trailingIcon = {

                                                    ExposedDropdownMenuDefaults.TrailingIcon(
                                                        expanded = expanded
                                                    )
                                                }
                                            )

                                            ExposedDropdownMenu(
                                                expanded = expanded,
                                                onDismissRequest = {
                                                    expanded = false
                                                }
                                            ) {

                                                units.forEach { unit ->

                                                    DropdownMenuItem(
                                                        text = {
                                                            Text(unit)
                                                        },
                                                        onClick = {

                                                            variables[index] = variable.copy(
                                                                unit = unit
                                                            )

                                                            expanded = false
                                                        }
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            item {

                Button(
                    onClick = {

                        variables.forEachIndexed { i, item ->

                            variables[i] = item.copy(
                                expanded = false
                            )
                        }

                        variables.add(
                            LaboratoryVariable(
                                expanded = true
                            )
                        )
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {

                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null
                    )

                    Spacer(
                        modifier = Modifier.width(8.dp)
                    )

                    Text("Agregar nueva variable")
                }

                Spacer(
                    modifier = Modifier.height(24.dp)
                )
            }
        }
    }
}