package com.tenko.app.ui.screen

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.tenko.app.R
import com.tenko.app.data.serializable.ContactResponse
import com.tenko.app.data.view.DoctorViewModel
import com.tenko.app.ui.components.AppTopBar
import com.tenko.app.ui.components.FloatingActionButton
import com.tenko.app.ui.theme.AntiFlashWhite
import com.tenko.app.ui.theme.BackgroundColor
import com.tenko.app.ui.theme.PompAndPower
import com.tenko.app.ui.theme.StarsLove
import com.tenko.app.ui.theme.SweetGrey
import com.tenko.app.ui.theme.Tekhelet
import com.tenko.app.ui.theme.White

@Composable
fun DoctorDetailsScreen(
    navController: NavController,
    doctor: ContactResponse
) {
    Scaffold(
        topBar = {
            AppTopBar(
                title = "Detalles del contacto",
                onBackClick = { navController.popBackStack() }
            )
        },
        floatingActionButton = {
            FloatingActionButton(R.drawable.calendar_solid_full) {

            }
        },
        bottomBar = {
            BookAppointmentButton(doctor.id_contact) {
                Toast.makeText(navController.context, "Contacto eliminado", Toast.LENGTH_SHORT)
                    .show()
                navController.popBackStack()
            }
        },
        containerColor = BackgroundColor
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(horizontal = 20.dp, vertical = 30.dp)
        ) {
            DoctorHeader(doctor, navController)

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

            Text(
                "Citas próximas",
                fontWeight = FontWeight.SemiBold,
                fontSize = 18.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn() {
                items(count = 3) {
                    ScheduleCard()
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }
    }
}

@Composable
fun DoctorHeader(
    doctor: ContactResponse,
    navController: NavController,
    viewModel: DoctorViewModel = viewModel(),
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        val avatar = when (doctor.genre) {
            0 -> R.drawable.doctor0
            1 -> R.drawable.doctor1
            2 -> R.drawable.doctor2
            3 -> R.drawable.doctor3
            4 -> R.drawable.doctor4
            else -> R.drawable.profile_picture_placeholder
        }
        var showDialog by remember { mutableStateOf(false) }

        Image(
            painter = painterResource(avatar),
            contentDescription = "Doctor Avatar",
            modifier = Modifier
                .size(150.dp)
                .background(
                    color = AntiFlashWhite,
                    shape = RoundedCornerShape(16.dp)
                ),
            contentScale = ContentScale.Crop
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 12.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = "${doctor.name!!} ${doctor.last_name!!}",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.SemiBold
                )

                Text(
                    text = doctor.specialty!!,
                    color = Color.Gray,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.Start)
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
                    border = BorderStroke(width = 1.dp, color = MaterialTheme.colorScheme.error),
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
    }
}

@Composable
fun ActionIcon(icon: Int, label: String) {
    Card(
        onClick = {},
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                tint = PompAndPower,
                contentDescription = label,
                painter = painterResource(icon),
                modifier = Modifier.size(28.dp)
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = label,
                fontSize = 12.sp,
                color = PompAndPower,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun ScheduleCard() {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFE6E6E6)),
//        elevation = CardDefaults.cardElevation(6.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .background(
                        color = White,
                        shape = RoundedCornerShape(8.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    tint = PompAndPower,
                    contentDescription = "Calendar Icon",
                    painter = painterResource(R.drawable.calendar_solid_full),
                    modifier = Modifier.size(45.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column {
                Text(text = "Consultations", fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
                Text(
                    text = "Sunday",
                    fontWeight = FontWeight.Medium,
                    color = Color.Gray,
                    fontSize = 13.sp
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            Text("9am - 11am", color = Color.Gray)
        }
    }
}

@Composable
fun BookAnAppointment() {
    FloatingActionButton(
        onClick = {
// Acción para el botón de chat
        },
        containerColor = PompAndPower,
        modifier = Modifier.width(IntrinsicSize.Max)
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                tint = White,
                contentDescription = "Agregar cita",
                painter = painterResource(R.drawable.calendar_plus_regular_full),
                modifier = Modifier.size(30.dp)
            )

            Text(
                text = "Agendar",
                fontSize = 14.sp,
                color = White,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun BookAppointmentButton(
    contactId: Int,
    viewModel: DoctorViewModel = viewModel(),
    onSuccess: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
        shadowElevation = 8.dp,
        color = BackgroundColor
    ) {

    }
}
