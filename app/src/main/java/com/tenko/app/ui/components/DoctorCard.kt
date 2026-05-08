package com.tenko.app.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tenko.app.R
import com.tenko.app.data.serializable.ContactResponse
import com.tenko.app.ui.theme.Tekhelet
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
                    val avatar = when(it) {
                        0 -> R.drawable.doctor0
                        1 -> R.drawable.doctor1
                        2 -> R.drawable.doctor2
                        3 -> R.drawable.doctor3
                        4 -> R.drawable.doctor4
                        else -> R.drawable.profile_picture_placeholder
                    }
                    Image(
                        painter = painterResource(id = avatar),
                        contentDescription = "Doctor Avatar",
                        modifier = Modifier.fillMaxHeight().padding(top = 12.dp),
                        contentScale = ContentScale.FillHeight
                    )
                }
            }
        }
    )
}

@Composable
fun DoctorCard(
    icon: Int?,
    doctorDetails: List<String?>,
    clinicDetails: List<String?>,
    colors: List<Color>
) {
    var flipped by remember { mutableStateOf(false) }

    val rotation by animateFloatAsState(
        targetValue = if (flipped) 180f else 0f,
        animationSpec = tween(
            durationMillis = 900,
            easing = FastOutSlowInEasing
        ),
        label = "rotation_animation"
    )
    val scale by animateFloatAsState(
        targetValue = if (flipped) 1.04f else 1f,
        animationSpec = tween(900),
        label = "scale_animation"
    )
    val elevation by animateDpAsState(
        targetValue = if (flipped) 18.dp else 8.dp,
        animationSpec = tween(900),
        label = "elevation_animation"
    )

    /*val fullText = if (flipped) "Presiona nuevamente para regresar" else "Toca la tarjeta para ver más información"
    var animatedText by remember(fullText) { mutableStateOf("") }
    LaunchedEffect(fullText) {
        animatedText = ""
        fullText.forEachIndexed { index, _ ->
            delay(25)
            animatedText = fullText.take(index + 1)
        }
    }
    Text(
        text = animatedText,
        modifier = Modifier.fillMaxWidth(),
        textAlign = TextAlign.Center,
        fontSize = 14.sp,
        color = Color.Gray
    )*/

    Column {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .scale(scale)
                .graphicsLayer {
                    rotationY = rotation
//                    PROFUNDIDAD 3D
                    cameraDistance = 28f * density
                }
                .clickable { flipped = !flipped }
            ,
            shape = RoundedCornerShape(12.dp),
            elevation = CardDefaults.cardElevation(elevation),
            colors = CardDefaults.cardColors(containerColor = colors[0])
        ) {
            Box(modifier = Modifier.fillMaxWidth().height(150.dp)) {
                // FRONTAL
                if (rotation <= 90f) {
                    Row(
                        modifier = Modifier.fillMaxSize().padding(start = 20.dp, end = 15.dp),
                        verticalAlignment = Alignment.Bottom
                    ) {
                        Column(
                            modifier = Modifier.fillMaxHeight().weight(1f),
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = doctorDetails[0] ?: "Nombres sin proporcionar",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = colors[1]
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = doctorDetails[1] ?: "Sin especialidad",
                                fontSize = 14.sp,
                                color = colors[2]
                            )
                        }

                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .width(1.dp)
                                .padding(vertical = 12.dp)
                                .background(Color.White.copy(alpha = 0.5f))
                        )

                        Spacer(modifier = Modifier.width(15.dp))

                        icon?.let {
                            Image(
                                painter = painterResource(id = it),
                                contentDescription = "Doctor Avatar",
                                modifier = Modifier.padding(top = 35.dp),
                                contentScale = ContentScale.FillHeight
                            )
                        }
                    }
                } else {
                    // TRASERA
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .graphicsLayer { rotationY = 180f }
                            .padding(horizontal = 20.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = clinicDetails[0] ?: "Sin nombre de clínica",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = colors[1]
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = clinicDetails[1] ?: "Sin teléfono de clínica",
                                fontSize = 14.sp,
                                color = colors[2]
                            )
                        }

                        Box(
                            modifier = Modifier
                                .padding(vertical = 12.dp)
                                .fillMaxHeight()
                                .width(1.dp)
                                .background(Color.White.copy(alpha = 0.5f))
                        )

                        Spacer(modifier = Modifier.width(15.dp))

                        Box(
                            modifier = Modifier
                                .clip(CircleShape)
                                .size(90.dp)
                                .background(colors[2]),
                            contentAlignment = Alignment.Center,
                            content = {
                                Icon(
                                    painter = painterResource(id = R.drawable.hospital_regular_full),
                                    contentDescription = "Hospital Icon",
                                    tint = colors[0],
                                    modifier = Modifier.size(40.dp)
                                )
                            }
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "*Presiona la tarjeta para ver más información",
            modifier = Modifier.fillMaxWidth(),
            color = Color.LightGray,
            fontSize = 12.sp,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(12.dp))

        HorizontalDivider(color = Tekhelet.copy(0.6f), thickness = 2.dp)

        Spacer(modifier = Modifier.height(24.dp))

        AnimatedContent(
            targetState = flipped,
            transitionSpec = {
                fadeIn(animationSpec = tween(900)
                ) + slideInVertically(
                    initialOffsetY = { it / 2 }
                ) togetherWith fadeOut(
                    animationSpec = tween(900)
                ) + slideOutVertically(
                    targetOffsetY = { -it / 2 }
                )
            },
            label = "bottom_text_animation"
        ) { isFlipped ->
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                if(!isFlipped) {
                    Text(
                        text = "Contacto disponible en...",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                    Text(
                        text = clinicDetails[2] ?: "Sin contacto de clínica",
                        fontSize = 12.sp,
                        color = Color.LightGray,
                        modifier = Modifier.padding(start = 16.dp)
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Acerca de su especialidad...",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                    Text(
                        text = doctorDetails[2] ?: "Sin descripción de especialidad",
                        fontSize = 12.sp,
                        color = Color.LightGray,
                        modifier = Modifier.padding(start = 16.dp)
                    )
                } else {
                    Text(
                        text = "Clínica ubicada en...",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                    Text(
                        text = clinicDetails[3] ?: "Sin dirección de clínica",
                        fontSize = 12.sp,
                        color = Color.LightGray,
                        modifier = Modifier.padding(start = 16.dp)
                    )
                }
            }
        }
    }
}