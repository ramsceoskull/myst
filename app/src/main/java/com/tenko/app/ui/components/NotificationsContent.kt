package com.tenko.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tenko.app.data.model.NotificationItem
import com.tenko.app.data.view.NotificationViewModel
import com.tenko.app.ui.theme.Tekhelet
import com.tenko.app.ui.theme.White

@Composable
fun NotificationsContent(
    viewModel: NotificationViewModel,
    onSeeAllClick: () -> Unit,
    onNotificationClick: (NotificationItem) -> Unit
) {
    val notifications = viewModel.getFilteredNotifications()

    Column ( modifier = Modifier.background(White) ) {
        /// HEADER
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Notificaciones",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "Marcar como leídas",
                color = Tekhelet,
                fontSize = 14.sp,
                modifier = Modifier.clickable { viewModel.markAllAsRead() }
            )
        }

        /// TABS
        FilterSection(viewModel)
        HorizontalDivider()

        /// LIST
        if(notifications.isEmpty()) {
            Text(
                "No hay notificaciones",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                textAlign = TextAlign.Center,
                color = Color.Gray
            )
        } else {
            notifications.take(3).forEach { notification ->
                NotificationRow(
                    notification = notification,
                    onClick = {
                        viewModel.markAsRead(notification.id)
                        onNotificationClick(notification)
                    }
                )
            }

            if(notifications.size > 3) {
                HorizontalDivider()

                Text(
                    text = "Verlas todas",
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .clickable { onSeeAllClick() }
                )
            }
        }
    }
}