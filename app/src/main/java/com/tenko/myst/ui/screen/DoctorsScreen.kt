package com.tenko.myst.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.tenko.myst.R
import com.tenko.myst.data.model.Doctor
import com.tenko.myst.ui.components.AddContactButton
import com.tenko.myst.ui.components.AppTopBar
import com.tenko.myst.ui.components.BottomNavigationBar
import com.tenko.myst.ui.components.DoctorCard
import com.tenko.myst.ui.theme.AntiFlashWhite
import com.tenko.myst.ui.theme.CardDark
import com.tenko.myst.ui.theme.CardGray
import com.tenko.myst.ui.theme.CardPurple
import com.tenko.myst.ui.theme.RaisinBlack
import com.tenko.myst.ui.theme.Tekhelet
import com.tenko.myst.ui.theme.White

val doctorsList = mutableListOf<Doctor>(
    Doctor(
        id = 0,
        imageRes = R.drawable.doctor0,
        name = "Dr. Fillmore",
        subtitle = "MMBS, Lorem Ipsum",
        about = "Lorem ipsum dolor sit amet...",
        email = "ramsesrame21@gmail.com",
        phoneNumber = 3333943613
    ),
    Doctor(
        id = 1,
        imageRes = R.drawable.doctor1,
        name = "Dr. Maria Lexa",
        subtitle = "MMBS, Lorem Ipsum",
        about = "Lorem ipsum dolor sit amet...",
        email = "ramsesrame21@gmail.com",
        phoneNumber = 3333943613
    ),
    Doctor(
        id = 2,
        imageRes = R.drawable.doctor2,
        name = "Dr. Pullen",
        subtitle = "MMBS, Lorem Ipsum",
        about = "Lorem ipsum dolor sit amet...",
        email = "ramsesrame21@gmail.com",
        phoneNumber = 3333943613
    ),
    Doctor(
        id = 3,
        imageRes = R.drawable.doctor3,
        name = "Dr. Rodrigo",
        subtitle = "MMBS, Lorem Ipsum",
        about = "Lorem ipsum dolor sit amet...",
        email = "ramsesrame21@gmail.com",
        phoneNumber = 3333943613
    ),
    Doctor(
        id = 4,
        imageRes = R.drawable.doctor4,
        name = "Dr. Random",
        subtitle = "MMBS, Lorem Ipsum",
        about = "Lorem ipsum dolor sit amet...",
        email = "ramsesrame21@gmail.com",
        phoneNumber = 3333943613
    ),
)

@Composable
fun DoctorsScreen(navController: NavHostController) {
    Scaffold(
        topBar = {
            AppTopBar(
                title = "Lista de Doctores",
                onBackClick = { navController.popBackStack() },
            )
        },
        floatingActionButton = { AddContactButton() },
        bottomBar = { BottomNavigationBar(navController) },
        containerColor = White
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .padding(horizontal = 16.dp)
                .padding(top = 30.dp, bottom = 30.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            items(count = doctorsList.size) { index ->
                val doctor = doctorsList[index]

                DoctorCard(
                    imageRes = doctor.imageRes,
                    name = doctor.name,
                    subtitle = doctor.subtitle,
                    colors = when (index % 5) {
                        0 -> listOf(CardGray, RaisinBlack, Color.Gray)
                        1 -> listOf(CardPurple, White, AntiFlashWhite)
                        2 -> listOf(Tekhelet, White, AntiFlashWhite)
                        3 -> listOf(CardDark, White, AntiFlashWhite)
                        else -> listOf(RaisinBlack, White, AntiFlashWhite)
                    },
                    onClick = {
                        navController.navigate("doctor_details_screen/${doctor.id}")
                    }
                )
            }
        }
    }
}