package com.tenko.myst.ui.screen

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.with
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tenko.myst.R
import com.tenko.myst.data.model.Genre
import com.tenko.myst.data.model.Speciality
import com.tenko.myst.data.serializable.ContactCreate
import com.tenko.myst.data.serializable.ContactUpdate
import com.tenko.myst.data.view.DoctorViewModel

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun AddDoctorScreen(viewModel: DoctorViewModel = viewModel(), onBack: () -> Unit) {
    Column {
        FormProgress(viewModel.currentStep)

        AnimatedContent(
            targetState = viewModel.currentStep,
            transitionSpec = {
                slideInHorizontally { it } with slideOutHorizontally { -it }
            },
            label = ""
        ) { step ->

            when (step) {
                0 -> StepBasicInfo(viewModel)
                1 -> StepContact(viewModel, onBack)
//                2 -> StepResumen(viewModel)
            }
        }
    }
}

@Composable
fun FormProgress(currentStep: Int, totalSteps: Int = 2) {
    val progress = (currentStep + 1) / totalSteps.toFloat()

    Column {
        LinearProgressIndicator(progress = progress)
        Spacer(modifier = Modifier.height(4.dp))
        Text("Paso ${currentStep + 1} de $totalSteps")
    }
}

@Composable
fun StepBasicInfo(viewModel: DoctorViewModel) {
    var name by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var genre by remember { mutableStateOf(Genre.FEMALE) }
    var specialty by remember { mutableStateOf(Speciality.GYNECOLOGIST) }

    val avatarPreview = getRandomAvatar(genre)
    val acercaPreview = getAbout(specialty)

    Column(modifier = Modifier.padding(16.dp)) {
        OutlinedTextField(name, { name = it }, label = { Text("Nombre") })
        OutlinedTextField(lastName, { lastName = it }, label = { Text("Apellido") })
        OutlinedTextField(email, { email = it }, label = { Text("Email") })

        Text("Género: $genre")
        Text("Especialidad: $specialty")
        Card {
            Column(Modifier.padding(12.dp)) {

                Text("Vista previa", style = MaterialTheme.typography.titleMedium)

                Spacer(modifier = Modifier.height(8.dp))

                Text("Avatar asignado:")
                Image(painter = painterResource(avatarPreview), contentDescription = "Avatar del médico", modifier = Modifier.size(100.dp))

                Spacer(modifier = Modifier.height(8.dp))

                Text("Acerca de mí:")
                Text(acercaPreview)
            }
        }

        // Aquí puedes usar Dropdowns para genero y especialidad

        Button(onClick = {
            val newContact = ContactCreate(
                name = name,
                last_name = lastName,
                email = email,
                about = getAbout(specialty),
                specialty = specialty.name,
            )
            viewModel.createContact(newContact)
            viewModel.nextStep()
        }) {
            Text("Siguiente")
        }
    }
}

@Composable
fun StepContact(viewModel: DoctorViewModel, onBack: () -> Unit) {
    var phoneNumber by remember { mutableStateOf("") }
    var street by remember { mutableStateOf("") }
    var city by remember { mutableStateOf("") }

    Column(modifier = Modifier.padding(16.dp)) {
        OutlinedTextField(phoneNumber, { phoneNumber = it }, label = { Text("Teléfono") })
        OutlinedTextField(street, { street = it }, label = { Text("Calle") })
        OutlinedTextField(city, { city = it }, label = { Text("Ciudad") })

        Row {
            Button(onClick = { viewModel.previousStep() }) {
                Text("Atrás")
            }

            Spacer(modifier = Modifier.width(8.dp))

            Button(onClick = {
                val updateInfo = ContactUpdate(
                    phone_number = phoneNumber,
                    address = "$street, $city"
                )
                viewModel.updateContact(
                    idContact = viewModel.contacts.last().id_contact, // Asumiendo que el nuevo contacto es el último
                    updateInfo,
                    onBack
                )
//                viewModel.nextStep()
            }) {
                Text("Finalizar")
            }
        }
    }
}

fun getRandomAvatar(genre: Genre): Int {
    val femaleAvatar = listOf(
        R.drawable.doctor0,
        R.drawable.doctor1,
        R.drawable.doctor4
    )

    val maleAvatar = listOf(
        R.drawable.doctor2,
        R.drawable.doctor3,
    )

    return if (genre == Genre.FEMALE) femaleAvatar.random() else maleAvatar.random()
}

fun getAbout(speciality: Speciality): String {
    return when (speciality) {
        Speciality.CARDIOLOGIST ->
            "Especialista en el diagnóstico y tratamiento de enfermedades del corazón."
        Speciality.PEDIATRICIAN ->
            "Encargado del cuidado integral de niños y adolescentes."
        Speciality.DERMATOLOGIST ->
            "Especialista en enfermedades de la piel, cabello y uñas."
        Speciality.NEUROLOGIST ->
            "Experto en trastornos del sistema nervioso, incluyendo cerebro y médula espinal."
        Speciality.GYNECOLOGIST ->
            "Especialista en salud femenina, embarazo y parto."
        Speciality.ENDOCRINOLOGIST ->
            "Encargado del diagnóstico y tratamiento de trastornos hormonales y metabólicos."
        else -> TODO()
    }
}