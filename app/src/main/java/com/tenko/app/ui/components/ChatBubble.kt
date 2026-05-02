package com.tenko.app.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.tenko.app.R
import com.tenko.app.data.model.ChatMessage
import com.tenko.app.data.view.AuthViewModel
import com.tenko.app.ui.theme.AntiFlashWhite
import com.tenko.app.ui.theme.PompAndPower
import com.tenko.app.ui.theme.White

@Composable
fun ChatBubble(message: ChatMessage, navController: NavController, authViewModel: AuthViewModel = viewModel()) {
    // 1. Obtenemos el usuario del estado del ViewModel (es reactivo)
    val user = authViewModel.currentUser

    // 2. Si por alguna razón el usuario es nulo (ej. refresco manual),
    // lo pedimos una SOLA VEZ al entrar.
    LaunchedEffect(Unit) {
        if (user == null) {
            authViewModel.getUser(navController)
        }
    }

    val alignment = if (message.isUser) Arrangement.End else Arrangement.Start

    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = alignment
    ) {
        if(!message.isUser) {
            Image(
                painter = painterResource(R.drawable.tenko_avatar),
                contentDescription = null,
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape)
                    .border(2.dp, PompAndPower, CircleShape)
            )
            Spacer(modifier = Modifier.width(8.dp))
        }

        Box(
            modifier = Modifier
                .background(
                    color = if (message.isUser) White else PompAndPower,
                    shape = RoundedCornerShape(12.dp)
                )
                .border(
                    width = 2.dp,
                    color = if (message.isUser) AntiFlashWhite else PompAndPower,
                    shape = RoundedCornerShape(12.dp)
                )
                .padding(horizontal = 16.dp, vertical = 10.dp)
                .widthIn(max = 250.dp)
        ) {
            Text(
                text = message.text,
                fontSize = 14.sp,
                color = if(message.isUser) Color.Black else White
            )
        }

        if(message.isUser) {
            Spacer(modifier = Modifier.width(8.dp))
            ProfilePicture(user?.picture?.toUri(), 50.dp, isChatMessage = true)
        }
    }

    Spacer(modifier = Modifier.height(8.dp))
}