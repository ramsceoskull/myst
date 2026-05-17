package com.tenko.app.ui.screen

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.HorizontalRule
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.tenko.app.R
import com.tenko.app.data.serializable.LabResultBase
import com.tenko.app.data.serializable.LabResultResponse
import com.tenko.app.data.serializable.LabResultUpdate
import com.tenko.app.data.serializable.LabStudyResponse
import com.tenko.app.data.serializable.ParameterDataPoint
import com.tenko.app.data.serializable.Trend
import com.tenko.app.data.view.LabViewModel
import com.tenko.app.navigation.AppScreens
import com.tenko.app.ui.components.AppTopBar
import com.tenko.app.ui.components.EmptyStateFullscreen
import com.tenko.app.ui.components.FloatingActionButton
import com.tenko.app.ui.theme.Tekhelet
import com.tenko.app.ui.theme.White
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LaboratoryStudiesScreen(
    navController: NavController,
    viewModel: LabViewModel = viewModel()
) {
    // Cargar estudios al iniciar
    LaunchedEffect(Unit) {
        viewModel.fetchMyStudies()
    }
    val context = LocalContext.current
    val isRefreshing = viewModel.isLoading
    val lazyState = rememberLazyListState()

    var showSearchDialog by remember { mutableStateOf(false) }
    var showAddResultDialog by remember { mutableStateOf(false) }
    var parameterSearch by remember { mutableStateOf("") }
    var selectedResult by remember { mutableStateOf<LabResultResponse?>(null) }
    var selectedStudyId by remember { mutableStateOf<Int?>(null) }

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            topBar = {
                AppTopBar(
                    title = "Estudios de Laboratorio",
                    onBackClick = { navController.popBackStack() },
                ) {}
            },
            floatingActionButton = {
                FloatingActionButton(R.drawable.flask_vial_solid_full) {
                    navController.navigate(AppScreens.AddLaboratoryStudyScreen.route)
                }
            }
        ) { paddingValues ->
            PullToRefreshBox(
                isRefreshing = isRefreshing,
                onRefresh = { viewModel.fetchMyStudies() },
                modifier = Modifier
                    .background(White)
                    .padding(paddingValues)
            ) {
                // --- LISTA GENERAL DE ESTUDIOS DE LA API ---
                if (viewModel.studies.isEmpty() && !isRefreshing)
                    EmptyStateFullscreen(
                        icon = R.drawable.square_poll_vertical_solid_full,
                        title = "No hay estudios registrados",
                        description = "Agrega tus estudios de laboratorio para llevar un mejor control de tu salud."
                    )
                else {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 20.dp)
                    ) {
                        TextButton(
                            onClick = { showSearchDialog = true },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(IntrinsicSize.Max)
                                .padding(top = 30.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.textButtonColors(
                                containerColor = Tekhelet,
                                contentColor = White
                            ),
                            contentPadding = PaddingValues(vertical = 12.dp),
                            content = {
                                Icon(
                                    painter = painterResource(id = R.drawable.chart_simple_solid_full),
                                    contentDescription = "Ver evolución",
                                    modifier = Modifier.size(30.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Evolución",
                                    fontSize = 18.sp,
                                    style = MaterialTheme.typography.labelSmall,
                                )
                            }
                        )

                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            state = lazyState,
//                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            item {
                                // --- SECCIÓN NUEVA: RENDERIZADO DE LA EVOLUCIÓN HISTÓRICA ---
                                viewModel.evolutionData?.let { evolution ->
                                    Card(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(top = 16.dp),
                                        colors = CardDefaults.cardColors(
                                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                                        ),
                                        shape = RoundedCornerShape(12.dp)
                                    ) {
                                        Column(modifier = Modifier.padding(16.dp)) {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Text(
                                                    text = "Evolución de ${evolution.parameter}",
                                                    style = MaterialTheme.typography.titleMedium,
                                                    color = Tekhelet
                                                )
                                                IconButton(onClick = {
                                                    viewModel.evolutionData = null
                                                }) {
                                                    Icon(
                                                        Icons.Default.Close,
                                                        contentDescription = "Cerrar",
                                                        tint = Color.Gray
                                                    )
                                                }
                                            }
                                            Spacer(modifier = Modifier.height(8.dp))

                                            // Lista longitudinal de puntos
                                            evolution.data_points.forEach { point ->
                                                EvolutionRowItem(point = point)
                                            }
                                        }
                                    }
                                }
                            }

                            item {
                                // Manejo de errores de la API
                                viewModel.errorMessage?.let { error ->
                                    Text(
                                        text = error,
                                        color = MaterialTheme.colorScheme.error,
                                        style = MaterialTheme.typography.bodyMedium,
                                        modifier = Modifier.padding(bottom = 16.dp)
                                    )
                                }
                            }

                            items(viewModel.studies) { study ->
                                LabStudySection(
                                    study = study,
                                    results = study.results,
                                    onDeleteStudy = {
                                        viewModel.deleteStudy(
                                            idStudy = study.id_study,
                                            context = context
                                        )
                                    },
                                    onAddResult = {
                                        selectedStudyId = study.id_study
                                        showAddResultDialog = true
                                    },
                                    onEditResult = { result ->
                                        selectedResult = result
                                        selectedStudyId = study.id_study
                                    },
                                    onDeleteResult = { result ->
                                        viewModel.deleteResultFromStudy(
                                            idStudy = study.id_study,
                                            idResult = result.id_result,
                                            context = context
                                        )
                                    }
                                )
                            }

                            item { Spacer(modifier = Modifier.height(80.dp)) }
                        }
                    }
                }
            }
        }

        // --- DIÁLOGO EMERGENTE CAPTURA PARÁMETRO ---
        if (showSearchDialog) {
            AlertDialog(
                onDismissRequest = { showSearchDialog = false },
                title = { Text("Evolución de Parámetro", color = Tekhelet) },
                text = {
                    OutlinedTextField(
                        value = parameterSearch,
                        onValueChange = { parameterSearch = it },
                        label = { Text("Ej: Glucosa, TSH, Hemoglobina") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (parameterSearch.isNotBlank()) {
                                viewModel.fetchParameterEvolution(parameterSearch.trim())
                                showSearchDialog = false
                                parameterSearch = ""
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Tekhelet)
                    ) {
                        Text("Buscar", color = White)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showSearchDialog = false }) {
                        Text("Cancelar", color = Color.Gray)
                    }
                }
            )
        }

        selectedResult?.let { result ->

            var value by remember(result.value) {
                mutableStateOf(result.value.toString())
            }

            var unit by remember(result.unit) {
                mutableStateOf(result.unit ?: "")
            }

            var expanded by remember {
                mutableStateOf(false)
            }

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

            AlertDialog(
                onDismissRequest = {
                    selectedResult = null
                },
                title = {
                    Text(
                        text = result.parameter,
                        color = Tekhelet
                    )
                },
                text = {

                    Column {

                        OutlinedTextField(
                            value = value,
                            onValueChange = {
                                value = it.filter { char ->
                                    char.isDigit() || char == '.'
                                }
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

                        Spacer(modifier = Modifier.height(16.dp))

                        ExposedDropdownMenuBox(
                            expanded = expanded,
                            onExpandedChange = {
                                expanded = !expanded
                            }
                        ) {

                            OutlinedTextField(
                                value = unit,
                                onValueChange = {},
                                modifier = Modifier
                                    .menuAnchor()
                                    .fillMaxWidth(),
                                readOnly = true,
                                label = {
                                    Text("Unidad")
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

                                units.forEach { currentUnit ->

                                    DropdownMenuItem(
                                        text = {
                                            Text(currentUnit)
                                        },
                                        onClick = {
                                            unit = currentUnit
                                            expanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                },
                confirmButton = {

                    Button(
                        onClick = {

                            val parsedValue = value.toDoubleOrNull()

                            if (parsedValue != null) {
                                viewModel.updateResultFromStudy(
                                    idStudy = selectedStudyId!!,
                                    idResult = result.id_result,
                                    resultData = LabResultUpdate(
                                        value = parsedValue,
                                        unit = unit
                                    ),
                                    context = context
                                ) {
                                    Toast.makeText(
                                        context,
                                        "Parámetro actualizado correctamente",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }

                                selectedResult = null
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Tekhelet
                        )
                    ) {

                        Text(
                            "Guardar",
                            color = White
                        )
                    }
                },
                dismissButton = {

                    TextButton(
                        onClick = {
                            selectedResult = null
                        }
                    ) {

                        Text(
                            "Cancelar",
                            color = Color.Gray
                        )
                    }
                }
            )
        }

        if (showAddResultDialog && selectedStudyId != null) {

            var parameter by remember {
                mutableStateOf("")
            }

            var value by remember {
                mutableStateOf("")
            }

            var unit by remember {
                mutableStateOf("")
            }

            var expanded by remember {
                mutableStateOf(false)
            }

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

            AlertDialog(
                onDismissRequest = {
                    showAddResultDialog = false
                },

                title = {
                    Text(
                        text = "Agregar resultado",
                        color = Tekhelet
                    )
                },

                text = {

                    Column {

                        OutlinedTextField(
                            value = parameter,
                            onValueChange = {
                                parameter = it
                            },
                            modifier = Modifier.fillMaxWidth(),
                            label = {
                                Text("Parámetro")
                            },
                            singleLine = true
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        OutlinedTextField(
                            value = value,
                            onValueChange = {
                                value = it.filter { char ->
                                    char.isDigit() || char == '.'
                                }
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

                        Spacer(modifier = Modifier.height(16.dp))

                        ExposedDropdownMenuBox(
                            expanded = expanded,
                            onExpandedChange = {
                                expanded = !expanded
                            }
                        ) {

                            OutlinedTextField(
                                value = unit,
                                onValueChange = {},
                                modifier = Modifier
                                    .menuAnchor()
                                    .fillMaxWidth(),
                                readOnly = true,
                                label = {
                                    Text("Unidad")
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

                                units.forEach { currentUnit ->

                                    DropdownMenuItem(
                                        text = {
                                            Text(currentUnit)
                                        },
                                        onClick = {
                                            unit = currentUnit
                                            expanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                },

                confirmButton = {

                    Button(
                        onClick = {

                            val parsedValue = value.toDoubleOrNull()

                            if (
                                parameter.isBlank() ||
                                parsedValue == null
                            ) {

                                Toast.makeText(
                                    context,
                                    "Completa los campos correctamente",
                                    Toast.LENGTH_SHORT
                                ).show()

                                return@Button
                            }

                            viewModel.addResultToStudy(
                                idStudy = selectedStudyId!!,
                                resultData = LabResultBase(
                                    parameter = parameter.trim(),
                                    value = parsedValue,
                                    unit = unit.ifBlank { null },
                                    reference_range = null
                                ),
                                context = context
                            )

                            Toast.makeText(
                                context,
                                "Resultado agregado correctamente",
                                Toast.LENGTH_SHORT
                            ).show()

                            showAddResultDialog = false
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Tekhelet
                        )
                    ) {

                        Text(
                            "Guardar",
                            color = White
                        )
                    }
                },

                dismissButton = {

                    TextButton(
                        onClick = {
                            showAddResultDialog = false
                        }
                    ) {

                        Text(
                            "Cancelar",
                            color = Color.Gray
                        )
                    }
                }
            )
        }

        if (isRefreshing) {
            SplashScreen()
        }
    }
}

@Composable
fun LabStudySection(
    study: LabStudyResponse,
    results: List<LabResultResponse>,
    onDeleteStudy: () -> Unit,
    onAddResult: () -> Unit,
    onEditResult: (LabResultResponse) -> Unit,
    onDeleteResult: (LabResultResponse) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 24.dp, bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = study.laboratory_name ?: "Estudio sin nombre",
                    style = MaterialTheme.typography.titleMedium,
                    color = Tekhelet
                )
                Text(
                    text = study.test_date.format(DateTimeFormatter.ofPattern("dd MMM yyyy")),
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }

            IconButton(onClick = onAddResult) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Agregar resultado",
                    tint = Tekhelet,
                    modifier = Modifier.size(20.dp)
                )
            }
            IconButton(onClick = onDeleteStudy) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Eliminar estudio",
                    tint = Color.Red,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        results.forEach { result ->
            LabInfoRow(
                label = result.parameter,
                value = "${result.value} ${result.unit ?: ""}",
                onClick = { onEditResult(result) },
                onDelete = {
                    onDeleteResult(result)
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LabInfoRow(
    label: String,
    value: String,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {

    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = { value ->

            if (value == SwipeToDismissBoxValue.EndToStart) {
                onDelete()
                true
            } else {
                false
            }
        }
    )

    SwipeToDismissBox(
        state = dismissState,
        enableDismissFromStartToEnd = false,
        enableDismissFromEndToStart = true,

        backgroundContent = {

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Red)
                    .padding(horizontal = 20.dp),
                contentAlignment = Alignment.CenterEnd
            ) {

                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Eliminar",
                    tint = White
                )
            }
        },

        content = {

            Surface(
                onClick = onClick,
                color = Color.Transparent
            ) {

                Column {

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(White)
                            .padding(vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        Column(
                            modifier = Modifier.weight(1f)
                        ) {

                            Text(
                                text = label,
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.Gray
                            )

                            Text(
                                text = value,
                                style = MaterialTheme.typography.bodyLarge,
                                color = Color.Black
                            )
                        }

                        Text(
                            "Editar",
                            style = MaterialTheme.typography.labelSmall,
                            color = Tekhelet.copy(alpha = 0.6f)
                        )
                    }

                    HorizontalDivider(
                        color = Color.LightGray.copy(alpha = 0.3f),
                        thickness = 1.dp
                    )
                }
            }
        }
    )
}

// --- SUB-COMPONENTE AUXILIAR PARA CADA RENGLÓN DE LA EVOLUCIÓN LONGITUDINAL ---
@Composable
fun EvolutionRowItem(point: ParameterDataPoint) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text(
                text = "Fecha: ${point.test_date}",
                style = MaterialTheme.typography.bodySmall,
                color = Color.DarkGray
            )
            Text(
                text = "${point.value} ${point.unit ?: ""}",
                style = MaterialTheme.typography.bodyLarge,
                color = Color.Black
            )
        }

        // Mapeo dinámico de Flecha / Color basado en tu lógica de negocio
        val (icon, tintColor) = when (point.trend) {
            Trend.UP -> Icons.Default.ArrowUpward to Color(0xFFD32F2F)      // Rojo
            Trend.DOWN -> Icons.Default.ArrowDownward to Color(0xFFD32F2F)  // Rojo
            Trend.STABLE -> Icons.Default.HorizontalRule to Color(0xFF388E3C)// Verde
            Trend.NONE -> null to Color.Transparent
        }

        if (icon != null) {
            Icon(
                imageVector = icon,
                contentDescription = "Tendencia ${point.trend}",
                tint = tintColor,
                modifier = Modifier
                    .size(24.dp)
                    .padding(end = 4.dp)
            )
        }
    }
    HorizontalDivider(color = Color.Gray.copy(alpha = 0.2f), thickness = 0.5.dp)
}