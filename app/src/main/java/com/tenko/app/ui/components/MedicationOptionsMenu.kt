package com.tenko.app.ui.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tenko.app.R
import com.tenko.app.data.serializable.ReminderResponse
import com.tenko.app.data.view.MedicineViewModel
import com.tenko.app.ui.theme.White

@Composable
fun MedicationOptionsMenu(
    medicine: ReminderResponse,
    onDelete: () -> Unit,
    viewModel: MedicineViewModel = viewModel()
) {
    var expanded by remember { mutableStateOf(false) }
    val rotation by animateFloatAsState(
        targetValue = if (expanded) 90f else 0f,
        animationSpec = spring(
            stiffness = Spring.StiffnessMediumLow,
            dampingRatio = Spring.DampingRatioMediumBouncy
        ),
        label = "rotationAnim"
    )

    val haptic = LocalHapticFeedback.current

    Box {
        IconButton(onClick = { expanded = true }) {
            Icon(
                painter = painterResource(R.drawable.ellipsis_vertical_solid_full),
                contentDescription = "More Options",
                tint = Color.Gray,
                modifier = Modifier
                    .size(26.dp)
                    .rotate(rotation)
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.background(White),
            content = {
                DropdownMenuItem(
                    text = { Text(text = "Editar") },
                    leadingIcon = {
                        Icon(
                            painter = painterResource(R.drawable.pen_fancy_solid_full),
                            contentDescription = "Edit Icon",
                            tint = Color.Gray,
                            modifier = Modifier.size(18.dp)
                        )
                    },
                    onClick = {
                        expanded = false
//                        onEdit(medicine)
                    }
                )

                /*DropdownMenuItem(
                    text = { Text("Detalles") },
                    leadingIcon = {
                        Icon(
                            painter = painterResource(R.drawable.info_solid_full),
                            contentDescription = "Info Icon",
                            tint = Color.Gray,
                            modifier = Modifier.size(18.dp)
                        )
                    },
                    onClick = {
                        expanded = false
                        onInfo(medicine)
                    }
                )*/

                HorizontalDivider(modifier = Modifier.padding(horizontal = 12.dp))

                DropdownMenuItem(
                    text = {
                        Text(
                            text = "Eliminar",
                            color = MaterialTheme.colorScheme.error,
                            fontWeight = FontWeight.SemiBold
                        )
                    },
                    leadingIcon = {
                        Icon(
                            painter = painterResource(R.drawable.trash_can_regular_full),
                            contentDescription = "Delete Icon",
                            modifier = Modifier.size(18.dp),
                            tint = MaterialTheme.colorScheme.error
                        )
                    },
                    onClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        onDelete()
                        expanded = false
                    }
                )
            }
        )
    }
}
