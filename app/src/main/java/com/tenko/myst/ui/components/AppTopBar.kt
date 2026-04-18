package com.tenko.myst.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.tenko.myst.R
import com.tenko.myst.data.model.Quote
import com.tenko.myst.data.view.AuthViewModel
import com.tenko.myst.data.view.NotificationViewModel
import com.tenko.myst.navigation.AppScreens
import com.tenko.myst.ui.theme.AntiFlashWhite
import com.tenko.myst.ui.theme.Monserrat
import com.tenko.myst.ui.theme.PompAndPower
import com.tenko.myst.ui.theme.StarsLove
import com.tenko.myst.ui.theme.SweetGrey
import com.tenko.myst.ui.theme.Tekhelet
import com.tenko.myst.ui.theme.White
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
    showBackButton: Boolean = true,
    onBackClick: (() -> Unit)? = null,
    navigationIcon: Int = R.drawable.chevron_left_solid_full,
    actions: @Composable RowScope.() -> Unit = {}
) {
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
                    modifier = Modifier.padding(top = 8.dp)
                )
            },
            navigationIcon = {
                if (showBackButton)
                    onBackClick?.let {
                        IconButton(onClick = it) {
                            Icon(
                                modifier = Modifier.size(28.dp),
                                painter = painterResource(navigationIcon),
                                contentDescription = "Back",
                                tint = Tekhelet
                            )
                        }
                    }
            },
            actions = actions,
            colors = TopAppBarDefaults.topAppBarColors( containerColor = White )
        )
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
    authViewModel.getUser(navController)
    val hasUnread = notificationViewModel.hasUnread
    val user = authViewModel.currentUser

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
                    modifier = Modifier.padding(top = 8.dp, start = 8.dp)
                )
            },
            navigationIcon = {
                IconButton(
                    onClick = { navController.navigate(AppScreens.ProfileScreen.route) },
                    modifier = Modifier
                        .padding(start = 8.dp)
                        .size(40.dp)
                        .clip(CircleShape),
                    content = {
                        AsyncImage(
                            model = user?.picture,
                            contentDescription = "Profile Photo",
                            modifier = Modifier
                                .size(150.dp)
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop,
                            placeholder = painterResource(R.drawable.profile_picture_placeholder),
                            error = painterResource(R.drawable.profile_picture_placeholder)
                        )
                    }
                )
            },
            actions = {
                IconButton(onClick = actions, modifier = Modifier.padding(end = 8.dp)) {
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

            scrollBehavior = scrollBehavior
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