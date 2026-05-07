package com.tenko.app.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tenko.app.data.serializable.ContactResponse
import com.tenko.app.ui.theme.White

@Composable
fun DoctorCard(
    contact: ContactResponse,
    colors: List<Color>,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(110.dp)
            .padding(horizontal = 25.dp),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(8.dp),
        colors = CardDefaults.cardColors(containerColor = colors[0]),
        content = {
            Row(
                modifier = Modifier.fillMaxSize().padding(start = 20.dp, end = 15.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "${contact.name} ${contact.last_name ?: ""}",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = colors[1]
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = contact.specialty ?: "Sin especialidad",
                        fontSize = 14.sp,
                        color = colors[2]
                    )
                }

                Box(
                    modifier = Modifier
                        .padding(vertical = 12.dp)
                        .fillMaxHeight()
                        .width(1.dp)
                        .background(White.copy(0.5f))
                )

                Spacer(modifier = Modifier.width(15.dp))

                contact.genre?.let {
                    /*Image(
                        painter = painterResource(id = it),
                        contentDescription = "Doctor Avatar",
                        modifier = Modifier.fillMaxHeight().padding(top = 12.dp),
                        contentScale = ContentScale.FillHeight
                    )*/
                }
            }
        }
    )
}

/*@Composable
fun DoctorCard(
    icon: Int?,
    details: List<String?>,
    colors: List<Color>
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(8.dp),
        colors = CardDefaults.cardColors(containerColor = colors[0]),
        content = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(110.dp)
                    .padding(horizontal = 20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "${details[0]} ${details[1] ?: "Nombres sin proporcionar"}",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = colors[1]
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = details[2] ?: "Sin especialidad",
                        fontSize = 14.sp,
                        color = colors[2]
                    )
                }

                Box(
                    modifier = Modifier
                        .padding(top = 12.dp)
                        .fillMaxHeight()
                        .width(1.dp)
                        .background(White.copy(0.5f))
                )

                Spacer(modifier = Modifier.width(15.dp))

                icon?.let {
                    Image(
                        painter = painterResource(id = it),
                        contentDescription = "Doctor Avatar",
                        modifier = Modifier.fillMaxHeight().padding(top = 12.dp),
                        contentScale = ContentScale.FillHeight
                    )
                }
            }
        }
    )
}*/

@Composable
fun DoctorCard(
    icon: Int?,
    details: List<String?>,
    colors: List<Color>
) {
    var flipped by remember { mutableStateOf(false) }

    val rotation by animateFloatAsState(
        targetValue = if (flipped) 180f else 0f,
        animationSpec = tween(
            durationMillis = 700,
            easing = FastOutSlowInEasing
        ),
        label = "card_rotation"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .graphicsLayer {
                rotationY = rotation
                cameraDistance = 12f * density
            }
            .clickable { flipped = !flipped },
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(8.dp),
        colors = CardDefaults.cardColors(containerColor = colors[0])
    ) {

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(110.dp)
        ) {

            // FRONT
            if (rotation <= 90f) {

                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Column(modifier = Modifier.weight(1f)) {

                        Text(
                            text = "${details[0]} ${details[1] ?: "Nombres sin proporcionar"}",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = colors[1]
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = details[2] ?: "Sin especialidad",
                            fontSize = 14.sp,
                            color = colors[2]
                        )
                    }

                    Box(
                        modifier = Modifier
                            .padding(top = 12.dp)
                            .fillMaxHeight()
                            .width(1.dp)
                            .background(Color.White.copy(alpha = 0.5f))
                    )

                    Spacer(modifier = Modifier.width(15.dp))

                    icon?.let {
                        Image(
                            painter = painterResource(id = it),
                            contentDescription = "Doctor Avatar",
                            modifier = Modifier
                                .fillMaxHeight()
                                .padding(top = 12.dp),
                            contentScale = ContentScale.FillHeight
                        )
                    }
                }
            }

            // BACK
            else {

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer {
                            rotationY = 180f
                        }
                        .padding(20.dp),
                    contentAlignment = Alignment.Center
                ) {

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {

                        Text(
                            text = "Información adicional",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = colors[1]
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Aquí puedes mostrar horarios, experiencia, teléfono, etc.",
                            fontSize = 14.sp,
                            color = colors[2],
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}

/*items(count = doctorsList.size) { index ->
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
            }*/