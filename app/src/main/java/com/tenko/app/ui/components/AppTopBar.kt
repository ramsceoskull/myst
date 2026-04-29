package com.tenko.app.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.wear.compose.material3.ConfirmationDialog
import androidx.wear.compose.material3.TextButtonDefaults
import com.tenko.app.R
import com.tenko.app.data.model.Quote
import com.tenko.app.data.view.AuthViewModel
import com.tenko.app.data.view.NotificationViewModel
import com.tenko.app.navigation.AppScreens
import com.tenko.app.ui.theme.AntiFlashWhite
import com.tenko.app.ui.theme.Monserrat
import com.tenko.app.ui.theme.PompAndPower
import com.tenko.app.ui.theme.StarsLove
import com.tenko.app.ui.theme.SweetGrey
import com.tenko.app.ui.theme.Tekhelet
import com.tenko.app.ui.theme.White
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay

val quotes = listOf(
    Quote(
        id = 0,
        quote = "Una mujer con fuerza interior no solo enfrenta las tormentas, las usa para crecer y brillar aún más",
        author = "Autor desconocido"
    ),
    Quote(
        id = 1,
        quote = "Tu bienestar es tu mayor poder",
        author = "Myst"
    ),
    Quote(
        id = 2,
        quote = "Escuchar tu cuerpo también es amor propio",
        author = "Myst"
    )
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopBar(
    title: String,
    onBackClick: (() -> Unit)? = null,
    actions: Triple<() -> Unit, Int, String> = Triple({}, 0, "")
) {
    var showDialog by remember { mutableStateOf(false) }

    Surface(
        shadowElevation = 4.dp,
        border = BorderStroke(1.dp, AntiFlashWhite)
    ) {
        TopAppBar(
            title = {
                Text(
                    text = title,
                    color = Tekhelet,
                    fontFamily = StarsLove,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 32.sp,
                    modifier = Modifier.offset(y = 4.dp)
                )
            },
            navigationIcon = {
                onBackClick?.let {
                    IconButton(onClick = it) {
                        Icon(
                            modifier = Modifier.size(28.dp),
                            painter = painterResource(R.drawable.chevron_left_solid_full),
                            contentDescription = "Back",
                            tint = Tekhelet
                        )
                    }
                }
            },
            actions = {
                if(actions.second != 0) {
                    IconButton(onClick = { showDialog = true }) {
                        Icon(
                            modifier = Modifier.size(28.dp),
                            painter = painterResource(id = actions.second),
                            contentDescription = null,
                            tint = Tekhelet
                        )
                    }
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = White),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp)
        )

        if(showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                confirmButton = {
                    TextButton(
                        onClick = {
                            actions.first()
                            showDialog = false
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = White,
                            containerColor = Tekhelet
                        ),
                        content = { Text("Confirmar") }
                    )
                },
                dismissButton = {
                    TextButton(
                        onClick = { showDialog = false },
                        content = { Text("Cancelar", color = SweetGrey) }
                    )
                },
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            modifier = Modifier.size(24.dp),
                            painter = painterResource(actions.second),
                            contentDescription = null,
                        )
                        Text("¿Estás segura?")
                    }
                },
                text = {
                    Text(actions.third)
                },
                shape = RoundedCornerShape(12.dp),
                containerColor = White,
                titleContentColor = Tekhelet,
                textContentColor = SweetGrey
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopBar(
    title: String,
    navController: NavController,
    scrollBehavior: TopAppBarScrollBehavior,
    notificationViewModel: NotificationViewModel,
    authViewModel: AuthViewModel = viewModel(),
    actions: () -> Unit
) {
    // 1. Obtenemos el usuario del estado del ViewModel (es reactivo)
    val user = authViewModel.currentUser

    // 2. Si por alguna razón el usuario es nulo (ej. refresco manual),
    // lo pedimos una SOLA VEZ al entrar.
    LaunchedEffect(Unit) {
        if (user == null) {
            authViewModel.getUser(navController)
        }
    }

    val hasUnread = notificationViewModel.hasUnread

    Surface(
        shadowElevation = 4.dp,
        border = BorderStroke(1.dp, AntiFlashWhite)
    ) {
        TopAppBar(
            title = {
                Text(
                    text = title,
                    color = Tekhelet,
                    fontFamily = StarsLove,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 32.sp,
                    modifier = Modifier.offset(y = 4.dp)
                )
            },
            navigationIcon = {
                IconButton(
                    onClick = { navController.navigate(AppScreens.ProfileScreen.route) },
                    content = { ProfilePicture(user?.picture?.toUri(), 40.dp, false) }
                )
            },
            actions = {
                IconButton(onClick = actions) {
                    if(hasUnread) {
                        Icon(
                            tint = null,
                            contentDescription = "Bandeja de entrada",
                            painter = painterResource(R.drawable.bell_new_notification),
                            modifier = Modifier.size(42.dp)
                        )
                    } else {
                        Icon(
                            tint = null,
                            contentDescription = "Bandeja de entrada",
                            painter = painterResource(R.drawable.bell_no_notification),
                            modifier = Modifier.size(42.dp)
                        )
                    }
                }
            },
            colors = TopAppBarDefaults.topAppBarColors( containerColor = White, scrolledContainerColor = White ),

            scrollBehavior = scrollBehavior,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopBar(
    title: String,
    scrollBehavior: TopAppBarScrollBehavior,
    navController: NavController,
    onNotificationsClick: () -> Unit,
    actions: @Composable RowScope.() -> Unit = {}
) {
    Surface(
        shadowElevation = 4.dp,
        border = BorderStroke(1.dp, SweetGrey)
    ) {
        MediumTopAppBar(
            title = {
                if(scrollBehavior.state.collapsedFraction < 0.5f)
                    WelcomeSection()
                else {
                    Text(
                        text = title,
                        fontSize = 26.sp,
                        fontWeight = FontWeight.SemiBold,
                        fontFamily = StarsLove,
                        color = Tekhelet,
                        modifier = Modifier.padding(start = 8.dp, top = 8.dp)
                    )
                }
            },

            navigationIcon = {
                IconButton(
                    onClick = { navController.navigate(AppScreens.ProfileScreen.route) },
                    modifier = Modifier
                        .padding(start = 8.dp)
                        .size(40.dp)
                        .clip(CircleShape)
                ) {
                    Image(
                        contentScale = ContentScale.Crop,
                        contentDescription = "Foto de perfil",
                        painter = painterResource(R.drawable.mujer4)
                    )
                }
            },

            actions = {
                IconButton(onClick = onNotificationsClick, modifier = Modifier.padding(end = 8.dp)) {
                    Icon(
                        tint = null,
                        contentDescription = "Bandeja de entrada",
                        painter = painterResource(R.drawable.bell_new_notification),
                        modifier = Modifier.size(42.dp)
                    )
                }
            },

            colors = TopAppBarDefaults.topAppBarColors( containerColor = White, scrolledContainerColor = White ),

            scrollBehavior = scrollBehavior
        )
    }
}

@Composable
fun WelcomeSection() {
    var index by remember { mutableIntStateOf(0) }

    LaunchedEffect(Unit) {
        while (true) {
            delay(8000)
            index = (index + 1) % quotes.size
        }
    }

    Column (
        modifier = Modifier
            .height(IntrinsicSize.Min)
            .padding(start = 4.dp, end = 22.dp, bottom = 8.dp)
    ) {
        Text(
            text = "Hello,",
            fontSize = 20.sp,
            fontFamily = Monserrat,
            fontWeight = FontWeight.Medium,
            color = SweetGrey
        )
        Text(
            text = "Alessandra Wins",
            fontSize = 32.sp,
            fontFamily = StarsLove,
            fontWeight = FontWeight.SemiBold,
            color = Tekhelet
        )

        DotsIndicator(
            total = quotes.size,
            selectedIndex = index
        )

        Spacer(modifier = Modifier.height(12.dp))

        AnimatedContent /*Crossfade*/(
            targetState = index,
            label = ""
        ) { i ->
            Column {
                Text(
                    color = SweetGrey,
                    text = "\"${quotes[i].quote}\"",
                    fontSize = 14.sp,
                    fontFamily = Monserrat,
                    lineHeight = 20.sp,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Justify
                )

                Text(
                    color = PompAndPower,
                    text = "- ${quotes[i].author}",
                    fontSize = 12.sp,
                    fontFamily = Monserrat,
                    fontWeight = FontWeight.Medium,
                )
            }
        }
    }
}

@Composable
fun DotsIndicator(
    total: Int,
    selectedIndex: Int
) {
    Row {
        repeat(total) { i ->
            Box(
                modifier = Modifier
                    .padding(end = 4.dp)
                    .size(
                        if (i == selectedIndex) 8.dp else 6.dp
                    )
                    .clip(CircleShape)
                    .background(
                        if (i == selectedIndex)
                            Tekhelet
                        else
                            SweetGrey
                    )
            )
        }
    }
}