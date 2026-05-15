package com.tenko.app.ui.components

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.tenko.app.R
import com.tenko.app.data.serializable.ContactResponse
import com.tenko.app.data.view.AuthViewModel
import com.tenko.app.ui.theme.AntiFlashWhite

@Composable
fun DoctorHeader(
    navController: NavController,
    doctor: ContactResponse,
    authViewModel: AuthViewModel = viewModel()
) {
    val user = authViewModel.currentUser
    LaunchedEffect(Unit) {
        if (user == null) {
            authViewModel.getUser(navController)
        }
    }

    var hasPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                navController.context,
                Manifest.permission.CALL_PHONE
            ) == PackageManager.PERMISSION_GRANTED
        )
    }
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasPermission = granted

        if (granted) {
            val intent = Intent(
                Intent.ACTION_CALL,
                "tel:${doctor.phone_number}".toUri()
            )
            navController.context.startActivity(intent)
        }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        val avatar = when (doctor.genre) {
            0 -> R.drawable.doctor0
            1 -> R.drawable.doctor1
            2 -> R.drawable.doctor2
            3 -> R.drawable.doctor3
            4 -> R.drawable.doctor4
            else -> R.drawable.profile_picture_placeholder
        }

        Image(
            painter = painterResource(avatar),
            contentDescription = "Doctor Avatar",
            modifier = Modifier
                .size(150.dp)
                .background(
                    color = AntiFlashWhite,
                    shape = RoundedCornerShape(16.dp)
                ),
            contentScale = ContentScale.Crop
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 12.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = "${doctor.name!!} ${doctor.last_name!!}",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = doctor.specialty!!,
                    color = Color.Gray,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.Start)) {
                ShortcutProfileIcon(R.drawable.phone_solid_full, "Llamar") {
                    if (hasPermission) {
                        val intent = Intent(
                            Intent.ACTION_DIAL,
                            "tel:${doctor.phone_number}".toUri()
                        )
                        navController.context.startActivity(intent)
                    } else {
                        permissionLauncher.launch(Manifest.permission.CALL_PHONE)
                    }
                }

                ShortcutProfileIcon(R.drawable.at_solid_full, "Correo") {
                    val recipient = doctor.email
                    val subject = "Consulta médica"
                    val body = """
                        Hola Dr. ${doctor.name} ${doctor.last_name},
                        
                        Me gustaría agendar una consulta para discutir algunos síntomas que he estado experimentando. Por favor, hágame saber su disponibilidad.
                        
                        Gracias,
                        ${user?.name ?: "Paciente"}
                        
                        Saludos.
                    """.trimIndent()
                    val intent = Intent(Intent.ACTION_SENDTO).apply {
                        data = "mailto:".toUri()
                        putExtra(Intent.EXTRA_EMAIL, arrayOf(recipient))
                        putExtra(Intent.EXTRA_SUBJECT, subject)
                        putExtra(Intent.EXTRA_TEXT, body)
                    }

                    try {
                        navController.context.startActivity(intent)
                    } catch (e: Exception) {
                        Toast.makeText(
                            navController.context,
                            "No se pudo abrir la aplicación de correo",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }
}