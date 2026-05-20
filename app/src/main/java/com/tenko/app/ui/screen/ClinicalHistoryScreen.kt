package com.tenko.app.ui.screen

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButtonDefaults.Icon
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.tenko.app.R
import com.tenko.app.data.model.AnswerType
import com.tenko.app.data.model.ClinicalQuestion
import com.tenko.app.data.model.clinicalHistoryQuestions
import com.tenko.app.data.serializable.ClinicalHistoryResponse
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
import com.tenko.app.ui.components.AnswerSelector
import com.tenko.app.ui.components.ClinicalInfoRow
import com.tenko.app.ui.components.EmptyClinicalHistoryState
import com.tenko.app.ui.components.EmptyStateFullscreen
import com.tenko.app.ui.components.SectionTitle
import com.tenko.app.ui.components.nameInput
import com.tenko.app.ui.theme.RaisinBlack
import com.tenko.app.ui.theme.SweetGrey
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClinicalHistoryScreen(
    navController: NavController,
    authViewModel: AuthViewModel = viewModel(),
    viewModel: ChatViewModel = viewModel()
) {
    // Cargar datos al iniciar
    LaunchedEffect(Unit) { viewModel.fetchMyHistory() }

    // Observamos los datos del historial y el estado de carga
    val history by viewModel.historyData.collectAsState()
    val isRefreshing by viewModel.isLoading.collectAsState()
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    Box(modifier = Modifier.fillMaxSize()) {
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
                    viewModel.fetchMyHistory()
                    authViewModel.getUser(navController)
                },
                modifier = Modifier
                    .background(White)
                    .padding(paddingValues)
            ) {
                if (history?.last_name.isNullOrBlank() && !isRefreshing)
                    EmptyClinicalHistoryState(
                        icon = R.drawable.folder_open_solid_full,
                        title = "No hay historial clínico registrado",
                        description = "Agrega tu historial clínico para recibir recomendaciones personalizadas y mejorar tu salud integral.",
                        onClick = { navController.navigate(AppScreens.ChatScreen.route) }
                    )
                else {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 20.dp)
                            .verticalScroll(rememberScrollState())
                    ) {
                        Spacer(modifier = Modifier.height(30.dp))

                        TextButton(
                            onClick = { navController.navigate(AppScreens.ChatScreen.route) },
                            modifier = Modifier.align(Alignment.CenterHorizontally),
                            colors = ButtonDefaults.textButtonColors(contentColor = Tekhelet)
                        ) {
                            Text("Actualizar historial clínico via chat")
                        }

                        clinicalHistoryQuestions.forEach { question ->
                            ClinicalInfoRow(
                                question = question,
                                value = getCurrentValue(question.id, history),
                                onUpdate = { answer ->
                                    viewModel.updateSingleField(
                                        question.id,
                                        answer,
                                        navController
                                    )

                                    Toast.makeText(
                                        context,
                                        "${question.id} actualizado",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            )
                        }

                        Text(
                            text = "Nota: Algunos campos como el sexo biológico pueden afectar las recomendaciones que recibes, por lo que es importante mantenerlos actualizados.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Gray,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )

                        /*val question = clinicalHistoryQuestions.first { it.id == "sex_biology" }
                        ClinicalInfoRow(
                            question = question,
                            value = history?.sex_biology.orEmpty(),
                            onUpdate = { answer ->
                                val mappedValue = when (answer) {
                                    "Femenino" -> "femenine"
                                    "Masculino" -> "masculine"
                                    else -> null
                                }

                                viewModel.updateSingleField(
                                    "sex_biology",
                                    mappedValue,
                                    navController
                                )

                                Toast.makeText(
                                    context,
                                    "Sexo biológico actualizado",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        )*/


                        Spacer(modifier = Modifier.height(30.dp))
                    }
                }
            }
        }

        if (isRefreshing) {
            SplashScreen()
        }
    }
}

private fun getCurrentValue(fieldId: String, history: ClinicalHistoryResponse?): String {
    return when (fieldId) {
        "last_name" -> history?.last_name.orEmpty()
        "second_last_name" -> history?.second_last_name.orEmpty()
        "birthdate" -> history?.birthdate?.toString().orEmpty()
        "sex_biology" -> when (history?.sex_biology) {
            "femenine" -> "Femenino"
            "masculine" -> "Masculino"
            else -> "No registrado"
        }

        "sex_legally" -> when (history?.sex_legally) {
            "femenine" -> "Femenino"
            "masculine" -> "Masculino"
            else -> "No registrado"
        }

        "depression_screening" -> when (history?.depression_screening) {
            null -> ""
            true -> "Sí"
            false -> "No"
        }

        "depression" -> when (history?.depression) {
            null -> ""
            true -> "Sí"
            false -> "No"
        }

        "memory_screening" -> when (history?.memory_screening) {
            null -> ""
            true -> "Sí"
            false -> "No"
        }

        "memory_alterations" -> when (history?.memory_alterations) {
            null -> ""
            true -> "Sí"
            false -> "No"
        }

        "dementia" -> when (history?.dementia) {
            null -> ""
            true -> "Sí"
            false -> "No"
        }

        "urinary_incontinence_screening" -> when (history?.urinary_incontinence_screening) {
            null -> ""
            true -> "Sí"
            false -> "No"
        }

        "urinary_incontinence" -> when (history?.urinary_incontinence) {
            null -> ""
            true -> "Sí"
            false -> "No"
        }

        "anemia_screening" -> when (history?.anemia_screening) {
            null -> ""
            true -> "Sí"
            false -> "No"
        }

        "obesity_screening" -> when (history?.obesity_screening) {
            null -> ""
            true -> "Sí"
            false -> "No"
        }

        "osteoporosis_screening" -> when (history?.osteoporosis_screening) {
            null -> ""
            true -> "Sí"
            false -> "No"
        }

        "diabetes_mellitus" -> when (history?.diabetes_mellitus) {
            "none" -> "Ninguna"
            "type_1" -> "Tipo 1"
            "type_2" -> "Tipo 2"
            "gestational" -> "Gestacional"
            "prediabetes" -> "Prediabetes"
            else -> "No registrado"
        }

        "arterial_hypertension" -> when (history?.arterial_hypertension) {
            null -> ""
            true -> "Sí"
            false -> "No"
        }

        "sustance_use" -> when (history?.sustance_use) {
            null -> ""
            listOf("none") -> "No"
            else -> "Sí"
        }

        "std" -> when (history?.std) {
            null -> ""
            listOf("none") -> "No"
            else -> "Sí"
        }

        "turner_syndrome_screening" -> when (history?.turner_syndrome_screening) {
            null -> ""
            true -> "Sí"
            false -> "No"
        }

        "endometriosis_screening" -> when (history?.endometriosis_screening) {
            null -> ""
            true -> "Sí"
            false -> "No"
        }

        "endometriosis" -> when (history?.endometriosis) {
            null -> ""
            true -> "Sí"
            false -> "No"
        }

        "pcos_screening" -> when (history?.pcos_screening) {
            null -> ""
            true -> "Sí"
            false -> "No"
        }

        "pcos" -> when (history?.pcos) {
            null -> ""
            true -> "Sí"
            false -> "No"
        }

        "sexually_active" -> when (history?.sexually_active) {
            null -> ""
            true -> "Sí"
            false -> "No"
        }

        "average_menstrual_cycle" -> history?.average_menstrual_cycle?.toString().orEmpty()
        "average_ovulation" -> history?.average_ovulation?.toString().orEmpty()
        "last_period_date" -> history?.last_period_date?.toString().orEmpty()
        "regularity" -> history?.regularity.orEmpty()
        "miscarriages_abortions" -> history?.miscarriages_abortions?.toString().orEmpty()
        else -> ""
    }
}

private fun mapValueToBackend(fieldId: String, displayValue: String): Any? {
    return when (fieldId) {
        // Campos de texto directo o números (si tu backend los acepta como String)
        "last_name", "second_last_name", "birthdate", "regularity",
        "average_menstrual_cycle", "average_ovulation", "last_period_date", "miscarriages_abortions" -> {
            displayValue.ifBlank { null }
        }

        // Campos de Selección de Sexo
        "sex_biology", "sex_legally" -> when (displayValue) {
            "Femenino" -> "femenine"
            "Masculino" -> "masculine"
            else -> null
        }

        // Campos de Diabetes Mellitus
        "diabetes_mellitus" -> when (displayValue) {
            "Ninguna" -> "none"
            "Tipo 1" -> "type_1"
            "Tipo 2" -> "type_2"
            "Gestacional" -> "gestational"
            "Prediabetes" -> "prediabetes"
            else -> "none"
        }

        // Campos de tipo Lista (Sustancias y ETS)
        "sustance_use", "std" -> {
            if (displayValue == "No" || displayValue.isBlank()) listOf("none") else listOf("detected")
            // Nota: Ajusta "detected" por el string que maneje tu backend si es "Sí"
        }

        // Todos los campos Booleanos (Mapeo genérico)
        else -> when (displayValue) {
            "Sí" -> true
            "No" -> false
            else -> null
        }
    }
}