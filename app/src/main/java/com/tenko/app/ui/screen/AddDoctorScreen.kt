package com.tenko.app.ui.screen

import android.widget.Toast
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tenko.app.R
import com.tenko.app.data.api.getAddressByCP
import com.tenko.app.data.api.loadCountries
import com.tenko.app.data.model.Address
import com.tenko.app.data.model.Genre
import com.tenko.app.data.model.Speciality
import com.tenko.app.data.serializable.ContactCreate
import com.tenko.app.data.view.AddressViewModel
import com.tenko.app.data.view.DoctorViewModel
import com.tenko.app.regex.formatAsYouType
import com.tenko.app.regex.isValidNumber
import com.tenko.app.ui.components.AddressItem
import com.tenko.app.ui.components.AppTopBar
import com.tenko.app.ui.components.AvatarSelector
import com.tenko.app.ui.components.BottomBar
import com.tenko.app.ui.components.CountryDropdown
import com.tenko.app.ui.components.SpecialityDropdown
import com.tenko.app.ui.components.SquaredOptionSelector
import com.tenko.app.ui.components.blockedInput
import com.tenko.app.ui.components.emailInput
import com.tenko.app.ui.components.generalInput
import com.tenko.app.ui.components.nameInput
import com.tenko.app.ui.components.numberInput
import com.tenko.app.ui.theme.AntiFlashWhite
import com.tenko.app.ui.theme.BackgroundColor
import com.tenko.app.ui.theme.PompAndPower
import com.tenko.app.ui.theme.RaisinBlack
import com.tenko.app.ui.theme.StarsLove
import com.tenko.app.ui.theme.SweetGrey
import com.tenko.app.ui.theme.Tekhelet
import com.tenko.app.ui.theme.White
import kotlinx.coroutines.launch

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun AddDoctorScreen(
    viewModel: DoctorViewModel = viewModel(),
    addressViewModel: AddressViewModel = viewModel(),
    onBackClick: () -> Unit,
) {
    var screen by remember { mutableStateOf("list") }
    var selectedAddress by remember { mutableStateOf<Address?>(null) }

    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var avatar by remember { mutableStateOf<Int?>(null) }
    var genre by remember { mutableStateOf<Genre?>(null) }
    var speciality by remember { mutableStateOf<Speciality?>(null) }

    var label by remember { mutableStateOf(selectedAddress?.name ?: "") }
    var street by remember { mutableStateOf(selectedAddress?.street ?: "") }
    var city by remember { mutableStateOf(selectedAddress?.city ?: "") }
    var state by remember { mutableStateOf(selectedAddress?.state ?: "") }
    var zipCode by remember { mutableStateOf(selectedAddress?.zipCode ?: "") }
    var phoneNumber by remember { mutableStateOf(selectedAddress?.phoneNumber ?: "") }

    var nameError by remember { mutableStateOf<String?>(null) }
    var streetError by remember { mutableStateOf<String?>(null) }
    var cityError by remember { mutableStateOf<String?>(null) }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val scrollState = rememberScrollState()
    val context = LocalContext.current

    LaunchedEffect(city, state) {
        if (zipCode.length == 5 && city.isNotBlank() && state.isNotBlank()) {
            kotlinx.coroutines.delay(200)
            scrollState.animateScrollTo(scrollState.maxValue)
        }
    }

    Scaffold(
        topBar = {
            AppTopBar(
                title = if(screen == "form") "Consultorio" else "Agregar Doctor",
                onBackClick = onBackClick
            )
        },
        bottomBar = {
            if(screen != "form") {
                BottomBar(
                    onNextStep = {
                        viewModel.nextStep()
                        /*if(genre != null && avatar != null && name.isNotBlank() && lastName.isNotBlank() && email.isNotBlank() && speciality != null) {
                            if(!isValidEmail(email))
                                Toast.makeText(context, "Correo electrónico inválido", Toast.LENGTH_SHORT).show()
                            else
                                viewModel.nextStep()
                        } else {
                            Toast.makeText(context, "Por favor completa todos los campos", Toast.LENGTH_SHORT).show()
                        }*/
                    },
                    onPreviousStep = { viewModel.previousStep() },
                    onFinalStep = {
                        val newContact = ContactCreate(
                            name = name,
                            last_name = lastName,
                            email = email,
                            about = getAbout(speciality),
                            specialty = speciality?.displayName,
                            genre = avatar,
                            phone_number = phoneNumber,
                            address = "$street, $city"
                        )
                        viewModel.createContact(newContact)
                        Toast.makeText(context, "Doctor agregado exitosamente", Toast.LENGTH_SHORT).show()
//                    viewModel.nextStep()
                    },
                    currentStep = viewModel.currentStep,
                )
            } else {
                Surface(
                    shape = RoundedCornerShape(
                        topStart = 12.dp,
                        topEnd = 12.dp
                    ),
                    shadowElevation = 8.dp
                ) {
                    NavigationBar(
                        modifier = Modifier.shadow(
                            elevation = 8.dp,
                            shape = RoundedCornerShape(
                                topStart = 12.dp,
                                topEnd = 12.dp
                            ),
                            ambientColor = Color.Black,
                            spotColor = Color.Black,
                            clip = false
                        ),
                        containerColor = White,
                        content = {
                            TextButton(
                                onClick = {
                                    var isFormValid = true
                                    if (label.isBlank()) {
                                        nameError = "Label is required"
                                        isFormValid = false
                                    }
                                    if (street.length < 5) {
                                        streetError = "Street is too short"
                                        isFormValid = false
                                    }
                                    if (zipCode.isBlank() || zipCode.length != 5) {
                                        cityError = "Código postal inválido"
                                        isFormValid = false
                                    }
                                    if(!isFormValid) {
                                        Toast.makeText(context, "Por favor corrige los errores antes de guardar", Toast.LENGTH_SHORT).show()
                                        return@TextButton
                                    }
                                    val address = Address(
                                        id = selectedAddress?.id ?: -1,
                                        name = label,
                                        street = street,
                                        city = city,
                                        state = state,
                                        zipCode = zipCode,
                                        phoneNumber = phoneNumber,
                                    )
                                    if (selectedAddress == null) {
                                        addressViewModel.addAddress(address)
                                    } else {
                                        addressViewModel.updateAddress(selectedAddress!!)
                                    }
                                    screen = "list"
                                },
                                modifier = Modifier
                                    .padding(16.dp)
                                    .fillMaxWidth()
                                    .height(66.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.textButtonColors(
                                    containerColor = Tekhelet,
                                    contentColor = White
                                ),
                                content = {
                                    Text(
                                        text = "Guardar dirección",
                                        color = White,
                                        fontSize = 25.sp,
                                        fontFamily = StarsLove,
                                        fontWeight = FontWeight.ExtraLight,
                                        modifier = Modifier.offset(y = 4.dp)
                                    )
                                }
                            )
                        }
                    )
                }
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = BackgroundColor
    ) { paddingValues ->
        AnimatedContent(
            targetState = viewModel.currentStep,
            transitionSpec = { slideInHorizontally { it }.togetherWith(slideOutHorizontally { -it }) },
            label = "sliderPage"
        ) { step ->
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .background(BackgroundColor)
                    .verticalScroll(scrollState)
                    .padding(horizontal = 25.dp, vertical = 30.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                when (step) {
                    0 -> {
                        Text(
                            text = "Datos personales",
                            color = RaisinBlack,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.SemiBold
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "Género del especialista",
                            color = Color.Gray,
                            fontSize = 14.sp,
                        )
                        SquaredOptionSelector(
                            options = listOf(Genre.FEMALE, Genre.MALE),
                            selectedOption = genre,
                            onOptionSelected = { genre = it },
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        AnimatedVisibility(
                            visible = genre != null,
                            enter = fadeIn() + expandVertically(),
                            exit = fadeOut() + shrinkVertically()
                        ) {
                            genre?.let { currentGenre ->
                                AvatarSelector(
                                    avatar = avatar,
                                    genre = currentGenre,
                                    onAvatarChange = { avatar = it }
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "Nombre del especialista",
                            color = Color.Gray,
                            fontSize = 14.sp,
                        )
                        name = nameInput(false, "Primer nombre", name).first

                        Text(
                            text = "Apellido del especialista",
                            color = Color.Gray,
                            fontSize = 14.sp,
                        )
                        lastName = nameInput(false, "Primer apellido", lastName).first

                        Text(
                            text = "Correo electrónico del especialista",
                            color = Color.Gray,
                            fontSize = 14.sp,
                        )
                        email = emailInput(false, email)

                        Text(
                            text = "Especialidad",
                            color = Color.Gray,
                            fontSize = 14.sp,
                        )
                        SpecialityDropdown(
                            selected = speciality?.displayName ?: "",
                            onSelected = { sp ->
                                speciality = sp
                            }
                        )
                    }

                    1 -> {
                        when (screen) {
                            "list" -> {
                                Text(
                                    text = "Dirección del consultorio",
                                    color = RaisinBlack,
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.SemiBold
                                )

                                Spacer(modifier = Modifier.height(6.dp))

                                Text(
                                    text = "Direcciones guardadas",
                                    color = Color.Gray,
                                    fontSize = 14.sp
                                )
                                Text(
                                    text = "Selecciona una dirección existente o agrega una nueva para el consultorio.",
                                    color = Color.LightGray,
                                    fontSize = 12.sp
                                )

                                Spacer(modifier = Modifier.height(6.dp))

                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            selectedAddress = null
                                            screen = "form"
                                        },
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(45.dp)
                                            .background(
                                                color = PompAndPower.copy(0.5f),
                                                shape = CircleShape
                                            ),
                                        contentAlignment = Alignment.Center,
                                        content = {
                                            Icon(
                                                painter = painterResource(id = R.drawable.plus_solid_full),
                                                contentDescription = "Agregar",
                                                tint = White,
                                                modifier = Modifier.size(25.dp)
                                            )
                                        }
                                    )

                                    Text(
                                        text = "Agregar nueva dirección",
                                        color = RaisinBlack,
                                        fontSize = 14.sp,
                                        modifier = Modifier
                                            .padding(start = 12.dp)
                                            .weight(1f)
                                    )

                                    Icon(
                                        painter = painterResource(id = R.drawable.chevron_right_solid_full),
                                        contentDescription = "Ir a agregar dirección",
                                        tint = Color.Gray,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }

                                LazyColumn(modifier = Modifier.weight(1f)) {
                                    items(addressViewModel.addresses, key = { it.id }) { address ->
                                        AddressItem(
                                            address = address,
                                            onSelect = { addressViewModel.selectAddress(address.id) },
                                            onEdit = {
                                                selectedAddress = address
                                                screen = "form"
                                            }
                                        )
                                    }
                                }
                            }

                            "form" -> {
                                val countries = loadCountries(context)
                                var selected by remember { mutableStateOf(countries.firstOrNull { it.iso == "MX" } ?: countries.first()) }

                                var textFieldValue by remember { mutableStateOf(TextFieldValue("")) }
                                var isPhoneValid by remember { mutableStateOf(true) }

                                Text(
                                    text = "Datos del consultorio",
                                    color = RaisinBlack,
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.SemiBold
                                )

                                Spacer(modifier = Modifier.height(6.dp))

                                Text(
                                    text = "Nombre del consultorio",
                                    color = Color.Gray,
                                    fontSize = 14.sp
                                )
                                label = generalInput("Ej: Hospital San Javier", label, R.drawable.hospital_regular_full)

                                Text(
                                    text = "Teléfono",
                                    color = Color.Gray,
                                    fontSize = 14.sp
                                )
                                Text(
                                    text = "Ingresa el número de teléfono del consultorio.",
                                    color = Color.LightGray,
                                    fontSize = 12.sp
                                )
                                Row(
                                    modifier = Modifier.fillMaxWidth().height(66.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    CountryDropdown(
                                        countries = countries,
                                        selected = selected,
                                        onSelect = {
                                            selected = it
                                            // Si el país cambia, reseteamos el número para evitar confusiones
                                            textFieldValue = TextFieldValue("")
                                            phoneNumber = ""
                                        }
                                    )

                                    OutlinedTextField(
                                        value = textFieldValue,
                                        onValueChange = { input ->
                                            // Solo números
                                            val digits = input.text.filter { it.isDigit() }
                                            if(digits.length <= 10) {
                                                phoneNumber = digits

                                                // Formatear con el código del país
                                                val formatted = formatAsYouType(raw = digits, regionCode = selected.iso)
                                                isPhoneValid = isValidNumber(digits, selected.iso)
                                                textFieldValue = TextFieldValue(text = formatted, selection = TextRange(formatted.length)) // Mantener el cursor al final
                                            }
                                        },
                                        placeholder = { Text("Ej: 3312345678") },
                                        singleLine = true,
                                        keyboardOptions = KeyboardOptions(
                                            keyboardType = KeyboardType.Phone
                                        ),
                                        isError = !isPhoneValid,
                                        shape = RoundedCornerShape(12.dp),
                                        trailingIcon = {
                                            Icon(
                                                modifier = Modifier.size(30.dp).padding(end = 4.dp),
                                                painter = painterResource(id = R.drawable.phone_solid_full),
                                                contentDescription = "Icono de teléfono"
                                            )
                                        },
                                        colors = OutlinedTextFieldDefaults.colors(
                                            unfocusedContainerColor = AntiFlashWhite,
                                            focusedBorderColor = PompAndPower,
                                            unfocusedBorderColor = Color.Transparent,
                                            focusedTrailingIconColor = PompAndPower,
                                            unfocusedTrailingIconColor = SweetGrey,
                                            unfocusedPlaceholderColor = Color.Gray,
                                        ),
                                        modifier = Modifier.fillMaxHeight()
                                    )
                                }
                                if(phoneNumber.isNotEmpty())
                                    Text(
                                        text = "Número ${if(isPhoneValid) "completo" else "incompleto"}: ${selected.code} $phoneNumber",
                                        color = if(isPhoneValid) Color.LightGray else MaterialTheme.colorScheme.error,
                                        fontSize = 12.sp
                                    )

                                Spacer(modifier = Modifier.height(6.dp))

                                Text(
                                    text = "Dirección del consultorio",
                                    color = Color.Gray,
                                    fontSize = 14.sp
                                )
                                Text(
                                    text = "Ingresa la calle y número donde se encuentra el consultorio.",
                                    color = Color.LightGray,
                                    fontSize = 12.sp
                                )
                                street = generalInput("Monte Everest 1081", street)

                                Text(
                                    text = "Ingresa el código postal del consultorio.",
                                    color = Color.LightGray,
                                    fontSize = 12.sp
                                )
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    zipCode = numberInput("Ej: 44340", zipCode, Modifier.weight(1f))

                                    TextButton(
                                        onClick = {
                                            if(zipCode.length == 5) {
                                                scope.launch {
                                                    val result = getAddressByCP(zipCode)
                                                    state = result?.estado ?: "No encontrado"
                                                    city = result?.municipio ?: "No encontrado"
                                                }
                                            } else
                                                Toast.makeText(context, "Por favor ingresa un código postal válido", Toast.LENGTH_SHORT).show()
                                        },
                                        modifier = Modifier.height(66.dp),
                                        shape = RoundedCornerShape(12.dp),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = PompAndPower,
                                            contentColor = White
                                        ),
                                        content = { Text("Buscar") }
                                    )
                                }

                                AnimatedVisibility(
                                    visible = zipCode.length == 5 && city.isNotBlank() && state.isNotBlank(),
                                    enter = fadeIn() + expandVertically(),
                                    exit = fadeOut() + shrinkVertically()
                                ) {
                                    Column (
                                        modifier = Modifier
                                            .padding(top = 6.dp)
                                            .fillMaxWidth(),
                                        verticalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Text(
                                            text = "Ubicación encontrada",
                                            color = Color.Gray,
                                            fontSize = 14.sp
                                        )
                                        Text(
                                            text = "Estos son los datos de la ubicación encontrada.",
                                            color = Color.LightGray,
                                            fontSize = 12.sp
                                        )
                                        Row (
                                            modifier = Modifier.fillMaxWidth(),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                                        ) {
                                            state = blockedInput("Estado", state, Modifier.weight(1f))
                                            city = blockedInput("Ciudad", city, Modifier.weight(1f))
                                        }
                                    }
                                }

                                /*OutlinedTextField(
                                    value = label,
                                    onValueChange = { label = it; nameError = null },
                                    isError = nameError != null,
                                    supportingText = { nameError?.let { Text(it) } },
                                    label = { Text("Label (Home, Office)") }
                                )*/

                                Spacer(modifier = Modifier.height(16.dp))



                                // 👇 SOLO EN EDITAR
                                selectedAddress?.let { address ->
                                    Spacer(modifier = Modifier.height(8.dp))

                                    OutlinedButton(
                                        onClick = {
                                            addressViewModel.deleteAddress(address.id)

                                            scope.launch {
                                                val result = snackbarHostState.showSnackbar(
                                                    message = "Dirección eliminada",
                                                    actionLabel = "Deshacer",
                                                )

                                                if (result == SnackbarResult.ActionPerformed) {
                                                    addressViewModel.addAddress(address)
                                                }
                                            }

                                            screen = "list"
                                        },
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text("Delete", color = Color.Red)
                                    }
                                }
                            }
                        }
                    }

                    2 -> {
                        Text(
                            text = "Resumen y confirmación",
                            color = RaisinBlack,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.SemiBold
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = "Vista previa",
                            color = Color.Gray,
                            fontSize = 14.sp
                        )
                        Text(
                            text = "Revisa que toda la información sea correcta antes de agregar al doctor a tu lista de contactos.",
                            color = Color.LightGray,
                            fontSize = 12.sp
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Card(
                            modifier = Modifier
                                .fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            elevation = CardDefaults.cardElevation(8.dp),
                            colors = CardDefaults.cardColors(containerColor = Tekhelet),
                            content = {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(110.dp)
                                            .padding(horizontal = 20.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = "$name $lastName",
                                                fontSize = 18.sp,
                                                fontWeight = FontWeight.SemiBold,
                                                color = White
                                            )

                                            Spacer(modifier = Modifier.height(4.dp))

                                            Text(
                                                text = speciality?.displayName ?: "Sin especialidad",
                                                fontSize = 14.sp,
                                                color = White.copy(0.7f)
                                            )
                                        }

                                        Box(
                                            modifier = Modifier
                                                .padding(top = 12.dp)
                                                .fillMaxHeight()
                                                .width(1.dp)
                                                .background(White.copy(0.5f))
                                        )

                                        Spacer(modifier = Modifier.width(15.dp))

                                        avatar?.let {
                                            Image(
                                                painter = painterResource(id = it),
                                                contentDescription = "Doctor Avatar",
                                                modifier = Modifier
                                                    .fillMaxHeight()
                                                    .padding(top = 12.dp),
                                                contentScale = ContentScale.FillHeight
                                            )
                                        }
                                    }
                            }
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        HorizontalDivider(color = Tekhelet.copy(0.6f), thickness = 2.dp)

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = "Correo electrónico",
                            fontSize = 14.sp,
                            color = Color.Gray
                        )
                        Text(
                            text = email,
                            fontSize = 12.sp,
                            color = Color.LightGray,
                            modifier = Modifier.padding(start = 16.dp, bottom = 8.dp)
                        )
                        Text(
                            text = "Especializado en...",
                            fontSize = 14.sp,
                            color = Color.Gray
                        )
                        Text(
                            text = getAbout(speciality),
                            fontSize = 12.sp,
                            color = Color.LightGray,
                            modifier = Modifier.padding(start = 16.dp, bottom = 8.dp)
                        )
                    }
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
            "Experto en el diagnóstico and tratamiento del cáncer."
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
        else -> "Información no disponible para esta especialidad."
    }
}
