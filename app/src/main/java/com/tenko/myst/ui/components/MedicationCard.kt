package com.tenko.myst.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tenko.myst.R
import com.tenko.myst.data.model.Medicine
import com.tenko.myst.data.model.MedicineStatus
import com.tenko.myst.ui.theme.MedCardBg
import com.tenko.myst.ui.theme.MedPending
import com.tenko.myst.ui.theme.MedPrimary
import com.tenko.myst.ui.theme.MedSkipped
import com.tenko.myst.ui.theme.MedTaken
import com.tenko.myst.ui.theme.White

@Composable
fun MedicationCard(
    medicine: Medicine,
    onTaken: () -> Unit,
    onSkipped: () -> Unit,
    onEdit: (Medicine) -> Unit,
    onInfo: (Medicine) -> Unit,
    onDelete: (Medicine) -> Unit
) {
    val statusColor = when (medicine.status) {
        MedicineStatus.ALL -> MedPrimary
        MedicineStatus.TAKEN -> MedTaken
        MedicineStatus.SKIPPED -> MedSkipped
        MedicineStatus.PENDING -> MedPending
    }

    Card(
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(3.dp),
        colors = CardDefaults.cardColors(
            containerColor = MedCardBg
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column( modifier = Modifier.padding(16.dp) ) {
//            HEADER
            Row( verticalAlignment = Alignment.CenterVertically ) {
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .background(color = White, shape = CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        tint = null,
                        contentDescription = "Medicine Icon",
                        painter = painterResource(R.drawable.red_and_white_pill),
                        modifier = Modifier.size(26.dp)
                    )
                }

                Spacer(Modifier.width(12.dp))

                Column( modifier = Modifier.weight(1f) ) {
                    Text(
                        text = medicine.name,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold
                    )

                    Text(
                        text = medicine.dosage + " " + medicine.unit,
                        color = Color.Gray,
                        fontSize = 14.sp
                    )
                }

                MedicationOptionsMenu(medicine, onInfo, onEdit, onDelete)
            }

            Spacer(Modifier.height(12.dp))

//            INFO
            Row( verticalAlignment = Alignment.CenterVertically ) {
                Icon(
                    painter = painterResource(R.drawable.clock_regular_full),
                    contentDescription = "Time Icon",
                    tint = Color.Gray,
                    modifier = Modifier.size(18.dp)
                )

                Spacer(Modifier.width(4.dp))

                Text(
                    text = medicine.time + " " + medicine.timeFormat,
                    color = Color.Gray,
                    fontSize = 13.sp
                )

                Spacer(Modifier.width(16.dp))

                Icon(
                    painter = painterResource(R.drawable.utensils_solid_full),
                    contentDescription = "Meal Icon",
                    tint = Color.Gray,
                    modifier = Modifier.size(18.dp)
                )

                Spacer(Modifier.width(4.dp))

                Text(
                    text = if (medicine.afterMeal) "Después de comer" else "Antes de comer",
                    color = Color.Gray,
                    fontSize = 13.sp
                )
            }

            Spacer(Modifier.height(16.dp))

//            BUTTONS
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if(medicine.status == MedicineStatus.PENDING) {
                    OutlinedButton(
                        onClick = onSkipped,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(50),
                        border = BorderStroke(
                            width = 1.dp,
                            color = Color.LightGray
                        ),
                        content = {
                            Text(text = "Saltar")
                        }
                    )
                } else
                    Spacer(modifier = Modifier.weight(1f))

                Button(
                    onClick = onTaken,
                    enabled = medicine.status == MedicineStatus.PENDING,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(50),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = statusColor,
                        contentColor = Color.Black,
                        disabledContainerColor = statusColor,
                        disabledContentColor = Color.Black,
                    ),
                    content = {
                        Text(
                            text = when (medicine.status) {
                                MedicineStatus.ALL -> "Estado"
                                MedicineStatus.TAKEN -> "Tomada"
                                MedicineStatus.SKIPPED -> "Saltada"
                                MedicineStatus.PENDING -> "Tomar"
                            }
                        )
                    }
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewMed() {
    MedicationCard(
        medicine = Medicine(
            id = 0,
            name = "Amoxicillin",
            dosage = "20",
            unit = "mg",
            time = "8:30",
            timeFormat = "am",
            afterMeal = true,
            status = MedicineStatus.PENDING,
        ),
        onTaken = { },
        onSkipped = { },
        onInfo = { },
        onEdit = { },
        onDelete = { }
    )
}