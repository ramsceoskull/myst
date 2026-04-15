package com.tenko.myst.ui.screen

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CardMembership
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.tenko.myst.R
import com.tenko.myst.data.serializable.UserResponse
import com.tenko.myst.data.view.AuthViewModel
import com.tenko.myst.data.view.ProfilePictureViewModel
import com.tenko.myst.navigation.AppScreens
import com.tenko.myst.ui.components.ActionCard
import com.tenko.myst.ui.components.AppTopBar
import com.tenko.myst.ui.components.BottomNavigationBar
import com.tenko.myst.ui.components.MenuItem
import com.tenko.myst.ui.theme.AntiFlashWhite
import com.tenko.myst.ui.theme.StarsLove
import com.tenko.myst.ui.theme.SweetGrey
import com.tenko.myst.ui.theme.Tekhelet
import com.tenko.myst.ui.theme.White

@Composable
fun ProfileScreen(navController: NavController, authViewModel: AuthViewModel, profileViewModel: ProfilePictureViewModel) {
    authViewModel.getUser(navController)
    val user = authViewModel.currentUser
    val photoUri by profileViewModel.photoUri.collectAsState()

    Scaffold(
        topBar = {
            AppTopBar(
                title = "Perfil",
                showBackButton = true,
                onBackClick = { navController.popBackStack() },
                actions = {
                    IconButton(onClick = { }) {
                        Icon(
                            painter = painterResource(R.drawable.ellipsis_vertical_solid_full),
                            contentDescription = "More options",
                            tint = Tekhelet,
                            modifier = Modifier.size(30.dp)
                        )
                    }
                }
            )
        },
        bottomBar = {
            BottomNavigationBar(navController)
        },
        containerColor = White
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(White)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(30.dp))

            ProfileSection(user, photoUri)

            Spacer(modifier = Modifier.height(12.dp))

            PlanAndInviteSection()

            Spacer(modifier = Modifier.height(24.dp))

            MenuItem("Report History", R.drawable.file_pdf_solid_full)
            MenuItem("Clinical History", R.drawable.folder_open_solid_full)
            MenuItem("Help", R.drawable.circle_question_solid_full)
            MenuItem("Editar Perfil", R.drawable.gear_solid_full) {
                    navController.navigate(AppScreens.UpdateProfileScreen.route)
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
fun ProfileSection(user: UserResponse?, photoUri: Uri?) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        if(photoUri == null)
            Image(
                painter = painterResource(R.drawable.profile_picture_placeholder),
                contentDescription = "Profile Picture",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(200.dp)
                    .clip(CircleShape)
            )
        else
            AsyncImage(
                model = photoUri.toString(),
                contentDescription = "Profile Picture",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(200.dp)
                    .clip(CircleShape)
            )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = user?.name ?: "Nombre de Usuario",
            fontSize = 32.sp,
            color = Tekhelet,
            fontWeight = FontWeight.SemiBold,
            fontFamily = StarsLove
        )

        Text(
            text = user?.email ?: "Correo electrónico",
            color = SweetGrey,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun PlanAndInviteSection() {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = AntiFlashWhite),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            Modifier
                .padding(horizontal = 12.dp, vertical = 12.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            ActionCard(
                icon = Icons.Default.CardMembership,
                title = "Prueba",
                subtitle = "Tipo de plan",
                color = Tekhelet,
                enabled = false
            )

            ActionCard(
                icon = Icons.Default.Favorite,
                title = "Invita",
                subtitle = "A amistades",
                color = Tekhelet
            )
        }
    }
}