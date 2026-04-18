package com.tenko.myst.ui.screen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.tenko.myst.data.view.ChatViewModel
import com.tenko.myst.ui.components.AnswerSelector
import com.tenko.myst.ui.components.AppTopBar
import com.tenko.myst.ui.components.BottomBar
import com.tenko.myst.ui.components.ChatBubble
import com.tenko.myst.ui.components.TypingIndicator
import com.tenko.myst.ui.theme.SweetGrey
import com.tenko.myst.ui.theme.Tekhelet
import com.tenko.myst.ui.theme.White


@Composable
fun ChatScreen(navController: NavHostController, viewModel: ChatViewModel = viewModel()) {
    val messages by viewModel.messages.collectAsState()
    val isTyping by viewModel.isTyping.collectAsState()

    val lastMessage = viewModel.messages.collectAsState().value.lastOrNull()
    var showInputPrompt = false

    val listState = rememberLazyListState()

    LaunchedEffect(messages.size, isTyping) {
        listState.animateScrollToItem(
            index = messages.size + if (isTyping) 1 else 0
        )
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
            if(viewModel.isQuestionnaireMode && lastMessage?.questionRef != null) {
                AnswerSelector(lastMessage.questionRef) { answer ->
                    viewModel.sendMessage(answer)
                }
            } else if(showInputPrompt) {
                BottomBar({ viewModel.sendMessage(it) })
            }
        },
        containerColor = Color(0xFFF2F2F2)
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(horizontal = 20.dp, vertical = 12.dp)
        ) {
            LazyColumn(
                state = listState,
                modifier = Modifier.weight(1f)
            ) {
                items(messages) { message ->
                    ChatBubble(message, navController)
                }

                item {
                    Box(modifier = Modifier.padding(16.dp).fillMaxWidth()) {
                        if(viewModel.messages.collectAsState().value.size == 2) {
                            showInputPrompt = true
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                OutlinedButton(
                                    onClick = { viewModel.sendMessage("Modificar historial")},
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Tekhelet,
                                        contentColor = White,
                                    ),
//                                    border = BorderStroke(2.dp, Tekhelet),
                                    shape = RoundedCornerShape(12.dp),
                                    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp)
                                ) {
                                    Text("Actualizar historial")
                                }
                                OutlinedButton(
                                    onClick = { viewModel.sendMessage("Daily log") },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Tekhelet,
                                        contentColor = White,
                                    ),
//                                    border = BorderStroke(2.dp, Tekhelet),
                                    shape = RoundedCornerShape(12.dp),
                                    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp)
                                ) {
                                    Text("Mi día")
                                }
                            }
                        }
                    }

                    if(isTyping)
                        TypingIndicator()
                }
            }
        }
    }
}