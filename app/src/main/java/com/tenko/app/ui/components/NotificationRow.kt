package com.tenko.app.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.tenko.app.R
import com.tenko.app.data.model.NotificationItem
import com.tenko.app.ui.theme.PompAndPower
import com.tenko.app.ui.theme.White

@Composable
fun NotificationRow(
    notification: NotificationItem,
    onClick: () -> Unit
) {
    val background by animateColorAsState(
        targetValue = if (notification.isRead) Color(0xFFEAEAEA) else White,
        label = "bgAnim"
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(background)
            .clickable { onClick() }
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Avatar placeholder
        Image(
            painter = painterResource(R.drawable.tenko_avatar),
            contentDescription = "Foto de remitente",
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
        )

        Spacer(modifier = Modifier.width(8.dp))

        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(notification.title, fontWeight = FontWeight.Bold)

                // Badge (puntito rojo para no leídas)
                if (!notification.isRead) {
                    Spacer(modifier = Modifier.width(6.dp))
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(PompAndPower)
                    )
                }
            }

            Text(notification.message, color = Color.Gray)
        }

        Icon(
            painter = painterResource(R.drawable.angle_right_solid_full),
            contentDescription = "See details",
            tint = Color.Gray,
            modifier = Modifier.size(25.dp)
        )
    }

    HorizontalDivider()
}