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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
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
import com.tenko.app.ui.components.DoctorCard
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
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun AddDoctorScreen(
    viewModel: DoctorViewModel = viewModel(),
    addressViewModel: AddressViewModel = viewModel(),
    onBackClick: () -> Unit,
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val scrollState = rememberScrollState()
    val context = LocalContext.current

    var screen by remember { mutableStateOf("list") }
    var selectedAddress by remember { mutableStateOf<Address?>(null) }

    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var avatar by remember { mutableStateOf<Int?>(null) }
    var genreTemp by remember { mutableStateOf<Int?>(null) }
    var genre by remember { mutableStateOf<Genre?>(null) }
    var speciality by remember { mutableStateOf<Speciality?>(null) }

    var label by remember { mutableStateOf(selectedAddress?.name ?: "") }
    var street by remember { mutableStateOf(selectedAddress?.street ?: "") }
    var city by remember { mutableStateOf(selectedAddress?.city ?: "") }
    var state by remember { mutableStateOf(selectedAddress?.state ?: "") }
    var zipCode by remember { mutableStateOf(selectedAddress?.zipCode ?: "") }
    var neighborhood by remember { mutableStateOf(selectedAddress?.neighborhood ?: "") }
    var phoneNumber by remember { mutableStateOf(selectedAddress?.phoneNumber ?: "") }

    val countries = loadCountries(context)
    var formatted by remember { mutableStateOf("") }
    var textFieldValue by remember { mutableStateOf(TextFieldValue("")) }
    var selected by remember { mutableStateOf(countries.firstOrNull { it.iso == "MX" } ?: countries.first()) }

    LaunchedEffect(city, state) {
        if (zipCode.length == 5 && city.isNotBlank() && state.isNotBlank() && neighborhood.isNotBlank()) {
            delay(200)
            scrollState.animateScrollTo(scrollState.maxValue)
        }
    }

    Scaffold(
        topBar = { AppTopBar(if(screen == "form") "Mi clínica" else "Agregar Doctor") },
        bottomBar = {
            if(screen != "form") {
                BottomBar(
                    onNextStep = {
                        when(viewModel.currentStep) {
                            0 -> {
                                if(genre != null && avatar != null && name.isNotBlank() && lastName.isNotBlank() && email.isNotBlank() && speciality != null) {
                                    viewModel.nextStep()
                                } else {
                                    Toast.makeText(context, "Por favor completa todos los campos", Toast.LENGTH_SHORT).show()
                                }
//                                viewModel.nextStep()
                            }
                            1 -> {
                                if(selectedAddress != null) {
                                    viewModel.nextStep()
                                } else {
                                    Toast.makeText(context, "Por favor selecciona o agrega una dirección para el consultorio", Toast.LENGTH_SHORT).show()
                                }
                            }
                            2 -> {
                                val newContact = ContactCreate(
                                    name = name,
                                    last_name = lastName,
                                    email = email,
                                    about = getAbout(speciality),
                                    specialty = speciality?.displayName,
                                    genre = avatar,
                                    phone_number = phoneNumber,
                                    address = selectedAddress?.let { "${it.name},${it.street},${it.neighborhood},${it.zipCode},${it.city},${it.state}" }
                                )
                                viewModel.createContact(newContact, context, onBackClick)
                            }
                        }
                    },
                    onPreviousStep = { if(viewModel.currentStep > 0) viewModel.previousStep() else onBackClick() },
                    currentStep = viewModel.currentStep,
                    totalSteps = 3
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
                                    if(label.isBlank() || street.isBlank() || city.isBlank() || state.isBlank() || zipCode.length != 5 || phoneNumber.length != 10) {
                                        Toast.makeText(context, "Por favor completa todos los campos correctamente", Toast.LENGTH_SHORT).show()
                                        return@TextButton
                                    }
                                    addressViewModel.unselectAllAddresses()
                                    val address = Address(
                                        id = selectedAddress?.id ?: -1,
                                        name = label,
                                        street = street,
                                        city = city,
                                        state = state,
                                        zipCode = zipCode,
                                        phoneNumber = phoneNumber,
                                        neighborhood = neighborhood,
                                        isSelected = true
                                    )
                                    if (selectedAddress == null)
                                        addressViewModel.addAddress(address)
                                    else
                                        addressViewModel.updateAddress(selectedAddress!!)

                                    selectedAddress = address
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
            ) {
                when (step) {
                    0 -> {
                        Column(
                            modifier = Modifier.padding(horizontal = 25.dp, vertical = 30.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
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
                                        avatar = genreTemp,
                                        genre = currentGenre,
                                        onAvatarChange = { genreTemp = it }
                                    )
                                    avatar = when(genreTemp) {
                                        R.drawable.doctor0 -> 0
                                        R.drawable.doctor1 -> 1
                                        R.drawable.doctor2 -> 2
                                        R.drawable.doctor3 -> 3
                                        R.drawable.doctor4 -> 4
                                        else -> null
                                    }
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
                    }

                    1 -> {
                        when (screen) {
                            "list" -> {
                                if(selectedAddress?.phoneNumber != null) {
                                    formatted = formatAsYouType(raw = selectedAddress!!.phoneNumber, regionCode = selected.iso)
                                    textFieldValue = TextFieldValue(text = formatted, selection = TextRange(formatted.length))
                                }
                                Column(
                                    modifier = Modifier.padding(start = 25.dp, end = 25.dp, top = 30.dp),
                                    verticalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
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
                                }
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            label = ""
                                            street = ""
                                            city = ""
                                            state = ""
                                            zipCode = ""
                                            phoneNumber = ""
                                            selectedAddress = null
                                            screen = "form"
                                            textFieldValue = TextFieldValue("")
                                        }
                                        .padding(vertical = 20.dp, horizontal = 25.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(50.dp)
                                            .background(
                                                color = PompAndPower.copy(0.3f),
                                                shape = CircleShape
                                            ),
                                        contentAlignment = Alignment.Center,
                                        content = {
                                            Icon(
                                                painter = painterResource(id = R.drawable.plus_solid_full),
                                                contentDescription = "Agregar",
                                                tint = PompAndPower,
                                                modifier = Modifier.size(20.dp)
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
                                        tint = Color.LightGray,
                                        modifier = Modifier.size(25.dp)
                                    )
                                }

                                HorizontalDivider(
                                    modifier = Modifier.padding(horizontal = 40.dp),
                                    thickness = 2.dp,
                                    color = AntiFlashWhite
                                )

                                addressViewModel.addresses.forEach { address ->
                                    println("Dirección: $address")
                                    AddressItem(
                                        address = address,
                                        onSelect = {
                                            selectedAddress = address
                                            addressViewModel.selectAddress(address.id)
                                        },
                                        onEdit = {
                                            selectedAddress = address
                                            screen = "form"
                                        }
                                    )
                                }
                            }

                            "form" -> {
                                Column(
                                    modifier = Modifier.padding(horizontal = 25.dp, vertical = 30.dp),
                                    verticalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
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
                                    label = generalInput("Ej: Hospital San Javier", selectedAddress?.name ?: "", R.drawable.hospital_regular_full)

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
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(66.dp),
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
                                                    formatted = formatAsYouType(raw = digits, regionCode = selected.iso)
                                                    isPhoneValid = isValidNumber(number = digits, regionCode = selected.iso)
                                                    textFieldValue = TextFieldValue(text = formatted, selection = TextRange(formatted.length)) // Mantener el cursor al final
                                                }
                                            },
                                            placeholder = { Text("Ej: 33 3225 8014", fontSize = 14.sp) },
                                            singleLine = true,
                                            keyboardOptions = KeyboardOptions(
                                                keyboardType = KeyboardType.Phone
                                            ),
                                            isError = !isPhoneValid,
                                            shape = RoundedCornerShape(12.dp),
                                            trailingIcon = {
                                                Icon(
                                                    modifier = Modifier
                                                        .size(30.dp)
                                                        .padding(end = 4.dp),
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
                                            text = "Número ${if(isPhoneValid) "completo" else "incompleto"}: ${selected.code} $formatted",
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
                                    street = generalInput("Monte Everest 1081", selectedAddress?.street ?: "")

                                    Text(
                                        text = "Ingresa el código postal del consultorio.",
                                        color = Color.LightGray,
                                        fontSize = 12.sp
                                    )
                                    Row(
                                        verticalAlignment = Alignment.Top,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            zipCode = numberInput("Ej: 44340", selectedAddress?.zipCode ?: "")
                                            Text(
                                                text = "Debe contener exactamente 5 dígitos",
                                                color = if(zipCode.isEmpty() || zipCode.length == 5 || selectedAddress?.zipCode?.length == 5) Color.LightGray else MaterialTheme.colorScheme.error,
                                                fontSize = 12.sp
                                            )
                                        }

                                        IconButton(
                                            onClick = {
                                                if(zipCode.length == 5) {
                                                    scope.launch {
                                                        val result = getAddressByCP(zipCode)
                                                        if(result == null)
                                                            Toast.makeText(context, "No se encontró información para el código postal ingresado", Toast.LENGTH_SHORT).show()
                                                        else {
                                                            state = result.estado
                                                            city = result.municipio
                                                            neighborhood = result.asentamientos.firstOrNull() ?: ""
                                                        }
                                                    }
                                                } else
                                                    Toast.makeText(context, "Por favor ingresa un código postal válido", Toast.LENGTH_SHORT).show()
                                            },
                                            modifier = Modifier.size(66.dp),
                                            shape = RoundedCornerShape(12.dp),
                                            colors = IconButtonDefaults.iconButtonColors(
                                                containerColor = Tekhelet,
                                                contentColor = White
                                            ),
                                            content = {
                                                Icon(
                                                    painter = painterResource(id = R.drawable.magnifying_glass_solid_full),
                                                    contentDescription = "Search icon",
                                                    modifier = Modifier.size(30.dp)
                                                )
                                            }
                                        )
                                    }

                                    AnimatedVisibility(
                                        visible = zipCode.length == 5 && city.isNotBlank() && state.isNotBlank() && neighborhood.isNotBlank(),
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
                                                text = "Código postal encontrado",
                                                color = Color.Gray,
                                                fontSize = 14.sp
                                            )
                                            Text(
                                                text = "Estos son los datos para el código postal ingresado.",
                                                color = Color.LightGray,
                                                fontSize = 12.sp
                                            )
                                            Row (
                                                modifier = Modifier.fillMaxWidth(),
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                                            ) {
                                                city = blockedInput("Ciudad", city, Modifier.weight(1f))
                                                state = blockedInput("Estado", state, Modifier.weight(1f))
                                            }
                                            neighborhood = blockedInput("Colonia", neighborhood, Modifier.fillMaxWidth())
                                        }
                                    }


                                    // SOLO EN EDITAR
                                    selectedAddress?.let { address ->
                                        Spacer(modifier = Modifier.height(8.dp))

                                        OutlinedButton(
                                            onClick = {
                                                addressViewModel.unselectAllAddresses()
                                                selectedAddress = null
                                                screen = "list"
                                            },
                                            modifier = Modifier.fillMaxWidth(),
                                            shape = RoundedCornerShape(12.dp),
                                            content = {
                                                Row(
                                                    modifier = Modifier.padding(12.dp),
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    horizontalArrangement = Arrangement.Center,
                                                ) {
                                                    Icon(
                                                        painter = painterResource(id = R.drawable.reply_solid_full),
                                                        contentDescription = "Flecha hacia atrás",
                                                        tint = Color.Gray,
                                                        modifier = Modifier.size(25.dp)
                                                    )

                                                    Spacer(modifier = Modifier.width(8.dp))

                                                    Text(
                                                        text = "Cancelar edición",
                                                        fontSize = 14.sp,
                                                        color = Color.Gray,
                                                        fontWeight = FontWeight.SemiBold,
                                                    )
                                                }
                                            }
                                        )

                                        HorizontalDivider(
                                            modifier = Modifier.padding(vertical = 8.dp, horizontal = 40.dp),
                                            thickness = 2.dp,
                                            color = AntiFlashWhite
                                        )

                                        OutlinedButton(
                                            onClick = {
                                                addressViewModel.deleteAddress(address.id)
                                                scope.launch {
                                                    val result = snackbarHostState.showSnackbar(
                                                        message = "Dirección eliminada",
                                                        actionLabel = "Deshacer",
                                                        duration = SnackbarDuration.Short
                                                    )

                                                    if (result == SnackbarResult.ActionPerformed) {
                                                        addressViewModel.addAddress(address.copy(isSelected = false))
                                                        addressViewModel.unselectAllAddresses()
                                                    }
                                                }

                                                selectedAddress = null
                                                screen = "list"
                                            },
                                            modifier = Modifier.fillMaxWidth(),
                                            shape = RoundedCornerShape(12.dp),
                                            content = {
                                                Row(
                                                    modifier = Modifier.padding(12.dp),
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    horizontalArrangement = Arrangement.Center,
                                                ) {
                                                    Icon(
                                                        painter = painterResource(id = R.drawable.trash_can_regular_full),
                                                        contentDescription = "Eliminar",
                                                        tint = MaterialTheme.colorScheme.error,
                                                        modifier = Modifier.size(25.dp)
                                                    )

                                                    Spacer(modifier = Modifier.width(8.dp))

                                                    Text(
                                                        text = "Eliminar dirección",
                                                        fontSize = 14.sp,
                                                        color = MaterialTheme.colorScheme.error,
                                                        fontWeight = FontWeight.SemiBold,
                                                    )
                                                }
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }

                    2 -> {
                        Column(
                            modifier = Modifier.padding(horizontal = 25.dp, vertical = 30.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
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

                            DoctorCard(
                                icon = genreTemp,
                                doctorDetails = listOf("$name $lastName", speciality?.displayName, getAbout(speciality)),
                                clinicDetails = listOf(
                                    selectedAddress?.name,
                                    "${selected.code} $formatted",
                                    email,
                                    "${selectedAddress?.street}, ${selectedAddress?.neighborhood}, ${selectedAddress?.zipCode} ${selectedAddress?.city}, ${selectedAddress?.state}"
                                ),
                                colors = listOf(Tekhelet, White, AntiFlashWhite),
                            )
                        }
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
