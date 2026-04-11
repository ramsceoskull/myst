package com.tenko.myst.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.tenko.myst.R
import com.tenko.myst.data.model.Doctor
import com.tenko.myst.ui.components.AppTopBar
import com.tenko.myst.ui.theme.AntiFlashWhite
import com.tenko.myst.ui.theme.PompAndPower
import com.tenko.myst.ui.theme.SweetGrey
import com.tenko.myst.ui.theme.White

@Composable
fun DoctorDetailsScreen(navController: NavHostController, doctor: Doctor) {
    Scaffold(
        topBar = {
            AppTopBar(
                title = "Detalles del contacto",
                onBackClick = { navController.popBackStack() }
            )
        },
        floatingActionButton = { BookAnAppointment() },
//        bottomBar = { BookAppointmentButton() },
        containerColor = White
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(16.dp)
                .padding(padding)
        ) {
            DoctorHeader(doctor)

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                "Acerca del Doctor",
                fontWeight = FontWeight.SemiBold,
                fontSize = 18.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = doctor.about,
                color = Color.Gray,
                fontSize = 14.sp,
                lineHeight = 20.sp,
                textAlign = TextAlign.Justify
            )

            HorizontalDivider(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 24.dp, bottom = 12.dp),
                color = Color(0xFFE0E0E0),
                thickness = 2.dp
            )

            Text(
                "Citas proximas",
                fontWeight = FontWeight.SemiBold,
                fontSize = 18.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn() {
                items(count = 7) {
                    ScheduleCard()
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }
    }
}

@Composable
fun DoctorHeader(doctor: Doctor) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(doctor.imageRes),
            contentDescription = "Doctor Image",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(120.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(
                    color = AntiFlashWhite,
                    shape = RoundedCornerShape(16.dp)
                )
        )

        Spacer(modifier = Modifier.width(16.dp))

        Column {
            Text(
                text = doctor.name,
                fontSize = 22.sp,
                fontWeight = FontWeight.SemiBold
            )

            Text(
                text = doctor.subtitle,
                color = SweetGrey,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                ActionIcon(R.drawable.comment_solid_full, "Mensaje")
                ActionIcon(R.drawable.phone_solid_full, "Llamada")
                ActionIcon(R.drawable.video_solid_full, "Videollamada")
            }
        }
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
            Box (
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
                Text(text = "Sunday", fontWeight = FontWeight.Medium, color = Color.Gray, fontSize = 13.sp)
            }

            Spacer(modifier = Modifier.weight(1f))

            Text("9am - 11am", color = Color.Gray)
        }
    }
}

@Composable
fun BookAnAppointment() {
    FloatingActionButton(
        onClick = { /* Acción para el botón de chat */ },
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
fun BookAppointmentButton() {
    Box(
        modifier = Modifier
            .padding(16.dp)
            .height(100.dp)
    ) {
        Button(
            onClick = { },
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = PompAndPower
            ),
            modifier = Modifier.fillMaxWidth().height(80.dp)
        ) {
            Icon(
                tint = White,
                contentDescription = "Book an appointment icon",
                painter = painterResource(R.drawable.calendar_plus_regular_full),
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "Registrar cita", color = White)
        }
    }
}