package com.tenko.myst.ui.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.tenko.myst.data.model.NotificationItem
import com.tenko.myst.data.view.NotificationViewModel
import com.tenko.myst.ui.components.NotificationRow

@Composable
fun AllNotificationsScreen(
    viewModel: NotificationViewModel,
    onBack: () -> Unit,
    onNotificationClick: (NotificationItem) -> Unit
) {
    val notifications = viewModel.getFilteredNotifications()

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {

        Text("All Notifications", fontWeight = FontWeight.Bold)

        Spacer(modifier = Modifier.height(12.dp))

        LazyColumn {
            items(notifications) { notification ->
                NotificationRow(notification) {
                    viewModel.markAsRead(notification.id)
                    onNotificationClick(notification)
                }
            }
        }
    }
}