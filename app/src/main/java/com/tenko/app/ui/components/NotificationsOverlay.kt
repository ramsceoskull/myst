package com.tenko.app.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.tenko.app.data.model.NotificationItem
import com.tenko.app.data.view.NotificationViewModel

@Composable
fun NotificationsOverlay(
    padding: Dp = 0.dp,
    viewModel: NotificationViewModel,
    onDismiss: () -> Unit,
    onSeeAllClick: () -> Unit,
    onNotificationClick: (NotificationItem) -> Unit
) {
    AnimatedVisibility(
        visible = true,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.25f))
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                ) { onDismiss() }
        ) {
            Card(
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = (padding + 8.dp))
                    .padding(horizontal = 16.dp)
                    .clickable(enabled = false) {},

                elevation = CardDefaults.cardElevation(8.dp)
            ) {
                NotificationsContent(viewModel, onSeeAllClick, onNotificationClick)
            }
        }
    }
}