package com.tenko.myst.ui.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.tenko.myst.data.view.AuthViewModel
import com.tenko.myst.data.view.CycleViewModel
import com.tenko.myst.data.view.ReportViewModel
import com.tenko.myst.ui.components.AppTopBar
import com.tenko.myst.ui.components.ReportCard
import com.tenko.myst.ui.theme.BackgroundColor

@Composable
fun ReportsScreen(
    navController: NavController,
    authViewModel: AuthViewModel = viewModel(),
    cycleViewModel: CycleViewModel = viewModel(),
    reportViewModel: ReportViewModel = viewModel()
) {
    val context = LocalContext.current
    // Obtenemos el ID directamente del estado del usuario logueado
    // 1. Obtenemos el usuario del estado del ViewModel (es reactivo)
    val user = authViewModel.currentUser

    // 2. Si por alguna razón el usuario es nulo (ej. refresco manual),
    // lo pedimos una SOLA VEZ al entrar.
    LaunchedEffect(Unit) {
        if (user == null) {
            authViewModel.getUser(navController)
        }
    }

    LaunchedEffect(user) {
        user?.id_user?.let { id ->
            // Solo cuando el ID existe y es válido, cargamos los reportes
            reportViewModel.fetchSavedReports(id.toString())
        }
    }

    Scaffold(
        topBar = {
            AppTopBar(
                title = "Mis Reportes Médicos",
                onBackClick = { navController.popBackStack() }
            )
        },
        floatingActionButton = {
            user?.id_user?.let { id ->
                ExpandableFAB(
                    listOf(
                        reportViewModel.fetchSavedReports(id.toString())
                    )
                )
            }
        },
        containerColor = BackgroundColor
    ) { padding ->

        // Al listar
        LazyColumn {
            items(reportViewModel.savedReports) { report ->
                Text(
                    text = report.name,
                    modifier = Modifier.clickable { reportViewModel.openPdf(context, report.url) }
                )
            }
        }
        if (reportViewModel.isLoading) {
            LinearProgressIndicator(modifier = Modifier
                .fillMaxWidth()
                .padding(padding))
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(reportViewModel.savedReports) { report ->
                ReportCard(report, onClick = { reportViewModel.openPdf(context, report.url) })
            }
        }
    }
}

@Composable
fun ExpandableFAB(onClicks: List<Unit>) {
    var expanded by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.BottomEnd
    ) {
        Column(
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.padding(16.dp)
        ) {
            AnimatedVisibility(
                visible = expanded,
                enter = fadeIn() + slideInVertically { it },
                exit = fadeOut() + slideOutVertically { it }
            ) {
                Column(
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    for ((index, onClick) in onClicks.withIndex()) {
                        SmallFAB(
                            text = if (index == 0) "Ciclo" else "Reporte Clínico",
                            onClick = { onClick }
                        )
                    }
                }
            }

            FloatingActionButton(
                onClick = { expanded = !expanded }
            ) {
                Icon(
                    imageVector = if (expanded) Icons.Default.Close else Icons.Default.Add,
                    contentDescription = "FAB"
                )
            }
        }
    }
}

@Composable
fun SmallFAB(text: String, onClick: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            shape = RoundedCornerShape(8.dp),
            tonalElevation = 4.dp
        ) {
            Text(
                text = text,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        SmallFloatingActionButton(
            onClick = onClick
        ) {
            Icon(Icons.Default.Star, contentDescription = text)
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
