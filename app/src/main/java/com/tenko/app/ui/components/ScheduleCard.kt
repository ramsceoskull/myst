package com.tenko.app.ui.components

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement.Absolute.spacedBy
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tenko.app.R
import com.tenko.app.data.serializable.ReminderResponse
import com.tenko.app.ui.theme.AntiFlashWhite
import com.tenko.app.ui.theme.PompAndPower
import com.tenko.app.ui.theme.White

@Composable
fun ScheduleCard(reminder: ReminderResponse, onDelete: (ReminderResponse) -> Unit) {
    val context = LocalContext.current

    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = { value ->
            if (value != SwipeToDismissBoxValue.EndToStart) false
            else {
                onDelete(reminder)
                true
            }
        }
    )

    SwipeToDismissBox(
        state = dismissState,
        enableDismissFromStartToEnd = false,
        enableDismissFromEndToStart = true,
        backgroundContent = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(18.dp))
                    .background(Color.Red)
                    .padding(horizontal = 20.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                Icon(
                    painter = painterResource(R.drawable.trash_solid_full),
                    contentDescription = "Eliminar",
                    tint = Color.White,
                    modifier = Modifier.size(30.dp)
                )
            }
        },
        content = {
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = AntiFlashWhite,
                    contentColor = Color.Gray
                ),
                elevation = CardDefaults.cardElevation(4.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(IntrinsicSize.Min)
                    .clickable {
                        if (reminder.description!!.isNotEmpty()) {
                            Toast.makeText(
                                context,
                                "Notas para tu cita:\n${reminder.description}",
                                Toast.LENGTH_LONG
                            ).show()
                        } else {
                            Toast.makeText(
                                context,
                                "No hay notas adicionales para esta cita",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = spacedBy(16.dp, Alignment.Start)
                ) {
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .background(White, RoundedCornerShape(8.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.calendar_week_solid_full),
                            contentDescription = "Calendar Icon",
                            modifier = Modifier.size(45.dp),
                            tint = PompAndPower
                        )
                    }

                    Column {
                        Text(
                            text = reminder.title,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Row(horizontalArrangement = spacedBy(20.dp)) {
                            Text(
                                text = reminder.start_date.toString().let {
                                    val parts = it.split("-")
                                    if (parts.size == 3) {
                                        "${parts[2]}/${parts[1]}/${parts[0]}"
                                    } else it
                                },
                                color = Color.Gray,
                                fontSize = 13.sp
                            )
                            Text(
                                reminder.day_time.toString().let {
                                    val parts = it.split(":")
                                    if (parts.size >= 2) {
                                        "${if (parts[0].toInt() - 12 >= 0) parts[0].toInt() - 12 else parts[0]}:${parts[1]} ${if (parts[0].toInt() >= 12) "PM" else "AM"}"
                                    } else it
                                },
                                color = Color.Gray,
                                fontSize = 13.sp
                            )
                        }
                    }
                }
            }
        }
    )
}