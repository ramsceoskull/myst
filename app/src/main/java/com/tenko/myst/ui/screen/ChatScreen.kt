package com.tenko.myst.ui.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.tenko.myst.R
import com.tenko.myst.data.model.ChatMessage
import com.tenko.myst.ui.components.AppTopBar
import com.tenko.myst.ui.components.BottomNavigationBar
import com.tenko.myst.ui.components.ChatBubble
import com.tenko.myst.ui.components.MessageBubble
import com.tenko.myst.ui.components.MessageInput
import com.tenko.myst.ui.theme.SweetGrey
import com.tenko.myst.data.view.ChatViewModel


@Composable
fun ChatScreen(navController: NavHostController, viewModel: ChatViewModel = viewModel()) {
    val messages by viewModel.messages.collectAsState()
    val isTyping by viewModel.isTyping.collectAsState()

    val lightGray = Color(0xFFF2F2F2)

    val listState = rememberLazyListState()

    LaunchedEffect(messages.size) {
        listState.animateScrollToItem(messages.size)
    }

    Scaffold(
        topBar = {
            Surface(
                shadowElevation = 4.dp,
                border = BorderStroke(1.dp, SweetGrey),
            ) {
                AppTopBar(
                    title = "Tenko",
                    showBackButton = true,
                    onBackClick = { navController.popBackStack() },
                )
            }
        },
        bottomBar = {
            BottomNavigationBar(navController)
        },
        containerColor = lightGray
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(horizontal = 20.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            WelcomeSection()

            Spacer(modifier = Modifier.weight(1f))

            ChatBubble()

            Spacer(modifier = Modifier.height(16.dp))

            ChatIndicator()

            Spacer(modifier = Modifier.height(16.dp))

            MessageInput()

            Spacer(modifier = Modifier.height(16.dp))
        }
        /*Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            LazyColumn(
                state = listState,
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(16.dp)
            ) {
                items(messages, key = { it.id }) { message ->
                    AnimatedMessageBubble(message)
                }

                if (isTyping) {
                    item {
                        TypingIndicator()
                    }
                }
            }
            MessageInput { text ->
                viewModel.sendMessage(text)
            }
        }*/
    }
}

@Composable
fun AnimatedMessageBubble(message: ChatMessage) {
    AnimatedVisibility(
        visible = true,
        enter = fadeIn() + slideInVertically { it / 2 }
    ) {
        MessageBubble(message)
    }
}

@Composable
fun WelcomeSection() {
    Row(verticalAlignment = Alignment.Top) {
        Image(
            painter = painterResource(R.drawable.tenko_avatar),
            contentDescription = null,
            modifier = Modifier.size(120.dp)
        )

        Spacer(modifier = Modifier.width(12.dp))

        Column {

            Text(
                "🌸 ¡Hola! Me alegra verte por aquí.",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                "Estoy aquí para escucharte y ayudarte a registrar cómo te sientes hoy.",
                fontSize = 14.sp,
                color = Color.DarkGray
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                "Cuéntame, ¿cómo te encuentras? 💕",
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun ChatIndicator() {
    Row(
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxWidth()
    ) {
        repeat(3) {
            Box(
                modifier = Modifier
                    .padding(4.dp)
                    .size(10.dp)
                    .clip(CircleShape)
                    .background(Color.Gray)
            )
        }
    }
}