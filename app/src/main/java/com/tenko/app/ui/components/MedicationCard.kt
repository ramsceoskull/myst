package com.tenko.app.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tenko.app.R
import com.tenko.app.data.model.MedicineStatus
import com.tenko.app.data.serializable.ReminderResponse
import com.tenko.app.ui.theme.MedCardBg
import com.tenko.app.ui.theme.MedPending
import com.tenko.app.ui.theme.MedPrimary
import com.tenko.app.ui.theme.MedSkipped
import com.tenko.app.ui.theme.MedTaken
import com.tenko.app.ui.theme.RaisinBlack
import com.tenko.app.ui.theme.White
import java.time.format.DateTimeFormatter

@Composable
fun MedicationCard(
    medicine: ReminderResponse,
    onTaken: () -> Unit,
    onSkipped: () -> Unit,
    onDelete: () -> Unit
) {
    val statusColor = when (medicine.status) {
        MedicineStatus.TAKEN.ordinal -> MedTaken
        MedicineStatus.SKIPPED.ordinal -> MedSkipped
        MedicineStatus.PENDING.ordinal -> MedPending
        else -> {
            MedPrimary
        }
    }

    Card(
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(2.dp),
        colors = CardDefaults.cardColors(
            containerColor = MedCardBg,
            contentColor = Color.Gray
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
//            HEADER
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .background(color = White, shape = CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(R.drawable.red_and_white_pill),
                        contentDescription = "Medicine Icon",
                        modifier = Modifier.size(26.dp),
                        tint = Color.Unspecified
                    )
                }

                Spacer(Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = medicine.title,
                        color = RaisinBlack,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1
                    )

                    Text(
                        text = medicine.dosage ?: "Dosis no especificada",
                        color = Color.Gray,
                        fontSize = 15.sp
                    )
                }

                MedicationOptionsMenu(medicine, onDelete)
            }

//            INFO
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    painter = painterResource(R.drawable.clock_regular_full),
                    contentDescription = "Time Icon",
                    modifier = Modifier.size(20.dp),
                )
                Text(
                    text = medicine.day_time?.format(DateTimeFormatter.ofPattern("HH:mm"))
                        ?: "Hora no especificada",
                    fontSize = 13.sp
                )

                Spacer(Modifier.width(16.dp))

                Icon(
                    painter = painterResource(R.drawable.utensils_solid_full),
                    contentDescription = "Meal Icon",
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = if (medicine.after_meal == true) "Después de comer" else "Antes de comer",
                    fontSize = 13.sp
                )
            }

//            BUTTONS
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (medicine.status == MedicineStatus.PENDING.ordinal) {
                    OutlinedButton(
                        onClick = onSkipped,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(width = 1.dp, color = Color.LightGray),
                        content = {
                            Text(text = "Saltar")
                        }
                    )
                } else
                    Spacer(modifier = Modifier.weight(1f))

                Button(
                    onClick = onTaken,
                    modifier = Modifier.weight(1f),
                    enabled = medicine.status == MedicineStatus.PENDING.ordinal,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = statusColor,
                        contentColor = RaisinBlack,
                        disabledContainerColor = statusColor,
                        disabledContentColor = RaisinBlack,
                    ),
                    content = {
                        Text(
                            text = when (medicine.status) {
                                MedicineStatus.TAKEN.ordinal -> "Tomada"
                                MedicineStatus.SKIPPED.ordinal -> "Saltada"
                                MedicineStatus.PENDING.ordinal -> "Tomar"
                                else -> "Estado"
                            }
                        )
                    }
                )
            }
        }
    }
}