package com.tenko.app.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tenko.app.ui.theme.AntiFlashWhite
import com.tenko.app.ui.theme.PompAndPower
import com.tenko.app.ui.theme.StarsLove
import com.tenko.app.ui.theme.Tekhelet
import com.tenko.app.ui.theme.White

@Composable
fun BottomBar(onSend: (String) -> Unit, isNumeric: Boolean = false) {
    Surface(
        shape = RoundedCornerShape(
            topStart = 12.dp,
            topEnd = 12.dp
        ),
        shadowElevation = 8.dp
    ) {
        NavigationBar(
            modifier = Modifier.shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(
                    topStart = 12.dp,
                    topEnd = 12.dp
                ),
                ambientColor = Color.Black,
                spotColor = Color.Black,
                clip = false
            ),
            containerColor = White,
        ) {
            ChatInput(
                onSend = onSend,
                modifier = Modifier
                    .imePadding()
                    .padding(16.dp),
                isNumeric = isNumeric
            )
        }
    }
}

@Composable
fun BottomBar(
    text: String,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(
            topStart = 12.dp,
            topEnd = 12.dp
        ),
        shadowElevation = 8.dp
    ) {
        NavigationBar(
            modifier = Modifier.shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(
                    topStart = 12.dp,
                    topEnd = 12.dp
                ),
                ambientColor = Color.Black,
                spotColor = Color.Black,
                clip = false
            ),
            containerColor = White,
        ) {
            TextButton(
                onClick = { onClick() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(66.dp)
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.textButtonColors(
                    containerColor = PompAndPower,
                    contentColor = White
                ),
                content = {
                    Text(
                        text = text,
                        fontSize = 25.sp,
                        fontFamily = StarsLove,
                        fontWeight = FontWeight.ExtraLight,
                        modifier = Modifier.offset(y = 4.dp)
                    )
                }
            )
        }
    }
}

@Composable
fun BottomBar(
    onNextStep: () -> Unit,
    onPreviousStep: () -> Unit,
    currentStep: Int,
    totalSteps: Int
) {
    val progress = (currentStep + 1) / totalSteps.toFloat()
    /*for (i in 0 until totalSteps) {
        val dotColor = if (i <= currentStep) Tekhelet else AntiFlashWhite
        Text(
            text = "●",
            fontSize = 12.sp,
            color = dotColor
        )
    }*/

    Surface(
        shape = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp),
        shadowElevation = 8.dp,
    ) {
        NavigationBar(
            modifier = Modifier.shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp),
                clip = false,
                ambientColor = Color.Black,
                spotColor = Color.Black
            ),
            containerColor = White,
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp),
                    color = Tekhelet,
                    trackColor = AntiFlashWhite,
                    strokeCap = StrokeCap.Round,
                )

                Text(
                    text = "Paso ${currentStep + 1} de $totalSteps",
                    fontSize = 16.sp,
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally)
                ) {
                    for (i in 1..2) {
                        TextButton(
                            onClick = {
                                when (i) {
                                    1 -> onPreviousStep()
                                    2 -> onNextStep()
                                }
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.textButtonColors(
                                containerColor = Tekhelet,
                                contentColor = White
                            ),
                            content = {
                                Text(
                                    text = when (i) {
                                        1 -> if (currentStep > 0) "Atrás" else "Regresar"
                                        2 -> if (currentStep < totalSteps - 1) "Siguiente" else "Finalizar"
                                        else -> "TODO"
                                    },
                                    fontSize = 16.sp,
                                    modifier = Modifier.padding(vertical = 4.dp)
                                )
                            }
                        )
                    }
                }
            }
        }
    }
}