package com.tenko.myst.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.tenko.myst.R
import com.tenko.myst.navigation.AppScreens
import com.tenko.myst.ui.theme.AntiFlashWhite
import com.tenko.myst.ui.theme.Tekhelet
import com.tenko.myst.ui.theme.White

@Composable
fun SuggestionsCard(navController: NavController) {
    /*val gradient = Brush.horizontalGradient(
        colors = listOf(
            Tekhelet, // morado
            AntiFlashWhite  // gris claro
        )
    )*/
    val gradient = Brush.linearGradient(
        colors = listOf(
            Tekhelet,
            AntiFlashWhite
        ),
        start = Offset(0f, 600f),
        end = Offset(800f, 0f)
    )

    Text(
        text = "Obten sugerencias de salud",
        fontSize = 20.sp,
        fontWeight = FontWeight.SemiBold,
        modifier = Modifier.padding(bottom = 8.dp)
    )

    Card(
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(6.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .background(gradient)
                .padding(top = 16.dp, start = 16.dp, end = 16.dp)
        ) {
            Row( verticalAlignment = Alignment.Bottom ) {
                Image(
                    contentDescription = "Tenko Avatar",
                    painter = painterResource(R.drawable.tenko_avatar),
                    modifier = Modifier.size(140.dp)
                )

//                Spacer(Modifier.width(12.dp))

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(bottom = 16.dp)
                ) {
                    Text(
                        text = "Algunas de tus preguntas pueden ser respondidas por tus especialistas, sin necesidad de una consulta formal.",
                        fontSize = 14.sp
                    )

                    Spacer(Modifier.height(12.dp))

                    Button(
                        onClick = { navController.navigate(AppScreens.DoctorsScreen.route) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = White
                        ),
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp)
                    ) {
                        Text(
                            text = "Contactar a un doctor",
                            color = Tekhelet,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }
    }
}