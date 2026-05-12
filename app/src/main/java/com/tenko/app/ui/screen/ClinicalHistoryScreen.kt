package com.tenko.app.ui.screen

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButtonDefaults.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.tenko.app.data.serializable.UserUpdate
import com.tenko.app.data.view.AuthViewModel
import com.tenko.app.data.view.ChatViewModel
import com.tenko.app.navigation.AppScreens
import com.tenko.app.ui.components.AppTopBar
import com.tenko.app.ui.components.InfoRow
import com.tenko.app.ui.theme.AntiFlashWhite
import com.tenko.app.ui.theme.BackgroundColor
import com.tenko.app.ui.theme.PompAndPower
import com.tenko.app.ui.theme.Tekhelet
import com.tenko.app.ui.theme.White
import com.tenko.app.data.serializable.ClinicalHistoryUpdate
import com.tenko.app.ui.theme.RaisinBlack
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClinicalHistoryScreen(
    navController: NavController,
    viewModel: ChatViewModel = viewModel()
) {
    // Observamos los datos del historial y el estado de carga
    val history by viewModel.historyData.collectAsState()
    val isTyping by viewModel.isTyping.collectAsState()

    var isRefreshing by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    // Cargar datos al iniciar
    LaunchedEffect(Unit) {
        viewModel.fetchMyHistory()
    }

    Scaffold(
        topBar = {
            AppTopBar(
                title = "Historial Clínico",
                onBackClick = { navController.popBackStack() }
            )
        }
    ) { paddingValues ->
        PullToRefreshBox(
            isRefreshing = isRefreshing,
            onRefresh = {
                scope.launch {
                    isRefreshing = true
                    viewModel.fetchMyHistory()
                    delay(1000)
                    isRefreshing = false
                }
            },
            modifier = Modifier
                .background(White)
                .padding(top = paddingValues.calculateTopPadding())
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 25.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Spacer(modifier = Modifier.height(20.dp))

                // SECCIÓN: Identificación
                SectionTitle("Identificación")

                InfoRow(
                    label = "Apellido Paterno",
                    value = history?.last_name ?: "No registrado",
                    onClick = { /* Lógica para abrir input de texto similar a UpdateProfile */ }
                )

                InfoRow(
                    label = "Sexo biológico",
                    value = history?.sex_biology ?: "No registrado",
                    onClick = { /* Diálogo de selección */ }
                )

                // SECCIÓN: Salud Femenina
                SectionTitle("Salud Femenina")

                // Ejemplo de edición directa con la función dinámica que creamos
                InfoRow(
                    label = "¿Padeces SOP (PCOS)?",
                    value = when (history?.pcos) {
                        true -> "Sí"
                        false -> "No"
                        else -> "Sin especificar"
                    },
                    onClick = {
                        // Diálogo rápido de cambio
                        viewModel.updateSingleField("pcos", !(history?.pcos ?: false))
                        Toast.makeText(context, "Dato actualizado", Toast.LENGTH_SHORT).show()
                    }
                )

                InfoRow(
                    label = "¿Vida sexual activa?",
                    value = if (history?.sexually_active == true) "Sí" else "No",
                    onClick = {
                        viewModel.updateSingleField(
                            "sexually_active",
                            !(history?.sexually_active ?: false)
                        )
                    }
                )

                // SECCIÓN: Condiciones Médicas
                SectionTitle("Condiciones Médicas")

                InfoRow(
                    label = "Diabetes Mellitus",
                    value = history?.diabetes_mellitus ?: "Ninguna",
                    onClick = { /* Diálogo de opciones */ }
                )

                InfoRow(
                    label = "Hipertensión Arterial",
                    value = if (history?.arterial_hypertension == true) "Sí" else "No",
                    onClick = {
                        viewModel.updateSingleField(
                            "arterial_hypertension",
                            !(history?.arterial_hypertension ?: false)
                        )
                    }
                )

                Spacer(modifier = Modifier.height(30.dp))

                // Botón para volver al chat si quiere usar el modo cuestionario
                TextButton(
                    onClick = { navController.navigate(AppScreens.ChatScreen.route) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.textButtonColors(contentColor = Tekhelet)
                ) {
                    Text("¿Prefieres actualizar vía chat?")
                }

                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }
}

@Composable
fun SectionTitle(text: String) {
    Text(
        text = text,
        modifier = Modifier.padding(top = 24.dp, bottom = 8.dp),
        style = MaterialTheme.typography.labelLarge,
        color = Tekhelet
    )
}