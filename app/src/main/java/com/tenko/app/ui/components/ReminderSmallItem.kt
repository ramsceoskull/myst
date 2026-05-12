package com.tenko.app.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.tenko.app.data.serializable.ReminderResponse

@Composable
fun ReminderSmallItem(reminder: ReminderResponse) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFF9F9F9)
        )
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(reminder.title, fontWeight = FontWeight.Bold)
            Text(
                "${reminder.end_date} a las ${reminder.day_time}",
                style = MaterialTheme.typography.labelSmall
            )
        }
    }
}