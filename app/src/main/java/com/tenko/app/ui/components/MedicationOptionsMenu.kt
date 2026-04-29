package com.tenko.app.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.tenko.app.R
import com.tenko.app.data.model.Medicine
import com.tenko.app.ui.theme.White

@Composable
fun MedicationOptionsMenu(
    medicine: Medicine,
    onInfo: (Medicine) -> Unit,
    onEdit: (Medicine) -> Unit,
    onDelete: (Medicine) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val rotation by animateFloatAsState(
        targetValue = if( expanded ) 90f else 0f,
        label = "rotationAnim"
    )

    val haptic = LocalHapticFeedback.current

    Box {
        IconButton( onClick = { expanded = true } ) {
            Icon(
                painter = painterResource(R.drawable.ellipsis_vertical_solid_full),
                contentDescription = "More Options",
                tint = Color.Gray,
                modifier = Modifier.size(26.dp).rotate(rotation)
            )
        }

        DropdownMenu(
            modifier = Modifier
                .background(White)
                .align(Alignment.TopEnd),
            expanded = expanded,
            onDismissRequest = { expanded = false },
            offset = DpOffset(x = (-4).dp, y = 4.dp),
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
                        onEdit(medicine)
                    }
                )

                DropdownMenuItem(
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
                )

                HorizontalDivider()

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
                            painter = painterResource(R.drawable.trash_can_solid_full),
                            contentDescription = "Delete Icon",
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(18.dp)
                        )
                    },
                    onClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        expanded = false
                        onDelete(medicine)
                    }
                )
            }
        )
    }
}