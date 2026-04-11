package com.tenko.myst.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.tenko.myst.ui.theme.PompAndPower
import com.tenko.myst.ui.theme.White

@Composable
fun FoodOption(
    text: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    val scale by animateFloatAsState(if (selected) 1.05f else 1f)

    Box(
        modifier = Modifier
            .scale(scale)
            .clip(RoundedCornerShape(16.dp))
            .background(color = if (selected) PompAndPower else Color.White)
            .clickable { onClick() }
            .padding(16.dp)
    ) {
        Text(
            text = text,
            color = if (selected) White else Color.Gray
        )
    }
}