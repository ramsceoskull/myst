package com.tenko.app.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.unit.dp
import com.tenko.app.ui.theme.AntiFlashWhite
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
    onNextStep: () -> Unit,
    onPreviousStep: () -> Unit,
    currentStep: Int,
    totalSteps: Int
) {
    val progress = (currentStep + 1) / totalSteps.toFloat()

    Surface(
        shape = RoundedCornerShape(
            topStart = 12.dp,
            topEnd = 12.dp
        ),
        shadowElevation = 8.dp,
    ) {
        NavigationBar(
            modifier = Modifier.shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp),
                ambientColor = Color.Black,
                spotColor = Color.Black,
                clip = false
            ),
            containerColor = White,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp)
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
                Text("Paso ${currentStep + 1} de $totalSteps")

                Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)) {
                    Button(
                        onClick = { onPreviousStep() },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Tekhelet,
                            contentColor = White
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f).padding(vertical = 8.dp),
                        content = {
                            Text(if(currentStep > 0) "Atrás" else "Regresar")
                        }
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Button(
                        onClick = { onNextStep() },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Tekhelet,
                            contentColor = White
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f).padding(vertical = 8.dp),
                        content = {
                            Text(if(currentStep < totalSteps - 1) "Siguiente" else "Finalizar")
                        }
                    )
                }
            }
        }
    }
}