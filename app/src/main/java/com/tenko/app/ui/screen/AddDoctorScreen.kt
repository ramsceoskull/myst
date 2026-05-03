package com.tenko.app.ui.screen

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tenko.app.R
import com.tenko.app.data.model.Genre
import com.tenko.app.data.model.Speciality
import com.tenko.app.data.serializable.ContactCreate
import com.tenko.app.data.serializable.ContactUpdate
import com.tenko.app.data.view.DoctorViewModel
import com.tenko.app.ui.components.AppTopBar
import com.tenko.app.ui.components.BottomBar
import com.tenko.app.ui.components.FoodOption
import com.tenko.app.ui.components.SpecialityDropdown
import com.tenko.app.ui.components.emailInput
import com.tenko.app.ui.components.nameInput
import com.tenko.app.ui.theme.BackgroundColor
import com.tenko.app.ui.theme.RaisinBlack
import com.tenko.app.ui.theme.White

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun AddDoctorScreen(
    viewModel: DoctorViewModel = viewModel(),
    onBackClick: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var genre by remember { mutableStateOf<Genre?>(null) }
    var speciality by remember { mutableStateOf<Speciality?>(null) }
    var phoneNumber by remember { mutableStateOf("") }
    var street by remember { mutableStateOf("") }
    var city by remember { mutableStateOf("") }
    var avatar by remember { mutableIntStateOf(0) }

    Scaffold(
        topBar = {
            AppTopBar(
                title = "Agregar Doctor",
                onBackClick = onBackClick
            )
        },
        bottomBar = {
            BottomBar(
                onNextStep = {
                    val newContact = ContactCreate(
                        name = name,
                        last_name = lastName,
                        email = email,
                        about = getAbout(speciality),
                        specialty = speciality?.name,
                        genre = avatar
                    )
                    viewModel.createContact(newContact)
                    viewModel.nextStep()
                },
                onPreviousStep = { viewModel.previousStep() },
                onFinalStep = {
                    val updateInfo = ContactUpdate(
                        phone_number = phoneNumber,
                        address = "$street, $city"
                    )

                    viewModel.updateContact(
                        idContact = viewModel.contacts.last().id_contact, // Asumiendo que el nuevo contacto es el último
                        updateInfo,
                        onBackClick
                    )
//                    viewModel.nextStep()
                },
                currentStep = viewModel.currentStep
            )
        },
        containerColor = BackgroundColor
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .background(BackgroundColor)
                .verticalScroll(rememberScrollState())
        ) {
            AnimatedContent(
                targetState = viewModel.currentStep,
                transitionSpec = {
                    slideInHorizontally { it }.togetherWith(slideOutHorizontally { -it })
                },
                label = "sliderPage"
            ) { step ->
                when (step) {
                    0 -> {
                        Column(
                            modifier = Modifier.padding(horizontal = 25.dp, vertical = 30.dp),
                        ) {
                            Text(
                                text = "Datos personales",
                                color = RaisinBlack,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.SemiBold
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            Text(
                                text = "Género del especialista",
                                color = Color.Gray,
                                fontSize = 14.sp,
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                FoodOption(
                                    text = "Femenino",
                                    selected = genre == Genre.FEMALE,
                                    onClick = { genre = Genre.FEMALE }
                                )
                                FoodOption(
                                    text = "Masculino",
                                    selected = genre == Genre.MALE,
                                    onClick = { genre = Genre.MALE }
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            genre?.let {
                                Text(
                                    text = "Vista previa del avatar",
                                    color = Color.Gray,
                                    fontSize = 14.sp
                                )
                                Text(
                                    text = "Selecciona un avatar para tu especialista",
                                    color = Color.LightGray,
                                    fontSize = 12.sp
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceAround
                                ) {
                                    var avatar by remember { mutableStateOf<Int?>(null) }

                                    if (genre == Genre.FEMALE) {
                                        val femaleAvatars = listOf(
                                            R.drawable.doctor0,
                                            R.drawable.doctor1,
                                            R.drawable.doctor2
                                        )

                                        for (icon in femaleAvatars) {
                                            val iconSelected = if(avatar != icon) icon else when (icon) {
                                                R.drawable.doctor0 -> R.drawable.avatar_female_doctor0
                                                R.drawable.doctor1 -> R.drawable.avatar_female_doctor1
                                                R.drawable.doctor2 -> R.drawable.avatar_female_doctor2
                                                else -> R.drawable.profile_picture_placeholder
                                            }

                                            Card(
                                                onClick = { avatar = iconSelected },
                                                colors = CardDefaults.cardColors(White),
                                                content = {
                                                    Image(
                                                        painter = painterResource(iconSelected),
                                                        contentDescription = "Avatar femenino",
                                                        modifier = Modifier.size(100.dp)
                                                    )
                                                }
                                            )
                                        }
                                    } else {
                                        val maleAvatars = listOf(
                                            R.drawable.doctor4,
                                            R.drawable.doctor3,
                                        )

                                        for (icon in maleAvatars) {
                                            val iconSelected = if(avatar != icon) icon else when (icon) {
                                                R.drawable.doctor3 -> R.drawable.avatar_male_doctor3
                                                R.drawable.doctor4 -> R.drawable.avatar_male_doctor4
                                                else -> R.drawable.profile_picture_placeholder
                                            }

                                            Card(
                                                onClick = { avatar = iconSelected },
                                                colors = CardDefaults.cardColors(White),
                                                content = {
                                                    Image(
                                                        painter = painterResource(iconSelected),
                                                        contentDescription = "Avatar femenino",
                                                        modifier = Modifier.size(100.dp)
                                                    )
                                                }
                                            )
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            Text(
                                text = "Nombre del especialista",
                                color = Color.Gray,
                                fontSize = 14.sp,
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            name = nameInput(false, "Primer nombre").first

                            Spacer(modifier = Modifier.height(12.dp))

                            Text(
                                text = "Apellido del especialista",
                                color = Color.Gray,
                                fontSize = 14.sp,
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            lastName = nameInput(false, "Primer apellido").first

                            Spacer(modifier = Modifier.height(12.dp))

                            Text(
                                text = "Correo electrónico del especialista",
                                color = Color.Gray,
                                fontSize = 14.sp,
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            email = emailInput(false)

                            Spacer(modifier = Modifier.height(12.dp))

                            Text(
                                text = "Especialidad",
                                color = Color.Gray,
                                fontSize = 14.sp,
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            SpecialityDropdown(
                                selected = speciality?.displayName ?: "",
                                onSelected = { sp ->
                                    speciality = sp
                                }
                            )
                        }
                    }
                    1 -> {
                        Column(modifier = Modifier.padding(16.dp)) {
                            OutlinedTextField(phoneNumber, { phoneNumber = it }, label = { Text("Teléfono") })
                            OutlinedTextField(street, { street = it }, label = { Text("Calle") })
                            OutlinedTextField(city, { city = it }, label = { Text("Ciudad") })
                        }
                    }
//                2 -> StepResumen(viewModel)
                }
            }
        }
    }
}

fun getAbout(speciality: Speciality?): String {
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
        Speciality.ORTHOPEDIC_SURGEON ->
            "Especialista en el tratamiento de lesiones y enfermedades del sistema musculoesquelético."
        Speciality.ONCOLOGIST ->
            "Experto en el diagnóstico y tratamiento del cáncer."
        Speciality.PULMONOLOGIST ->
            "Especialista en enfermedades del sistema respiratorio, incluyendo pulmones y vías respiratorias."
        Speciality.RHEUMATOLOGIST ->
            "Encargado del diagnóstico y tratamiento de enfermedades reumáticas, como artritis y lupus."
        Speciality.NEPHROLOGIST ->
            "Especialista en el cuidado de los riñones y el tratamiento de enfermedades renales."
        Speciality.HEMATOLOGIST ->
            "Experto en trastornos de la sangre, como anemia y leucemia."
        Speciality.INFECTIOUS_DISEASE_SPECIALIST ->
            "Especialista en el diagnóstico y tratamiento de enfermedades infecciosas, como VIH y tuberculosis."
        Speciality.ALLERGIST ->
            "Encargado del diagnóstico y tratamiento de alergias, como asma y rinitis alérgica."
        Speciality.IMMUNOLOGIST ->
            "Especialista en el sistema inmunológico y enfermedades relacionadas, como inmunodeficiencias y enfermedades autoinmunes."
        else -> TODO()
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun AnimatedImageSwitcher(imageRes: Int) {

    AnimatedContent(
        targetState = imageRes,
        transitionSpec = {
            (fadeIn(tween(300)) + slideInHorizontally { it }) togetherWith
                    (fadeOut(tween(300)) + slideOutHorizontally { -it })
        }
    ) { targetImage ->
        Image(
            painter = painterResource(targetImage),
            contentDescription = null,
            modifier = Modifier.size(100.dp)
        )
    }
}