package com.tenko.app.ui.screen

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tenko.app.data.view.LabViewModel
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.tenko.app.data.serializable.LabResultResponse
import com.tenko.app.data.serializable.LabResultUpdate
import com.tenko.app.ui.components.AppTopBar
import com.tenko.app.ui.theme.* // Asumiendo que aquí están Tekhelet, White, etc.

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

    var isRefreshing by remember { mutableStateOf(viewModel.isLoading) }

    Scaffold(
        topBar = {
            AppTopBar(
                title = "Estudios de Laboratorio",
                onBackClick = { navController.popBackStack() }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { /* Lógica para agregar NUEVO ESTUDIO (estudio base) */ },
                containerColor = Tekhelet,
                contentColor = White,
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Agregar nuevo estudio")
            }
        }
    ) { paddingValues ->
        // PullToRefreshBox similar a ClinicalHistory
        PullToRefreshBox(
            isRefreshing = isRefreshing,
            onRefresh = { viewModel.fetchMyStudies() },
            modifier = Modifier
                .background(White)
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp, vertical = 20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                if (viewModel.studies.isEmpty()) {
                    Text(
                        "No hay estudios registrados.",
                        modifier = Modifier.align(Alignment.CenterHorizontally),
                        color = Color.Gray
                    )
                }

                viewModel.studies.forEach { study ->
                    // El laboratory_name se usa como nombre del estudio (Título de Sección)
                    LabStudySection(
                        studyName = study.laboratory_name ?: "Estudio sin nombre",
                        results = study.results,
                        onAddResult = { /* Lógica para agregar resultado a este estudio específico */ },
                        onEditResult = { result ->
                            /* Lógica para editar un LabResult específico */
                        }
                    )
                }

                Spacer(modifier = Modifier.height(80.dp)) // Espacio para el FAB
            }
        }
    }
}

@Composable
fun LabStudySection(
    studyName: String,
    results: List<LabResultResponse>,
    onAddResult: () -> Unit,
    onEditResult: (LabResultResponse) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 24.dp, bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // laboratory_name como Título
            Text(
                text = studyName,
                style = MaterialTheme.typography.titleMedium,
                color = Tekhelet,
                modifier = Modifier.weight(1f)
            )

            // Botón "+" al lado del título para agregar resultado al estudio
            IconButton(onClick = onAddResult) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Agregar resultado",
                    tint = Tekhelet,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        results.forEach { result ->
            LabInfoRow(
                label = result.parameter, // Ejemplo: "Glucosa"
                value = "${result.value} ${result.unit}",
//                trend = result.trend, // "up", "down", "stable"
                onClick = { onEditResult(result) }
            )
        }
    }
}

@Composable
fun LabInfoRow(
    label: String,
    value: String,
//    trend: String?,
    onClick: () -> Unit
) {
    // Reutilizamos la lógica de InfoRow pero agregamos el icono de tendencia
    Surface(
        onClick = onClick,
        color = Color.Transparent
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = label, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                Text(text = value, style = MaterialTheme.typography.bodyLarge, color = Color.Black)
            }

            // Iconos de Trend
            /*trend?.let {
                val (icon, color) = when (it.lowercase()) {
                    "up" -> Icons.Default.TrendingUp to Color.Red
                    "down" -> Icons.Default.TrendingDown to Color.Blue
                    else -> Icons.Default.ShowChart to Color.Gray
                }
                Icon(
                    imageVector = icon,
                    contentDescription = "Tendencia",
                    tint = color,
                    modifier = Modifier.padding(horizontal = 8.dp).size(24.dp)
                )
            }*/

            // Indicador de edición (opcional, similar a tu ClinicalHistory)
            Text(
                "Editar",
                style = MaterialTheme.typography.labelSmall,
                color = Tekhelet.copy(alpha = 0.6f)
            )
        }
    }
    HorizontalDivider(color = Color.LightGray.copy(alpha = 0.3f), thickness = 1.dp)
}