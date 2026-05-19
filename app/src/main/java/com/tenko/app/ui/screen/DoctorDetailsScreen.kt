package com.tenko.app.ui.screen

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import com.tenko.app.data.notifications.NotificationPermissionHelper
import com.tenko.app.data.serializable.ContactResponse
import com.tenko.app.data.view.DoctorViewModel
import com.tenko.app.navigation.AppScreens
import com.tenko.app.ui.components.AppTopBar
import com.tenko.app.ui.components.DoctorHeader
import com.tenko.app.ui.components.EmptyAppointmentState
import com.tenko.app.ui.components.FloatingActionButton
import com.tenko.app.ui.components.ScheduleCard
import com.tenko.app.ui.theme.AntiFlashWhite
import com.tenko.app.ui.theme.BackgroundColor
import com.tenko.app.ui.theme.PompAndPower
import com.tenko.app.ui.theme.StarsLove
import com.tenko.app.ui.theme.SweetGrey
import com.tenko.app.ui.theme.Tekhelet
import com.tenko.app.ui.theme.White
import kotlinx.coroutines.launch

@Composable
fun DoctorDetailsScreen(
    navController: NavController,
    doctor: ContactResponse,
    viewModel: DoctorViewModel = viewModel()
) {
    LaunchedEffect(viewModel.allReminders) {
        viewModel.fetchReminders()
        viewModel.filterRemindersByContact(doctor.id_contact)
    }
    val isRefreshing = viewModel.isLoading

    var showDialog by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (!isGranted) {
            Toast.makeText(
                context,
                "Necesitamos permiso de notificaciones para usar esta función",
                Toast.LENGTH_LONG
            ).show()
        } else
            navController.navigate(AppScreens.AddMedicationScreen.route)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            topBar = {
                AppTopBar(
                    title = "Detalles del contacto",
                    onBackClick = { navController.popBackStack() }
                ) {}
            },
            floatingActionButton = {
                FloatingActionButton(R.drawable.calendar_solid_full) {
                    if (NotificationPermissionHelper.hasNotificationPermission(context))
                        navController.navigate(AppScreens.AddAppointmentScreen.createRoute(doctor.id_contact))
                    else
                        NotificationPermissionHelper
                            .requestNotificationPermission(notificationPermissionLauncher)
                }
            },
            snackbarHost = { SnackbarHost(snackbarHostState) },
            containerColor = BackgroundColor
        ) { paddingValues ->
            PullToRefreshBox(
                isRefreshing = isRefreshing,
                onRefresh = {
                    viewModel.fetchReminders()
                    viewModel.filterRemindersByContact(doctor.id_contact)
                },
                modifier = Modifier
                    .background(White)
                    .padding(paddingValues)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(start = 20.dp, top = 30.dp, end = 20.dp)
                ) {
                    DoctorHeader(navController, doctor)

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 12.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        TextButton(
                            onClick = { },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.textButtonColors(
                                containerColor = PompAndPower,
                                contentColor = White
                            ),
                            content = {
                                Text(
                                    text = "Editar",
                                    fontSize = 20.sp,
                                    fontFamily = StarsLove,
                                    fontWeight = FontWeight.ExtraLight,
                                    modifier = Modifier.offset(y = 4.dp)
                                )
                            }
                        )
                        TextButton(
                            onClick = { showDialog = true },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.textButtonColors(
                                containerColor = Color.Transparent,
                                contentColor = MaterialTheme.colorScheme.error
                            ),
                            border = BorderStroke(
                                width = 1.dp,
                                color = MaterialTheme.colorScheme.error
                            ),
                            content = {
                                Text(
                                    text = "Eliminar",
                                    fontSize = 20.sp,
                                    fontFamily = StarsLove,
                                    fontWeight = FontWeight.ExtraLight,
                                    modifier = Modifier.offset(y = 4.dp)
                                )
                            }
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        "Acerca del Doctor",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 18.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = doctor.about!!,
                        color = Color.Gray,
                        fontSize = 14.sp,
                        lineHeight = 20.sp,
                        textAlign = TextAlign.Justify
                    )

                    HorizontalDivider(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 24.dp, bottom = 12.dp),
                        color = AntiFlashWhite,
                        thickness = 2.dp
                    )

                    if (viewModel.filteredReminders.isEmpty() && !isRefreshing)
                        EmptyAppointmentState(
                            icon = R.drawable.calendar_xmark_solid_full,
                            title = "No hay citas próximas",
                            description = "Parece que no tienes citas agendadas con este doctor. ¡Agrega una nueva cita para mantener un seguimiento de tus consultas!"
                        )
                    else {
                        Text(
                            "Citas próximas",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 18.sp
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(
                                items = viewModel.filteredReminders,
                                key = { it.id_reminder }) { reminder ->
                                ScheduleCard(
                                    reminder = reminder,
                                    onDelete = { deletedReminder ->
                                        viewModel.filteredReminders -= deletedReminder
                                        scope.launch {
                                            val result = snackbarHostState.showSnackbar(
                                                message = "Cita eliminada",
                                                actionLabel = "Deshacer",
                                                duration = SnackbarDuration.Short
                                            )

                                            if (result == SnackbarResult.ActionPerformed) {
                                                viewModel.filteredReminders += deletedReminder

                                                viewModel.filteredReminders =
                                                    viewModel.filteredReminders.sortedBy { it.id_reminder }
                                            } else {
                                                viewModel.deleteContactReminder(
                                                    reminder.id_reminder,
                                                    reminder.id_contact!!,
                                                    navController
                                                )
                                            }
                                        }
                                    }
                                )
                            }

                            item { Spacer(modifier = Modifier.height(50.dp)) }
                        }
                    }
                }
            }
        }

        if (showDialog)
            AlertDialog(
                onDismissRequest = { showDialog = false },
                confirmButton = {
                    TextButton(
                        onClick = {
                            viewModel.deleteContact(doctor.id_contact, navController)
                            navController.popBackStack()
                            showDialog = false
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = White,
                            containerColor = Tekhelet
                        ),
                        content = { Text("Sí, eliminar") }
                    )
                },
                dismissButton = {
                    TextButton(
                        onClick = { showDialog = false },
                        content = { Text("Cancelar", color = SweetGrey) }
                    )
                },
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.trash_solid_full),
                            contentDescription = "Delete Icon",
                            modifier = Modifier.size(24.dp),
                        )
                        Text("¿Eliminar doctor?")
                    }
                },
                text = { Text("Esta acción no se puede deshacer.\nPerderás todos los datos relacionados con tu especialista ${doctor.name}.") },
                shape = RoundedCornerShape(12.dp),
                containerColor = White,
                titleContentColor = Tekhelet,
                textContentColor = SweetGrey
            )

        if (isRefreshing) {
            SplashScreen()
        }
    }
}
