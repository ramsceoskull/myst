package com.tenko.app.ui.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tenko.app.ui.theme.RaisinBlack

@Composable
fun HeaderAddLabResults() {
    Spacer(modifier = Modifier.height(30.dp))

    Text(
        text = "Datos del estudio",
        color = RaisinBlack,
        fontSize = 20.sp,
        fontWeight = FontWeight.SemiBold
    )

    Spacer(modifier = Modifier.height(8.dp))

    Text(
        text = "Agrega los resultados de tu estudio de laboratorio. Puedes incluir múltiples variables y sus respectivos resultados.",
        fontSize = 14.sp,
        textAlign = TextAlign.Justify
    )
}