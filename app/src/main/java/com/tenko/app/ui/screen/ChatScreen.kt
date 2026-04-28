package com.tenko.app.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.tenko.app.R
import com.tenko.app.data.view.ChatViewModel
import com.tenko.app.navigation.AppScreens
import com.tenko.app.ui.components.AnswerSelector
import com.tenko.app.ui.components.AppTopBar
import com.tenko.app.ui.components.ChatBubble
import com.tenko.app.ui.components.ChatInput
import com.tenko.app.ui.components.TypingIndicator
import com.tenko.app.ui.theme.BackgroundColor
import com.tenko.app.ui.theme.RaisinBlack
import com.tenko.app.ui.theme.Tekhelet
import com.tenko.app.ui.theme.White
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun ChatScreen(navController: NavHostController, viewModel: ChatViewModel = viewModel()) {
    val lastMessage = viewModel.messages.collectAsState().value.lastOrNull()
    val messages by viewModel.messages.collectAsState()
    val isTyping by viewModel.isTyping.collectAsState()

    var showBottomInput by remember { mutableStateOf(false) }
    var isRefreshing by remember { mutableStateOf(false) }

    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    LaunchedEffect(messages.size, isTyping) {
        listState.animateScrollToItem(
            index = messages.size + if (isTyping) 1 else 0
        )
    }

    Scaffold(
        topBar = {
            AppTopBar(
                title = "Tenko",
                onBackClick = { navController.popBackStack() },
                actions =
                    Triple(
                        {
                            scope.launch {
                                isRefreshing = true
                                delay(1000)
                                navController.popBackStack()
                                navController.navigate(AppScreens.ChatScreen.route)
                                isRefreshing = false
                            }
                        },
                        R.drawable.rotate_right_solid_full,
                        "Reiniciar el chat borrará toda la conversación actual. Esta acción no se puede deshacer."
                    )
            )
        },
        bottomBar = {
            if (showBottomInput)
                Surface(
                    shape = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp),
                    shadowElevation = 8.dp,
                ) {
                    NavigationBar(
                        modifier = Modifier.shadow(
                            elevation = 8.dp,
                            shape = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp),
                            ambientColor = RaisinBlack,
                            spotColor = RaisinBlack,
                            clip = false
                        ),
                        containerColor = White,
                    ) {
                        Box(modifier = Modifier.padding(12.dp)) {
                            if(viewModel.isQuestionnaireMode && lastMessage?.questionRef != null) {
                                AnswerSelector(lastMessage.questionRef) { answer ->
                                    viewModel.sendMessage(answer)
                                }
                            } else if(viewModel.messages.collectAsState().value.size > 2) {
                                ChatInput(onSend = { viewModel.sendMessage(it) }, modifier = Modifier.imePadding())
                            }
                        }
                    }
                }
        },
        containerColor = BackgroundColor
    ) { paddingValues ->
        PullToRefreshBox(
            isRefreshing = isRefreshing,
            onRefresh = {
                scope.launch {
                    isRefreshing = true
                    delay(2000)
                    navController.popBackStack()
                    navController.navigate(AppScreens.ChatScreen.route)
                    isRefreshing = false
                }
            }
        ) {
            LazyColumn(
                state = listState,
                contentPadding = paddingValues,
                modifier = Modifier.fillMaxSize()
            ) {
                item { Spacer(modifier = Modifier.height(20.dp)) }
                items(messages) { message ->
                    ChatBubble(message, navController)
                }

                item {
                    Box(modifier = Modifier.padding(16.dp).fillMaxWidth()) {
                        if(viewModel.messages.collectAsState().value.size == 2) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                OutlinedButton(
                                    onClick = { viewModel.sendMessage("Modificar historial"); showBottomInput = true },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Tekhelet,
                                        contentColor = White,
                                    ),
                                    shape = RoundedCornerShape(12.dp),
                                    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp),
                                    content = { Text("Actualizar historial") }
                                )

                                OutlinedButton(
                                    onClick = { viewModel.sendMessage("Daily log"); showBottomInput = true },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Tekhelet,
                                        contentColor = White,
                                    ),
                                    shape = RoundedCornerShape(12.dp),
                                    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp),
                                    content = { Text("Mi día") }
                                )
                            }
                        }
                    }

                    if(isTyping)
                        TypingIndicator()
                }

                item { Spacer(modifier = Modifier.height(12.dp)) }
            }
        }
    }
}