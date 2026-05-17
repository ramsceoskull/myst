package com.tenko.app.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Scaffold
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.tenko.app.R
import com.tenko.app.data.view.DoctorViewModel
import com.tenko.app.navigation.AppScreens
import com.tenko.app.ui.components.AppTopBar
import com.tenko.app.ui.components.BottomNavigationBar
import com.tenko.app.ui.components.DoctorCard
import com.tenko.app.ui.components.EmptyStateFullscreen
import com.tenko.app.ui.components.FloatingActionButton
import com.tenko.app.ui.theme.AntiFlashWhite
import com.tenko.app.ui.theme.BackgroundColor
import com.tenko.app.ui.theme.CardDark
import com.tenko.app.ui.theme.CardGray
import com.tenko.app.ui.theme.CardPurple
import com.tenko.app.ui.theme.RaisinBlack
import com.tenko.app.ui.theme.Tekhelet
import com.tenko.app.ui.theme.White

@Composable
fun DoctorsScreen(
    navController: NavController,
    viewModel: DoctorViewModel = viewModel()
) {
    LaunchedEffect(Unit) { viewModel.fetchContacts() }
    val isRefreshing = viewModel.isLoading

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            topBar = {
                AppTopBar(
                    title = "Mis doctores",
                    onBackClick = { navController.popBackStack() },
                ) {}
            },
            floatingActionButton = {
                FloatingActionButton(R.drawable.user_doctor_solid_full) {
                    navController.navigate(AppScreens.AddDoctorScreen.route)
                }
            },
            bottomBar = { BottomNavigationBar(navController) },
            containerColor = BackgroundColor
        ) { paddingValues ->
            PullToRefreshBox(
                isRefreshing = isRefreshing,
                onRefresh = { viewModel.fetchContacts() },
                modifier = Modifier
                    .background(White)
                    .padding(paddingValues)
            ) {

                if (viewModel.contacts.isEmpty() && !isRefreshing)
                    EmptyStateFullscreen(
                        icon = R.drawable.users_solid_full,
                        title = "No tienes especialistas registrados",
                        description = "Agrega tu primer especialista para comenzar"
                    )
                else {
                    LazyColumn(
                        contentPadding = PaddingValues(horizontal = 25.dp),
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(18.dp)
                    ) {

                        item { Spacer(modifier = Modifier.height(30.dp)) }

                        itemsIndexed(viewModel.contacts) { i, contact ->

                            val colors = when (i % 5) {
                                0 -> listOf(CardGray, RaisinBlack, Color.Gray)
                                1 -> listOf(CardPurple, White, AntiFlashWhite)
                                2 -> listOf(Tekhelet, White, AntiFlashWhite)
                                3 -> listOf(CardDark, White, AntiFlashWhite)
                                else -> listOf(RaisinBlack, White, AntiFlashWhite)
                            }

                            DoctorCard(
                                contact = contact,
                                colors = colors,
                                onClick = {
                                    navController.navigate(
                                        AppScreens.DoctorDetailsScreen
                                            .createRoute(contact.id_contact)
                                    )
                                }
                            )
                        }

                        item { Spacer(modifier = Modifier.height(30.dp)) }
                    }
                }
            }
        }

        if (isRefreshing) {
            SplashScreen()
        }
    }
}