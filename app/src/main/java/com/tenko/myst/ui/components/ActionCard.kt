package com.tenko.myst.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tenko.myst.ui.theme.SweetGrey
import com.tenko.myst.ui.theme.Tekhelet
import com.tenko.myst.ui.theme.White

@Composable
fun ActionCard(
    icon: ImageVector,
    title: String,
    subtitle: String,
    color: Color = Tekhelet,
    enabled: Boolean = true) {
    Card(
        onClick = {},
        enabled = enabled
    ) {
        Row(
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(White)
                .padding(horizontal = 12.dp, vertical = 16.dp)
                .width(138.dp)
        ) {
            Icon(icon, null, tint = color, modifier = Modifier.size(32.dp))

            Spacer(modifier = Modifier.width(8.dp))

            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                Text(title, color = color, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Text(subtitle, fontSize = 14.sp, color = SweetGrey)
            }
        }
    }
}