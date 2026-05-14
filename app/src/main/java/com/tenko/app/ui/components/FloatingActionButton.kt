package com.tenko.app.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.tenko.app.R
import com.tenko.app.ui.theme.PompAndPower
import com.tenko.app.ui.theme.White

@Composable
fun FloatingActionButton(icon: Int, isColored: Boolean = true, onClick: () -> Unit) {
    FloatingActionButton(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        containerColor = PompAndPower,
        contentColor = White
    ) {
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(12.dp)
        ) {
            val color = if (isColored) White else Color.Unspecified

            Icon(
                painter = painterResource(icon),
                contentDescription = null,
                modifier = Modifier.size(40.dp),
                tint = color
            )

            Icon(
                painter = painterResource(R.drawable.plus_solid_full),
                contentDescription = "Add icon",
                modifier = Modifier.size(25.dp)
            )
        }
    }
}