package com.tenko.app.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tenko.app.R
import com.tenko.app.data.model.CalendarLegend
import com.tenko.app.ui.theme.AntiFlashWhite
import com.tenko.app.ui.theme.Tekhelet

@Composable
fun CalendarLegendSection(
    legends: List<CalendarLegend>,
    selectedLegend: String?,
    onLegendSelected: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Text(
            text = "Leyenda",
            fontWeight = FontWeight.SemiBold,
            fontSize = 16.sp,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            legends.forEach { legend ->
                val isSelected = selectedLegend == legend.id

                FilterChip(
                    selected = isSelected,
                    onClick = { onLegendSelected(legend.id) },
                    label = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(14.dp)
                                    .background(
                                        legend.color,
                                        CircleShape
                                    )
                            )

                            Text(legend.label)
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun FloatingLegendSection(
    legends: List<CalendarLegend>,
    selectedLegend: String?,
    showLegend: Boolean,
    onToggleLegend: () -> Unit,
    onLegendSelected: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.End
    ) {
        Surface(
            modifier = Modifier.clickable { onToggleLegend() },
            shape = RoundedCornerShape(12.dp),
            color = AntiFlashWhite,
            contentColor = Tekhelet,
            shadowElevation = 4.dp
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    painter = painterResource(R.drawable.calendar_days_solid_full),
                    contentDescription = null,
                    modifier = Modifier.size(25.dp),
                )

                Text(
                    text = "Leyenda",
                    fontSize = 16.sp
                )
            }
        }

        AnimatedVisibility(
            visible = showLegend,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            Card(
                modifier = Modifier
                    .padding(top = 12.dp),
                shape = RoundedCornerShape(24.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    legends.forEach { legend ->
                        val selected = selectedLegend == legend.id

                        Row(
                            modifier = Modifier
                                .width(IntrinsicSize.Max)
                                .background(
                                    color = if (selected) legend.color.copy(alpha = 0.15f) else Color.Transparent,
                                    shape = RoundedCornerShape(16.dp)
                                )
                                .clickable { onLegendSelected(legend.id) }
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            val icon = when (legend.id) {
                                "real_bleeding" -> R.drawable.droplet_solid_full
                                "past_ovulation" -> R.drawable.heart_solid_full
                                "future_ovulation" -> R.drawable.hand_holding_heart_solid_full
                                "future_bleeding" -> R.drawable.hand_holding_droplet_solid_full
                                else -> R.drawable.calendar_days_solid_full
                            }

                            Icon(
                                painter = painterResource(icon),
                                contentDescription = null,
                                modifier = Modifier.size(18.dp),
                                tint = legend.color
                            )

                            Text(
                                text = legend.label,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }
        }
    }
}