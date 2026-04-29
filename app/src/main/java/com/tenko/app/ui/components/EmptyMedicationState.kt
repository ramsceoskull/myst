package com.tenko.app.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tenko.app.R
import com.tenko.app.data.model.MedicineStatus
import com.tenko.app.ui.theme.PompAndPower

@Composable
fun EmptyMedicationState(filter: MedicineStatus) {
    val (title, description, icon) = when (filter) {
        MedicineStatus.ALL -> Triple(
            "Sin medicamentos",
            "Agrega tu primer medicamento para comenzar",
            R.drawable.realistic_medicine
        )
        MedicineStatus.PENDING -> Triple(
            "Nada pendiente 🎉",
            "No tienes medicamentos por tomar",
            R.drawable.circle_check_regular_full
        )
        MedicineStatus.TAKEN -> Triple(
            "Aún no has tomado medicamentos",
            "Los medicamentos tomados aparecerán aquí",
            R.drawable.hand_holding_medical_solid_full
        )
        MedicineStatus.SKIPPED -> Triple(
            "Nada omitido 👍",
            "No has saltado ningún medicamento",
            R.drawable.forward_solid_full
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            painter = painterResource(icon),
            contentDescription = null,
            modifier = Modifier.size(72.dp),
            tint = if(icon != R.drawable.realistic_medicine) PompAndPower.copy(alpha = 0.6f) else Color.Unspecified
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = title,
            fontSize = 20.sp,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = description,
            color = Color.Gray,
            textAlign = TextAlign.Center
        )
    }
}