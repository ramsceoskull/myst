package com.tenko.app.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement.spacedBy
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import com.tenko.app.R
import com.tenko.app.navigation.AppScreens
import com.tenko.app.ui.theme.AntiFlashWhite
import com.tenko.app.ui.theme.Tekhelet
import com.tenko.app.ui.theme.White

@Composable
fun SuggestionsCard(navController: NavController) {
    val gradient = Brush.linearGradient(
        colors = listOf(Tekhelet, AntiFlashWhite),
        start = Offset(0f, 600f),
        end = Offset(800f, 0f)
    )

    Text(
        text = "Obtén sugerencias de salud",
        fontSize = 20.sp,
        fontWeight = FontWeight.SemiBold
    )

    Spacer(modifier = Modifier.height(8.dp))

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(6.dp),
    ) {
        Row(
            modifier = Modifier
                .background(gradient)
                .padding(start = 16.dp, top = 16.dp, end = 16.dp),
            verticalAlignment = Alignment.Bottom
        ) {
            Image(
                painter = painterResource(R.drawable.tenko_avatar),
                contentDescription = "Tenko Avatar",
                modifier = Modifier.size(140.dp)
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(bottom = 16.dp),
                verticalArrangement = spacedBy(12.dp)
            ) {
                Text(
                    text = "Algunas de tus preguntas pueden ser respondidas por tus especialistas, sin necesidad de una consulta formal.",
                    fontSize = 14.sp,
                )

                TextButton(
                    onClick = { navController.navigate(AppScreens.DoctorsScreen.route) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp),
                    shape = RoundedCornerShape(6.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = White,
                        contentColor = Tekhelet
                    ),
                    content = {
                        Text(
                            text = "Contactar a un doctor",
                            fontSize = 12.sp
                        )
                    }
                )
            }
        }
    }
}