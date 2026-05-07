package com.tenko.app.ui.components

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tenko.app.R
import com.tenko.app.data.model.Genre
import com.tenko.app.ui.theme.White

val femaleAvatars = listOf(
    R.drawable.doctor0,
    R.drawable.doctor1,
    R.drawable.doctor2
)
val maleAvatars = listOf(
    R.drawable.doctor3,
    R.drawable.doctor4,
)

@Composable
fun AvatarSelector(
    avatar: Int?,
    genre: Genre,
    onAvatarChange: (Int) -> Unit
) {
    Column {
        Text(
            text = "Vista previa del avatar",
            color = Color.Gray,
            fontSize = 14.sp
        )
        Text(
            text = "Selecciona un avatar para tu especialista.",
            color = Color.LightGray,
            fontSize = 12.sp
        )

        Spacer(modifier = Modifier.height(6.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            val icons = if (genre == Genre.FEMALE) femaleAvatars else maleAvatars

            icons.forEach { icon ->
                val isSelected = avatar == icon

                // Determine which image to show (colored version if selected)
                val imageToShow = if (isSelected) {
                    when (icon) {
                        R.drawable.doctor0 -> R.drawable.avatar_female_doctor0
                        R.drawable.doctor1 -> R.drawable.avatar_female_doctor1
                        R.drawable.doctor2 -> R.drawable.avatar_female_doctor2
                        R.drawable.doctor3 -> R.drawable.avatar_male_doctor3
                        R.drawable.doctor4 -> R.drawable.avatar_male_doctor4
                        else -> R.drawable.profile_picture_placeholder
                    }
                } else icon

                Card(
                    onClick = { onAvatarChange(icon) },
                    colors = CardDefaults.cardColors(White),
//                    elevation = CardDefaults.cardElevation(if (isSelected) 8.dp else 0.dp),
                    content = {
                        Crossfade(
                            targetState = imageToShow,
                            label = "avatarChange"
                        ) { targetImage ->
                            Image(
                                painter = painterResource(targetImage),
                                contentDescription = "Avatar",
                                modifier = Modifier.size(100.dp)
                            )
                        }
                    }
                )
            }
        }
    }
}
