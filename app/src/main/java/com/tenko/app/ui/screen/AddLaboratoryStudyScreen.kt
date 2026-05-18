package com.tenko.app.ui.screen

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.tenko.app.R
import com.tenko.app.data.model.LaboratoryStudy
import com.tenko.app.data.model.LaboratoryVariable
import com.tenko.app.data.serializable.LabResultBase
import com.tenko.app.data.serializable.LabStudyCreate
import com.tenko.app.data.view.LabViewModel
import com.tenko.app.ui.components.AppTopBar
import com.tenko.app.ui.components.DatePickerField
import com.tenko.app.ui.components.FloatingActionButton
import com.tenko.app.ui.components.LaboratoryStudyDropdown
import com.tenko.app.ui.theme.AntiFlashWhite
import com.tenko.app.ui.theme.PompAndPower
import com.tenko.app.ui.theme.RaisinBlack
import com.tenko.app.ui.theme.SweetGrey
import com.tenko.app.ui.theme.Tekhelet
import com.tenko.app.ui.theme.White
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddLaboratoryStudyScreen(
    navController: NavController,
    viewModel: LabViewModel = viewModel()
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    val nameFocus = remember { FocusRequester() }
    val dateFocus = remember { FocusRequester() }
    val variableFocus = remember { FocusRequester() }
    val valueFocus = remember { FocusRequester() }
    val unitFocus = remember { FocusRequester() }

    var studyDate by remember { mutableStateOf<LocalDate?>(null) }
    var showDateDialog by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState(
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                val selectedDate = Instant
                    .ofEpochMilli(utcTimeMillis)
                    .atZone(ZoneOffset.UTC)
                    .toLocalDate()
                val today = LocalDate.now(ZoneOffset.UTC)

                return selectedDate.isBefore(today) || selectedDate.isEqual(today)
            }

            override fun isSelectableYear(year: Int): Boolean {
                return year >= LocalDate.now().year
            }
        }
    )
    val dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")

    var nameError by remember { mutableStateOf<String?>(null) }
    var dateError by remember { mutableStateOf("") }

    var study by remember { mutableStateOf<LaboratoryStudy?>(null) }

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

    Scaffold(
        topBar = {
            AppTopBar(
                title = "Resultados",
                onBackClick = {
                    navController.popBackStack()
                }
            )
        },
        snackbarHost = {
            SnackbarHost(snackbarHostState)
        },
        floatingActionButton = {
            FloatingActionButton(R.drawable.floppy_disk_solid_full) {
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

                if (study == null || studyDate == null || validResults.isEmpty()) {
                    nameError =
                        if (study == null) "Por favor ingresa el nombre del estudio" else null
                    dateError =
                        if (studyDate == null) "Por favor selecciona la fecha del estudio" else ""
                    if (validResults.isEmpty()) {
                        Toast.makeText(
                            context,
                            "Agrega al menos una variable válida con valor numérico",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    return@FloatingActionButton
                }

                // Construcción del Payload esperado por tu ApiClient
                val payload = LabStudyCreate(
                    laboratory_name = study?.displayName,
                    test_date = studyDate!!,
                    results = validResults
                )

                // Despacho al ViewModel
                viewModel.createLabStudy(payload, context) {
                    navController.popBackStack() // Regresa al éxito
                }
            }
        },
        containerColor = White
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item { HeaderContent() }

            item {
                Text(
                    text = "Nombre del estudio realizado",
                    color = Color.Gray,
                    fontSize = 14.sp,
                )
                LaboratoryStudyDropdown(
                    scope = scope,
                    selected = study?.displayName ?: "",
                    onSelected = { name ->
                        study = name
                        nameError = null
                    },
                    modifier = Modifier.focusRequester(nameFocus)
                )
                if (nameError != null) {
                    AnimatedVisibility(
                        visible = nameError?.isNotEmpty() ?: false,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = nameError.toString(),
                            modifier = Modifier.padding(start = 6.dp),
                            color = MaterialTheme.colorScheme.error,
                            fontSize = 12.sp,
                        )
                    }
                }
            }

            item {
                Text(
                    text = "¿Cuándo se realizó el estudio?",
                    color = Color.Gray,
                    fontSize = 14.sp,
                )
                DatePickerField(
                    label = "Selecciona la fecha",
                    value = studyDate?.format(dateFormatter) ?: "",
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedTrailingIconColor = SweetGrey,
                        unfocusedPlaceholderColor = SweetGrey,
                        disabledContainerColor = AntiFlashWhite,
                        disabledBorderColor = Color.Transparent
                    ),
                    onClick = {
                        dateError = ""
                        showDateDialog = true
                    },
                )
                AnimatedVisibility(
                    visible = dateError.isNotEmpty(),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = dateError,
                        modifier = Modifier.padding(start = 6.dp),
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 12.sp,
                    )
                }
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

        if (showDateDialog) {
            DatePickerDialog(
                onDismissRequest = { showDateDialog = false },
                dismissButton = {
                    Button(
                        onClick = { showDateDialog = false },
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
                            showDateDialog = false
                            studyDate = datePickerState.selectedDateMillis?.let { millis ->
                                Instant
                                    .ofEpochMilli(millis)
                                    .atZone(ZoneOffset.UTC)
                                    .toLocalDate()
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
                        state = datePickerState,
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

@Composable
fun HeaderContent() {
    Spacer(modifier = Modifier.height(30.dp))

    Text(
        text = "Datos del estudio",
        color = RaisinBlack,
        fontSize = 20.sp,
        fontWeight = FontWeight.SemiBold
    )

    Spacer(modifier = Modifier.height(8.dp))

    Text(
        text = "Agrega los resultados de tu estudio de laboratorio. Puedes incluir múltiples variables y sus respectivos resultados.",
        fontSize = 14.sp,
        textAlign = TextAlign.Justify
    )
}