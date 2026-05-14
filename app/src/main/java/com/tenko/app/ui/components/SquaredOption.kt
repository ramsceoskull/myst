package com.tenko.app.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement.Absolute.spacedBy
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.tenko.app.ui.theme.PompAndPower
import com.tenko.app.ui.theme.White

@Composable
fun SquaredOption(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    icon: Int? = null
) {
    val scale by animateFloatAsState(
        targetValue = if (selected) 1.1f else 1f,
        animationSpec = tween(250),
        label = "scale"
    )

    val backgroundColor by animateColorAsState(
        targetValue = if (selected) PompAndPower else White,
        label = "background"
    )

    val textColor by animateColorAsState(
        targetValue = if (selected) White else Color.Gray,
        label = "textColor"
    )

    val elevation by animateDpAsState(
        targetValue = if (selected) 8.dp else 2.dp,
        label = "elevation"
    )

    Box(
        modifier = Modifier
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .shadow(elevation, RoundedCornerShape(12.dp))
            .clip(RoundedCornerShape(12.dp))
            .background(backgroundColor)
            .clickable { onClick() }
            .padding(horizontal = 25.dp, vertical = 15.dp),
        content = {
            Row(
                horizontalArrangement = spacedBy(8.dp, Alignment.CenterHorizontally),
                verticalAlignment = Alignment.CenterVertically
            ) {
                icon?.let {
                    Icon(
                        painter = painterResource(icon),
                        contentDescription = "$text icon",
                        modifier = Modifier.size(20.dp),
                        tint = textColor
                    )
                }

                Text(text = text, color = textColor, textAlign = TextAlign.Center)
            }
        }
    )
}