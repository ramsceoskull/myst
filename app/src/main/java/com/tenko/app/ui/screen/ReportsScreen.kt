package com.tenko.app.ui.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.tenko.app.R
import com.tenko.app.data.model.ReportItem
import com.tenko.app.data.serializable.CycleResponse
import com.tenko.app.data.view.AuthViewModel
import com.tenko.app.data.view.CycleViewModel
import com.tenko.app.data.view.ReportViewModel
import com.tenko.app.ui.components.AppTopBar

@Composable
fun ReportsScreen(
    navController: NavController,
    authViewModel: AuthViewModel,
    reportViewModel: ReportViewModel = viewModel(),
    cycleViewModel: CycleViewModel = viewModel()
) {
    val context = LocalContext.current
    val user = authViewModel.currentUser
    val userId = user?.id_user?.toString() ?: ""

    // Estado para alternar entre historial y selector de ciclos
    var showCycleSelector by remember { mutableStateOf(false) }

    LaunchedEffect(userId) {
        if (userId.isNotEmpty()) {
            reportViewModel.fetchSavedReports(userId)
            cycleViewModel.fetchData()
        }
    }

    Scaffold(
        topBar = {
            AppTopBar(
                title = if (showCycleSelector) "Seleccionar Ciclo" else "Mis Reportes",
                onBackClick = {
                    if (showCycleSelector) showCycleSelector = false
                    else { navController.popBackStack() }
                }
            )
        }
    ) { paddingValues ->
        Column(modifier = Modifier
            .padding(paddingValues)
            .fillMaxSize()
        ) {
            // Botones de Acción Superior
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Botón Reporte Clínico Completo
                Button(
                    onClick = { reportViewModel.generateAndSaveReport(userId) },
                    modifier = Modifier.weight(1f),
                    enabled = !reportViewModel.isLoading && userId.isNotEmpty()
                ) {
                    Text("Historial Completo", textAlign = TextAlign.Center)
                }

                // Botón para abrir selector de ciclos
                Button(
                    onClick = { showCycleSelector = true },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                ) {
                    Text("Reporte por Ciclo", textAlign = TextAlign.Center)
                }
            }

            if (reportViewModel.isLoading) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            }

            if (showCycleSelector) {
                // LISTADO DE CICLOS DISPONIBLES
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(cycleViewModel.cycles) { cycle ->
                        CycleCardForReport(cycle) {
                            reportViewModel.generateAndSaveReport(userId, cycle.id_cycle)
                            showCycleSelector = false // Regresar al historial tras solicitar
                        }
                    }
                }
            } else {
                // LISTADO DE REPORTES GUARDADOS (PDFs)
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(reportViewModel.savedReports) { report ->
                        ReportCard(report) {
                            reportViewModel.openPdf(context, report.url)
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun CycleCardForReport(cycle: CycleResponse, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("🗓️", fontSize = 24.sp)
            Column(modifier = Modifier.padding(start = 16.dp)) {
                Text(
                    text = "Ciclo: ${cycle.start_date}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Presiona para generar reporte de este periodo",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

@Composable
fun ReportCard(report: ReportItem, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF8F9FA))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(id = R.drawable.file_pdf_regular_full), // Asegúrate de tener un icono
                contentDescription = null,
                modifier = Modifier.size(32.dp),
                tint = Color.Red
            )
            Column(modifier = Modifier.padding(start = 16.dp)) {
                Text(report.name, fontWeight = FontWeight.Medium, maxLines = 1)
                val date = java.text.SimpleDateFormat("dd MMM yyyy", java.util.Locale.getDefault())
                    .format(java.util.Date(report.date))
                Text("Generado el $date", style = MaterialTheme.typography.labelSmall)
            }
        }
    }
}

/*
@Composable
fun ReportsScreen(navController: NavController, authViewModel: AuthViewModel = viewModel(), reportViewModel: ReportViewModel = viewModel()) {
    val context = LocalContext.current

    // Obtenemos el usuario del estado de Auth
    // 1. Obtenemos el usuario del estado del ViewModel (es reactivo)
    val user = authViewModel.currentUser

    // 2. Si por alguna razón el usuario es nulo (ej. refresco manual),
    // lo pedimos una SOLA VEZ al entrar.
    LaunchedEffect(Unit) {
        if (user == null) {
            authViewModel.getUser(navController)
        }
    }

    // Este efecto se dispara cada vez que el objeto 'user' cambia
    LaunchedEffect(user) {
        user?.id_user?.let { id ->
            // Solo cuando el ID existe y es válido, cargamos los reportes
            reportViewModel.fetchSavedReports(id.toString())
        }
    }

    // En tus botones, asegúrate de pasar el ID real
    Button(
        onClick = {
            user?.id_user?.let { id ->
                reportViewModel.generateAndSaveReport(id.toString())
            }
        },
        enabled = user != null && !reportViewModel.isLoading
    ) {
        if (reportViewModel.isLoading) {
            CircularProgressIndicator(modifier = Modifier.size(20.dp))
        } else {
            Text("Generar Reporte")
        }
    }
}*/
