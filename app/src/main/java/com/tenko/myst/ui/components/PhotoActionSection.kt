package com.tenko.myst.ui.components

import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.tenko.myst.R
import com.tenko.myst.ui.theme.PompAndPower
import com.tenko.myst.ui.theme.White

@Composable
fun PhotoActionsSection(
    imageUrl: Uri?,
    onEditClick: () -> Unit,
    onRemoveClick: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AsyncImage(
            model = imageUrl,
            contentDescription = "Profile Photo",
            modifier = Modifier
                .size(150.dp)
                .clip(CircleShape),
            contentScale = ContentScale.Crop,
            placeholder = painterResource(R.drawable.profile_picture_placeholder),
            error = painterResource(R.drawable.profile_picture_placeholder)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            ActionChip(
                text = "Cambiar foto",
                onClick = onEditClick,
                backgroundColor = PompAndPower,
                contentColor = White
            )

            Spacer(modifier = Modifier.width(12.dp))

            ActionChip(
                text = "Eliminar foto",
                onClick = onRemoveClick,
                backgroundColor = MaterialTheme.colorScheme.error,
                contentColor = White
            )
        }
    }
}