package com.tenko.app.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tenko.app.R
import com.tenko.app.ui.theme.AntiFlashWhite
import com.tenko.app.ui.theme.SweetGrey
import com.tenko.app.ui.theme.Tekhelet
import com.tenko.app.ui.theme.White

@Composable
fun ActionCard(
    icon: Int,
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null
) {
    Card(
        onClick = { onClick?.invoke() },
        enabled = onClick != null,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = White,
            contentColor = Tekhelet,
            disabledContainerColor = White,
            disabledContentColor = SweetGrey.copy(alpha = 0.5f)
        )
    ) {
        Row(
            modifier = modifier.padding(horizontal = 12.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally)
        ) {
            Icon(
                painter = painterResource(icon),
                contentDescription = title,
                modifier = Modifier.size(32.dp)
            )

            Column {
                Text(text = title, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Text(text = subtitle, fontSize = 14.sp, color = SweetGrey)
            }
        }
    }
}

@Composable
fun ActionCard(
    icon: Int,
    title: String,
    onClick: (() -> Unit)? = null
) {
    Card(
        onClick = { onClick?.invoke() },
        enabled = onClick != null,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = AntiFlashWhite,
            contentColor = Tekhelet,
            disabledContainerColor = SweetGrey.copy(alpha = 0.5f),
            disabledContentColor = SweetGrey.copy(alpha = 0.5f)
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(icon),
                contentDescription = title,
                modifier = Modifier.size(30.dp)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Text(
                text = title,
                fontSize = 18.sp,
                modifier = Modifier.weight(1f)
            )

            Icon(
                painter = painterResource(R.drawable.chevron_right_solid_full),
                contentDescription = "Proceed",
                modifier = Modifier.size(25.dp)
            )
        }
    }
}