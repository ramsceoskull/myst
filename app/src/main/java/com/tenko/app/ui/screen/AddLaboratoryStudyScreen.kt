package com.tenko.app.ui.screen

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.autofill.ContentType
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.Pair
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
import com.tenko.app.ui.components.DropdownField
import com.tenko.app.ui.components.FloatingActionButton
import com.tenko.app.ui.components.FormTextField
import com.tenko.app.ui.components.HeaderAddLabResults
import com.tenko.app.ui.components.LaboratoryStudyDropdown
import com.tenko.app.ui.components.LaboratoryVariableCard
import com.tenko.app.ui.components.inputField
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

    val scrollState = rememberScrollState()

    var study by remember { mutableStateOf<LaboratoryStudy?>(null) }

    val variables = remember {
        mutableStateListOf(LaboratoryVariable(expanded = true))
    }

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
                var hasErrors = false

                val hasAtLeastOneValidVariable = variables.any { it.isValid() }

                variables.forEachIndexed { index, variable ->
                    val isEmpty = variable.isEmpty()

                    // Si no existe ninguna variable válida,
                    // la primera vacía debe marcar error
                    if (isEmpty && !hasAtLeastOneValidVariable) {
                        variables[index] = variable.copy(
                            hasError = true,
                            parameterError = "Ingresa el nombre de la variable",
                            valueError = "Ingresa un valor",
                            unitError = "No se ha ingresado la unidad de medida"
                        )

                        hasErrors = true

                        return@forEachIndexed
                    }

                    // Si ya existe una válida, ignoramos vacías extra
                    if (isEmpty) {
                        variables[index] = variable.copy(
                            hasError = false,
                            parameterError = null,
                            valueError = null,
                            unitError = null
                        )

                        return@forEachIndexed
                    }

                    val validatedVariable = variable.validate()

                    variables[index] = validatedVariable

                    if (validatedVariable.hasError) hasErrors = true
                }

                val validResults = variables.mapNotNull { it.toLabResult() }

                if (study == null || studyDate == null || hasErrors || !hasAtLeastOneValidVariable) {
                    nameError =
                        if (study == null)
                            "Por favor selecciona el estudio"
                        else null

                    dateError =
                        if (studyDate == null)
                            "Por favor selecciona la fecha"
                        else ""

                    if (!hasAtLeastOneValidVariable) {
                        Toast.makeText(
                            context,
                            "Agrega al menos una variable",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    return@FloatingActionButton
                }

                val payload = LabStudyCreate(
                    laboratory_name = study?.displayName,
                    test_date = studyDate!!,
                    results = validResults
                )

                viewModel.createLabStudy(payload, context) {
                    navController.popBackStack()
                }
            }
        },
        containerColor = White
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .imePadding()
                .padding(padding)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item { HeaderAddLabResults() }

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
                    modifier = Modifier.focusRequester(dateFocus),
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

            item {
                Text(
                    text = "Variables y resultados",
                    color = Color.Gray,
                    fontSize = 14.sp,
                    textDecoration = TextDecoration.Underline
                )
            }

            itemsIndexed(variables) { index, variable ->
                LaboratoryVariableCard(
                    index = index,
                    variable = variable,
                    scrollState = scrollState,
                    scope = scope,
                    variableFocus = variableFocus,
                    valueFocus = valueFocus,

                    onVariableChange = {
                        variables[index] = it
                    }
                )
            }

            item {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Button(
                        onClick = {
                            var hasErrors = false
                            val lastIndex = variables.lastIndex
                            val lastVariable = variables[lastIndex]

                            val validatedVariable = lastVariable.validate()

                            if (validatedVariable.hasError) hasErrors = true

                            if (hasErrors) {
                                Toast.makeText(
                                    context,
                                    "Completa la variable actual",
                                    Toast.LENGTH_SHORT
                                ).show()

                                return@Button
                            }

                            variables.forEachIndexed { i, item ->
                                variables[i] = item.copy(expanded = false)
                            }

                            variables.add(LaboratoryVariable(expanded = true))
                        },
                        shape = CircleShape,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Tekhelet,
                            contentColor = White
                        ),
                        contentPadding = PaddingValues(12.dp),
                        content = {
                            Icon(
                                painter = painterResource(R.drawable.notes_medical_solid_full),
                                contentDescription = null,
                                modifier = Modifier.size(30.dp)
                            )
                        }
                    )

                    Text(
                        text = "Nueva variable",
                        color = Tekhelet.copy(alpha = 0.7f),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                    )
                }

                Spacer(modifier = Modifier.height(30.dp))
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

private fun LaboratoryVariable.validate(): LaboratoryVariable {
    var parameterError: String? = null
    var valueError: String? = null
    var unitError: String? = null

    if (parameter.isBlank()) parameterError = "Ingresa el nombre de la variable"

    if (value.isBlank()) valueError =
        "Ingresa un valor" else if (value.toDoubleOrNull() == null) valueError =
        "Ingresa un número válido"

    if (unit.isBlank()) unitError = "No se ha ingresado la unidad de medida"

    val hasError =
        parameterError != null || valueError != null || unitError != null

    return copy(
        hasError = hasError,
        parameterError = parameterError,
        valueError = valueError,
        unitError = unitError
    )
}

private fun LaboratoryVariable.isEmpty(): Boolean {
    return parameter.isBlank() && value.isBlank() && unit.isBlank()
}

private fun LaboratoryVariable.isValid(): Boolean {
    return parameter.isNotBlank() && value.toDoubleOrNull() != null && unit.isNotBlank()
}

private fun LaboratoryVariable.toLabResult(): LabResultBase? {
    val parsedValue = value.toDoubleOrNull() ?: return null

    if (!isValid()) return null

    return LabResultBase(
        parameter = parameter.trim(),
        value = parsedValue,
        unit = unit.ifBlank { null },
        reference_range = null
    )
}