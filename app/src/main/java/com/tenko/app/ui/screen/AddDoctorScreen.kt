package com.tenko.app.ui.screen

import android.util.Patterns
import android.widget.Toast
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Arrangement.Absolute.spacedBy
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.autofill.ContentType
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusEvent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.contentType
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tenko.app.R
import com.tenko.app.data.api.getAddressByCP
import com.tenko.app.data.api.loadCountries
import com.tenko.app.data.model.Genre
import com.tenko.app.data.model.Speciality
import com.tenko.app.data.serializable.AddressCreate
import com.tenko.app.data.serializable.AddressResponse
import com.tenko.app.data.serializable.AddressUpdate
import com.tenko.app.data.serializable.ContactCreate
import com.tenko.app.data.view.AddressViewModel
import com.tenko.app.data.view.DoctorViewModel
import com.tenko.app.regex.formatAsYouType
import com.tenko.app.regex.isValidEmail
import com.tenko.app.regex.isValidNumber
import com.tenko.app.ui.components.AddressItem
import com.tenko.app.ui.components.AppTopBar
import com.tenko.app.ui.components.AvatarSelector
import com.tenko.app.ui.components.BottomBar
import com.tenko.app.ui.components.CountryDropdown
import com.tenko.app.ui.components.EmptyStateFullscreen
import com.tenko.app.ui.components.FlipCard
import com.tenko.app.ui.components.FormTextField
import com.tenko.app.ui.components.InfoRow
import com.tenko.app.ui.components.SpecialityDropdown
import com.tenko.app.ui.components.SquaredOptionSelector
import com.tenko.app.ui.components.blockedInput
import com.tenko.app.ui.components.inputField
import com.tenko.app.ui.theme.AntiFlashWhite
import com.tenko.app.ui.theme.BackgroundColor
import com.tenko.app.ui.theme.CardDark
import com.tenko.app.ui.theme.CardGray
import com.tenko.app.ui.theme.CardPurple
import com.tenko.app.ui.theme.PompAndPower
import com.tenko.app.ui.theme.RaisinBlack
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
    LaunchedEffect(Unit) { addressViewModel.fetchMyAddresses() }
    val isRefreshing = viewModel.isLoading || addressViewModel.isLoading

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val scrollState = rememberScrollState()
    val keyboardController = LocalSoftwareKeyboardController.current
    val context = LocalContext.current

    var screen by remember { mutableStateOf("list") }

    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var avatar by remember { mutableStateOf<Int?>(null) }
    var genreTemp by remember { mutableStateOf<Int?>(null) }
    var genre by remember { mutableStateOf<Genre?>(null) }
    var speciality by remember { mutableStateOf<Speciality?>(null) }

    var selectedAddress by remember { mutableStateOf<AddressResponse?>(null) }
    var label by remember { mutableStateOf(selectedAddress?.name ?: "") }
    var street by remember { mutableStateOf(selectedAddress?.street ?: "") }
    var city by remember { mutableStateOf(selectedAddress?.city ?: "") }
    var state by remember { mutableStateOf(selectedAddress?.state ?: "") }
    var zipCode by remember { mutableStateOf(selectedAddress?.zip_code ?: "") }
    var neighborhood by remember { mutableStateOf(selectedAddress?.neighborhood ?: "") }
    var phoneNumber by remember { mutableStateOf(selectedAddress?.phone_number ?: "") }

    var nameError by remember { mutableStateOf<String?>(null) }
    var lastNameError by remember { mutableStateOf<String?>(null) }
    var emailError by remember { mutableStateOf<String?>(null) }

    var labelError by remember { mutableStateOf<String?>(null) }
    var phoneNumberError by remember { mutableStateOf<String?>(null) }
    var streetError by remember { mutableStateOf<String?>(null) }
    var zipCodeError by remember { mutableStateOf<String?>(null) }

    val countries = loadCountries(context)
    var formatted by remember { mutableStateOf("") }
    var textFieldValue by remember { mutableStateOf(TextFieldValue("")) }
    var selected by remember {
        mutableStateOf(countries.firstOrNull { it.iso == "MX" } ?: countries.first())
    }

    LaunchedEffect(city, state, neighborhood) {
        if (zipCode.length == 5 && city.isNotBlank() && state.isNotBlank() && neighborhood.isNotBlank()) {
            delay(200)
            scrollState.animateScrollTo(scrollState.maxValue)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            topBar = {
                AppTopBar(
                    title = when (viewModel.currentStep) {
                        0 -> "Agregar especialista"
                        1 -> when (screen) {
                            "list" -> "Seleccionar consultorio"
                            "form" -> "Agregar dirección"
                            "form_edit" -> "Editar dirección"
                            else -> "TODO"
                        }

                        else -> "Confirmar información"
                    },
                    onBackClick = {
                        when (viewModel.currentStep) {
                            0 -> onBackClick()

                            1, 2 -> {
                                if (screen == "form" || screen == "form_edit") {
                                    screen = "list"
                                    selectedAddress = null
                                    addressViewModel.unselectAllAddresses()
                                    return@AppTopBar
                                }
                                viewModel.previousStep()
                            }
                        }
                    }
                ) {}
            },
            bottomBar = {
                when (screen) {
                    "form" -> {
                        Surface(
                            shape = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp),
                            shadowElevation = 8.dp
                        ) {
                            NavigationBar(
                                modifier = Modifier.shadow(
                                    elevation = 8.dp,
                                    shape = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp),
                                    clip = false,
                                    ambientColor = Color.Black,
                                    spotColor = Color.Black
                                ),
                                containerColor = White,
                                content = {
                                    Row(
                                        modifier = Modifier
                                            .defaultMinSize(minHeight = 66.dp)
                                            .padding(12.dp),
                                        horizontalArrangement = spacedBy(
                                            8.dp,
                                            Alignment.CenterHorizontally
                                        ),
                                    ) {
                                        OutlinedButton(
                                            onClick = { screen = "list" },
                                            modifier = Modifier.weight(1f),
                                            shape = RoundedCornerShape(12.dp),
                                            colors = ButtonDefaults.outlinedButtonColors(
                                                containerColor = Color.Transparent,
                                                contentColor = Color.Gray
                                            ),
                                            content = {
                                                Row(
                                                    modifier = Modifier.padding(
                                                        horizontal = 20.dp,
                                                        vertical = 12.dp
                                                    ),
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    horizontalArrangement = spacedBy(
                                                        8.dp,
                                                        Alignment.CenterHorizontally
                                                    ),
                                                ) {
                                                    Icon(
                                                        painter = painterResource(id = R.drawable.reply_solid_full),
                                                        contentDescription = "Go back",
                                                        modifier = Modifier.size(25.dp)
                                                    )

                                                    Text(
                                                        text = "Cancelar",
                                                        fontSize = 14.sp,
                                                        fontWeight = FontWeight.SemiBold,
                                                    )
                                                }
                                            }
                                        )

                                        OutlinedButton(
                                            onClick = {
                                                if (label.isBlank() || street.isBlank() || city.isBlank() || state.isBlank() || zipCode.isBlank() || neighborhood.isBlank() || phoneNumber.isBlank()) {
                                                    labelError =
                                                        if (label.isBlank()) "El nombre es obligatorio" else null
                                                    phoneNumberError =
                                                        if (phoneNumber.isBlank()) "El teléfono es obligatorio" else null
                                                    streetError =
                                                        if (street.isBlank()) "La calle es obligatoria" else null
                                                    zipCodeError =
                                                        if (zipCode.isBlank()) "El código postal es obligatorio para obtener la ciudad y estado" else null

                                                    return@OutlinedButton
                                                }

                                                addressViewModel.createAddress(
                                                    addressData = AddressCreate(
                                                        name = label,
                                                        street = street,
                                                        neighborhood = neighborhood,
                                                        city = city,
                                                        state = state,
                                                        zip_code = zipCode,
                                                        phone_number = phoneNumber,
                                                        is_selected = true
                                                    )
                                                ) {
                                                    Toast.makeText(
                                                        context,
                                                        "Dirección agregada",
                                                        Toast.LENGTH_SHORT
                                                    ).show()
                                                    screen = "list"
                                                }
                                            },
                                            modifier = Modifier.weight(1f),
                                            enabled = !addressViewModel.isLoading,
                                            shape = RoundedCornerShape(12.dp),
                                            colors = ButtonDefaults.outlinedButtonColors(
                                                containerColor = PompAndPower,
                                                contentColor = White
                                            ),
                                            content = {
                                                Row(
                                                    modifier = Modifier.padding(
                                                        horizontal = 20.dp,
                                                        vertical = 12.dp
                                                    ),
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    horizontalArrangement = spacedBy(
                                                        8.dp,
                                                        Alignment.CenterHorizontally
                                                    ),
                                                ) {
                                                    Icon(
                                                        painter = painterResource(id = R.drawable.floppy_disk_solid_full),
                                                        contentDescription = "Save icon",
                                                        modifier = Modifier.size(25.dp)
                                                    )

                                                    Text(
                                                        text = "Guardar",
                                                        fontSize = 14.sp,
                                                        fontWeight = FontWeight.SemiBold
                                                    )
                                                }
                                            }
                                        )
                                    }
                                }
                            )
                        }
                    }

                    "form_edit" -> {
                        Surface(
                            shape = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp),
                            shadowElevation = 8.dp
                        ) {
                            NavigationBar(
                                modifier = Modifier.shadow(
                                    elevation = 8.dp,
                                    shape = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp),
                                    clip = false,
                                    ambientColor = Color.Black,
                                    spotColor = Color.Black
                                ),
                                containerColor = White,
                                content = {
                                    Row(
                                        modifier = Modifier.padding(12.dp),
                                        horizontalArrangement = spacedBy(
                                            8.dp,
                                            Alignment.CenterHorizontally
                                        ),
                                    ) {
                                        OutlinedButton(
                                            onClick = { screen = "list" },
                                            modifier = Modifier.weight(1f),
                                            shape = RoundedCornerShape(12.dp),
                                            content = {
                                                Row(
                                                    modifier = Modifier.padding(
                                                        horizontal = 20.dp,
                                                        vertical = 12.dp
                                                    ),
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    horizontalArrangement = spacedBy(
                                                        8.dp,
                                                        Alignment.CenterHorizontally
                                                    ),
                                                ) {
                                                    Icon(
                                                        painter = painterResource(id = R.drawable.reply_solid_full),
                                                        contentDescription = "Go back",
                                                        modifier = Modifier.size(25.dp),
                                                        tint = Color.Gray
                                                    )

                                                    Text(
                                                        text = "Cancelar",
                                                        color = Color.Gray,
                                                        fontSize = 14.sp,
                                                        fontWeight = FontWeight.SemiBold,
                                                    )
                                                }
                                            }
                                        )

                                        selectedAddress?.let { address ->
                                            OutlinedButton(
                                                onClick = {
                                                    addressViewModel.deleteAddress(address.id_address)
                                                    scope.launch {
                                                        val result = snackbarHostState.showSnackbar(
                                                            message = "Dirección eliminada",
                                                            actionLabel = "Deshacer",
                                                            duration = SnackbarDuration.Short
                                                        )

                                                        if (result == SnackbarResult.ActionPerformed) {
                                                            addressViewModel.createAddress(
                                                                addressData = AddressCreate(
                                                                    name = address.name,
                                                                    street = address.street,
                                                                    neighborhood = address.neighborhood,
                                                                    city = address.city,
                                                                    state = address.state,
                                                                    zip_code = address.zip_code,
                                                                    phone_number = address.phone_number,
                                                                    is_selected = false
                                                                )
                                                            ) {
                                                                Toast.makeText(
                                                                    context,
                                                                    "Dirección restaurada",
                                                                    Toast.LENGTH_SHORT
                                                                ).show()
                                                            }
                                                            addressViewModel.unselectAllAddresses()
                                                        }
                                                    }

                                                    selectedAddress = null
                                                    screen = "list"
                                                },
                                                modifier = Modifier.weight(1f),
                                                shape = RoundedCornerShape(12.dp),
                                                content = {
                                                    Row(
                                                        modifier = Modifier.padding(
                                                            horizontal = 20.dp,
                                                            vertical = 12.dp
                                                        ),
                                                        verticalAlignment = Alignment.CenterVertically,
                                                        horizontalArrangement = spacedBy(
                                                            8.dp,
                                                            Alignment.CenterHorizontally
                                                        ),
                                                    ) {
                                                        Icon(
                                                            painter = painterResource(id = R.drawable.trash_can_regular_full),
                                                            contentDescription = "Delete icon",
                                                            modifier = Modifier.size(25.dp),
                                                            tint = MaterialTheme.colorScheme.error
                                                        )

                                                        Text(
                                                            text = "Eliminar",
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
                            )
                        }
                    }

                    else -> {
                        BottomBar(
                            onNextStep = {
                                when (viewModel.currentStep) {
                                    0 -> {
                                        if (speciality == null || genre == null || avatar == null || name.isBlank() || lastName.isBlank() || email.isBlank() || !isValidEmail(
                                                email
                                            )
                                        ) {
                                            if (speciality == null) {
                                                Toast.makeText(
                                                    context,
                                                    "Por favor selecciona una especialidad",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                                return@BottomBar
                                            }
                                            if (genre == null) {
                                                Toast.makeText(
                                                    context,
                                                    "Por favor selecciona un género",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                                return@BottomBar
                                            }
                                            if (avatar == null) {
                                                Toast.makeText(
                                                    context,
                                                    "Por favor selecciona un avatar",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                                return@BottomBar
                                            }
                                            nameError =
                                                if (name.isBlank()) "El nombre es obligatorio" else null
                                            lastNameError =
                                                if (lastName.isBlank()) "El apellido es obligatorio" else null
                                            emailError =
                                                if (email.isBlank()) "El correo es obligatorio" else if (!isValidEmail(
                                                        email
                                                    )
                                                ) "Correo incompleto" else null
                                            return@BottomBar
                                        }

                                        keyboardController?.hide()
                                        scope.launch { delay(500) }
                                        viewModel.nextStep()
                                    }

                                    1 -> {
                                        if (selectedAddress != null) {
                                            viewModel.nextStep()
                                        } else {
                                            Toast.makeText(
                                                context,
                                                "Por favor selecciona o agrega una dirección para el consultorio",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    }

                                    2 -> {
                                        val newContact = ContactCreate(
                                            name = name,
                                            last_name = lastName,
                                            email = email,
                                            about = speciality?.description,
                                            specialty = speciality?.displayName,
                                            genre = avatar,
                                            phone_number = phoneNumber,
                                            address = selectedAddress?.let { "${it.name},${it.street},${it.neighborhood},${it.zip_code},${it.city},${it.state}" }
                                        )
                                        viewModel.createContact(newContact, context, onBackClick)
                                    }
                                }
                            },
                            onPreviousStep = { if (viewModel.currentStep > 0) viewModel.previousStep() else onBackClick() },
                            currentStep = viewModel.currentStep,
                            totalSteps = 3
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
                when (step) {
                    0 -> {
                        val nameFocus = remember { FocusRequester() }
                        val lastNameFocus = remember { FocusRequester() }
                        val emailFocus = remember { FocusRequester() }
                        val specialityFocus = remember { FocusRequester() }

                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .imePadding()
                                .verticalScroll(scrollState)
                                .padding(paddingValues)
                                .padding(horizontal = 20.dp, vertical = 30.dp),
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
                                text = "Especialidad",
                                color = Color.Gray,
                                fontSize = 14.sp,
                            )
                            SpecialityDropdown(
                                scope = scope,
                                selected = speciality?.displayName ?: "",
                                onSelected = { sp ->
                                    speciality = sp
                                },
                                modifier = Modifier.focusRequester(specialityFocus)
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
                                    avatar = when (genreTemp) {
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

                            FormTextField(
                                type = ContentType.PersonFirstName,
                                value = name,
                                onValueChange = {
                                    name = it
                                    nameError = null
                                },
                                label = "Nombre del especialista",
                                placeholder = "Primer nombre",
                                error = nameError,
                                focusRequester = nameFocus,
                                imeAction = ImeAction.Next,
                                onNext = {
                                    if (name.isBlank()) {
                                        nameError = "El nombre es obligatorio"
                                        return@FormTextField
                                    }
                                    lastNameFocus.requestFocus()
                                },
                                scrollState = scrollState,
                                scope = scope
                            )

                            FormTextField(
                                type = ContentType.PersonLastName,
                                value = lastName,
                                onValueChange = {
                                    lastName = it
                                    lastNameError = null
                                },
                                label = "Apellido del especialista",
                                placeholder = "Primer apellido",
                                error = lastNameError,
                                focusRequester = lastNameFocus,
                                imeAction = ImeAction.Next,
                                onNext = {
                                    if (lastName.isBlank()) {
                                        lastNameError = "El apellido es obligatorio"
                                        return@FormTextField
                                    }
                                    emailFocus.requestFocus()
                                },
                                scrollState = scrollState,
                                scope = scope
                            )

                            FormTextField(
                                type = ContentType.EmailAddress,
                                value = email,
                                onValueChange = {
                                    email = it
                                    emailError = null
                                },
                                label = "Correo electrónico del especialista",
                                placeholder = "correo@ejemplo.com",
                                error = emailError,
                                focusRequester = emailFocus,
                                imeAction = ImeAction.Done,
                                onDone = {
                                    if (speciality == null || genre == null || avatar == null || name.isBlank() || lastName.isBlank() || email.isBlank() || !isValidEmail(
                                            email
                                        )
                                    ) {
                                        if (speciality == null) {
                                            Toast.makeText(
                                                context,
                                                "Por favor selecciona una especialidad",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                            return@FormTextField
                                        }
                                        if (genre == null) {
                                            Toast.makeText(
                                                context,
                                                "Por favor selecciona un género",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                            return@FormTextField
                                        }
                                        if (avatar == null) {
                                            Toast.makeText(
                                                context,
                                                "Por favor selecciona un avatar",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                            return@FormTextField
                                        }
                                        nameError =
                                            if (name.isBlank()) "El nombre es obligatorio" else null
                                        lastNameError =
                                            if (lastName.isBlank()) "El apellido es obligatorio" else null
                                        emailError =
                                            if (email.isBlank()) "El correo es obligatorio" else if (!isValidEmail(
                                                    email
                                                )
                                            ) "Correo incompleto" else null
                                        return@FormTextField
                                    }

                                    keyboardController?.hide()
                                    scope.launch { delay(500) }
                                    viewModel.nextStep()
                                },
                                scrollState = scrollState,
                                scope = scope
                            )
                        }
                    }

                    1 -> {
                        when (screen) {
                            "list" -> {
                                val lazyListState = rememberLazyListState()
                                if (selectedAddress?.phone_number != null) {
                                    formatted = formatAsYouType(
                                        raw = selectedAddress!!.phone_number!!,
                                        regionCode = selected.iso
                                    )
                                    textFieldValue = TextFieldValue(
                                        text = formatted,
                                        selection = TextRange(formatted.length)
                                    )
                                }

                                Column(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(paddingValues)
                                        .padding(top = 30.dp)
                                ) {
                                    Column(
                                        modifier = Modifier.padding(horizontal = 20.dp),
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
                                            fontSize = 15.sp
                                        )
                                        Text(
                                            text = "Selecciona una dirección existente o agrega una nueva para el consultorio.",
                                            color = Color.LightGray,
                                            fontSize = 13.sp
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
                                                textFieldValue = TextFieldValue("")
                                                screen = "form"
                                            }
                                            .padding(20.dp),
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
                                                    contentDescription = "Add icon",
                                                    modifier = Modifier.size(20.dp),
                                                    tint = PompAndPower
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
                                            contentDescription = "Go to form",
                                            tint = Color.LightGray,
                                            modifier = Modifier.size(25.dp)
                                        )
                                    }

                                    HorizontalDivider(
                                        modifier = Modifier.padding(horizontal = 40.dp),
                                        thickness = 2.dp,
                                        color = AntiFlashWhite
                                    )

                                    if (addressViewModel.addresses.isEmpty() && !isRefreshing)
                                        EmptyStateFullscreen(
                                            icon = R.drawable.truck_medical_solid_full,
                                            title = "No hay direcciones registradas",
                                            description = "Agrega la dirección de tus consultorios para llevar un mejor control de tu salud."
                                        )
                                    else {
                                        LazyColumn(
                                            modifier = Modifier.fillMaxSize(),
                                            state = lazyListState,
                                        ) {
                                            items(addressViewModel.addresses) { address ->
                                                AddressItem(
                                                    address = address.copy(is_selected = address.id_address == selectedAddress?.id_address),
                                                    onSelect = {
                                                        selectedAddress = address
                                                        addressViewModel.selectAddress(address.id_address)
                                                        scope.launch {
                                                            lazyListState.animateScrollToItem(
                                                                0
                                                            )
                                                        }
                                                    },
                                                    onEdit = {
                                                        selectedAddress = address
                                                        screen = "form_edit"
                                                    }
                                                )
                                            }
                                        }
                                    }
                                }
                            }

                            "form_edit" -> {
                                var showLabelEditDialog by remember { mutableStateOf(false) }
                                var showPhoneEditDialog by remember { mutableStateOf(false) }
                                var showStreetEditDialog by remember { mutableStateOf(false) }
                                var showZipEditDialog by remember { mutableStateOf(false) }
                                var value by remember { mutableStateOf("") }
                                val colors = OutlinedTextFieldDefaults.colors(
                                    unfocusedContainerColor = AntiFlashWhite,
                                    focusedBorderColor = PompAndPower,
                                    unfocusedBorderColor = Color.Transparent,
                                    focusedTrailingIconColor = PompAndPower,
                                    unfocusedTrailingIconColor = SweetGrey,
                                    unfocusedPlaceholderColor = Color.Gray,
                                )

                                Column(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .imePadding()
                                        .verticalScroll(scrollState)
                                        .padding(paddingValues)
                                        .padding(horizontal = 20.dp, vertical = 30.dp)
                                ) {
                                    Text(
                                        text = "Datos del consultorio",
                                        modifier = Modifier.padding(vertical = 8.dp),
                                        color = Tekhelet,
                                        fontSize = 18.sp
                                    )

                                    InfoRow(
                                        label = "Nombre",
                                        value = selectedAddress?.name ?: "No proporcionado",
                                        onClick = { showLabelEditDialog = true }
                                    )
                                    AnimatedVisibility(
                                        visible = showLabelEditDialog,
                                        enter = fadeIn() + expandVertically(),
                                        exit = fadeOut() + shrinkVertically()
                                    ) {
                                        OutlinedTextField(
                                            value = value,
                                            onValueChange = { newText ->
                                                if (newText.all { it.isLetterOrDigit() || it.isWhitespace() }) {
                                                    if (newText.length > 25)
                                                        Toast.makeText(
                                                            context,
                                                            "Máximo 25 caracteres",
                                                            Toast.LENGTH_SHORT
                                                        )
                                                            .show()
                                                    else {
                                                        value = newText.split(" ")
                                                            .joinToString(" ") { word ->
                                                                word.replaceFirstChar {
                                                                    if (it.isLowerCase()) it.titlecase() else it.toString()
                                                                }
                                                            }
                                                    }
                                                } else
                                                    Toast.makeText(
                                                        context,
                                                        "Solo letras, números y espacios",
                                                        Toast.LENGTH_SHORT
                                                    )
                                                        .show()
                                            },
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .defaultMinSize(minHeight = 66.dp)
                                                .semantics {
                                                    contentType = ContentType.AddressLocality
                                                },
                                            placeholder = {
                                                Text(
                                                    selectedAddress?.name!!,
                                                    fontSize = 14.sp
                                                )
                                            },
                                            trailingIcon = {
                                                Icon(
                                                    modifier = Modifier
                                                        .size(35.dp)
                                                        .padding(end = 4.dp),
                                                    painter = painterResource(id = R.drawable.hospital_regular_full),
                                                    contentDescription = "Building icon"
                                                )
                                            },
                                            singleLine = true,
                                            shape = RoundedCornerShape(12.dp),
                                            keyboardOptions = KeyboardOptions(
                                                capitalization = KeyboardCapitalization.Words,
                                                keyboardType = KeyboardType.Text,
                                                imeAction = ImeAction.Done
                                            ),
                                            keyboardActions = KeyboardActions(
                                                onDone = {
                                                    val newName = value.trim()
                                                    if (newName.isNotEmpty()) {
                                                        addressViewModel.updateAddress(
                                                            idAddress = selectedAddress!!.id_address,
                                                            updateData = AddressUpdate(name = newName),
                                                            onSuccess = {
                                                                selectedAddress =
                                                                    selectedAddress?.copy(name = newName)
                                                                Toast.makeText(
                                                                    context,
                                                                    "Nombre actualizado",
                                                                    Toast.LENGTH_SHORT
                                                                ).show()
                                                            }
                                                        )
                                                    }
                                                }
                                            ),
                                            colors = colors,
                                        )
                                    }
                                    InfoRow(
                                        label = "Teléfono",
                                        value = selectedAddress?.phone_number ?: "No proporcionado",
                                        onClick = {

                                        }
                                    )
                                    InfoRow(
                                        label = "Calle y número",
                                        value = selectedAddress?.street ?: "No proporcionado",
                                        onClick = {

                                        }
                                    )
                                    InfoRow(
                                        label = "Código postal",
                                        value = selectedAddress?.zip_code ?: "No proporcionado",
                                        onClick = {

                                        }
                                    )
                                    InfoRow(
                                        label = "Ciudad",
                                        value = selectedAddress?.city ?: "No proporcionado",
                                    )
                                    InfoRow(
                                        label = "Estado",
                                        value = selectedAddress?.state ?: "No proporcionado",
                                    )
                                    InfoRow(
                                        label = "Colonia",
                                        value = selectedAddress?.neighborhood ?: "No proporcionado",
                                    )
                                }
                            }

                            "form" -> {
                                val labelFocus = remember { FocusRequester() }
                                val phoneNumberFocus = remember { FocusRequester() }
                                val streetFocus = remember { FocusRequester() }
                                val zipCodeFocus = remember { FocusRequester() }

                                Column(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .imePadding()
                                        .verticalScroll(scrollState)
                                        .padding(paddingValues)
                                        .padding(horizontal = 20.dp, vertical = 30.dp),
                                    verticalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Text(
                                        text = "Datos del consultorio",
                                        color = RaisinBlack,
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )

                                    Spacer(modifier = Modifier.height(6.dp))

                                    FormTextField(
                                        type = ContentType.AddressLocality,
                                        value = label,
                                        onValueChange = {
                                            label = it
                                            labelError = null
                                        },
                                        label = "Nombre del consultorio",
                                        placeholder = "Ej: Hospital San Javier",
                                        error = labelError,
                                        focusRequester = labelFocus,
                                        imeAction = ImeAction.Next,
                                        onNext = {
                                            if (label.isBlank()) {
                                                labelError =
                                                    "El nombre del consultorio es obligatorio"
                                                return@FormTextField
                                            }
                                            phoneNumberFocus.requestFocus()
                                        },
                                        scrollState = scrollState,
                                        scope = scope,
                                    )


                                    val (phoneNumberAux, formattedAux) = inputField(
                                        type = ContentType.PhoneNumber,
                                        value = textFieldValue,
                                        onValueChange = {
                                            textFieldValue = it
                                            phoneNumberError = null
                                        },
                                        error = phoneNumberError,
                                        focusRequester = phoneNumberFocus,
                                        imeAction = ImeAction.Next,
                                        onNext = {
                                            if (phoneNumber.isBlank()) {
                                                phoneNumberError =
                                                    "El teléfono del consultorio es obligatorio"
                                                return@inputField
                                            }
                                            streetFocus.requestFocus()
                                        },
                                        scrollState = scrollState,
                                        scope = scope,
                                    )
                                    phoneNumber = phoneNumberAux
                                    formatted = formattedAux

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
                                    FormTextField(
                                        type = ContentType.AddressStreet,
                                        value = street,
                                        onValueChange = {
                                            street = it
                                            streetError = null
                                        },
                                        label = "",
                                        placeholder = "Ej: Pánfilo Pérez 320",
                                        error = streetError,
                                        focusRequester = streetFocus,
                                        imeAction = ImeAction.Next,
                                        onNext = {
                                            if (street.isBlank()) {
                                                streetError = "La dirección es obligatoria"
                                                return@FormTextField
                                            }
                                            zipCodeFocus.requestFocus()
                                        },
                                        scrollState = scrollState,
                                        scope = scope,
                                    )

                                    FormTextField(
                                        value = zipCode,
                                        onValueChange = {
                                            zipCode = it
                                            zipCodeError = null
                                            city = ""
                                            state = ""
                                            neighborhood = ""
                                        },
                                        label = "Ingresa el código postal del consultorio",
                                        placeholder = "Ej: 44340",
                                        error = zipCodeError,
                                        focusRequester = zipCodeFocus,
                                        imeAction = ImeAction.Next,
                                        onNext = {
                                            if (zipCode.length != 5) {
                                                zipCodeError =
                                                    if (zipCode.isBlank()) "El código postal es obligatorio" else "Los códigos postales deben contener 5 dígitos"
                                                return@FormTextField
                                            }

                                            keyboardController?.hide()
                                            scope.launch {
                                                val result = getAddressByCP(zipCode)
                                                if (result == null)
                                                    Toast.makeText(
                                                        context,
                                                        "No se encontró información para el código postal ingresado",
                                                        Toast.LENGTH_SHORT
                                                    ).show()
                                                else {
                                                    state = result.estado
                                                    city = result.municipio
                                                    neighborhood =
                                                        result.asentamientos.firstOrNull()
                                                            ?: ""
                                                }
                                            }
                                        },
                                        scrollState = scrollState,
                                        scope = scope,
                                    )

                                    AnimatedVisibility(
                                        visible = zipCode.length == 5 && city.isNotBlank() && state.isNotBlank() && neighborhood.isNotBlank(),
                                        enter = fadeIn() + expandVertically(),
                                        exit = fadeOut() + shrinkVertically()
                                    ) {
                                        Column(
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
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                                            ) {
                                                city = blockedInput(
                                                    "Ciudad",
                                                    city,
                                                    Modifier.weight(1f)
                                                )
                                                state = blockedInput(
                                                    "Estado",
                                                    state,
                                                    Modifier.weight(1f)
                                                )
                                            }
                                            neighborhood = blockedInput(
                                                "Colonia",
                                                neighborhood,
                                                Modifier.fillMaxWidth()
                                            )
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
                                                        contentDescription = "Go back",
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
                                            modifier = Modifier.padding(
                                                vertical = 8.dp,
                                                horizontal = 40.dp
                                            ),
                                            thickness = 2.dp,
                                            color = AntiFlashWhite
                                        )

                                        OutlinedButton(
                                            onClick = {
                                                addressViewModel.deleteAddress(address.id_address)
                                                scope.launch {
                                                    val result = snackbarHostState.showSnackbar(
                                                        message = "Dirección eliminada",
                                                        actionLabel = "Deshacer",
                                                        duration = SnackbarDuration.Short
                                                    )

                                                    if (result == SnackbarResult.ActionPerformed) {
                                                        addressViewModel.createAddress(
                                                            addressData = AddressCreate(
                                                                name = address.name,
                                                                street = address.street,
                                                                neighborhood = address.neighborhood,
                                                                city = address.city,
                                                                state = address.state,
                                                                zip_code = address.zip_code,
                                                                phone_number = address.phone_number,
                                                                is_selected = false
                                                            )
                                                        ) {
                                                            Toast.makeText(
                                                                context,
                                                                "Dirección restaurada",
                                                                Toast.LENGTH_SHORT
                                                            ).show()
                                                        }
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
                            modifier = Modifier
                                .fillMaxSize()
                                .verticalScroll(scrollState)
                                .padding(paddingValues)
                                .padding(horizontal = 20.dp, vertical = 30.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            val colors = listOf(
                                listOf(CardGray, RaisinBlack, Color.Gray),
                                listOf(CardPurple, White, AntiFlashWhite),
                                listOf(Tekhelet, White, AntiFlashWhite),
                                listOf(CardDark, White, AntiFlashWhite),
                                listOf(RaisinBlack, White, AntiFlashWhite)
                            ).random()
                            var flipped by remember { mutableStateOf(false) }

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
                                fontSize = 15.sp
                            )
                            Text(
                                text = "Revisa que toda la información sea correcta antes de agregar al doctor a tu lista de contactos.",
                                color = Color.LightGray,
                                fontSize = 13.sp
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            FlipCard(
                                icon = genreTemp,
                                flipped = flipped,
                                onFlip = { flipped = !flipped },
                                doctorDetails = listOf(
                                    "$name $lastName",
                                    speciality?.displayName,
                                    speciality?.description
                                ),
                                clinicDetails = listOf(
                                    selectedAddress?.name,
                                    "${selected.code} $formatted",
                                    email,
                                    "${selectedAddress?.street}, ${selectedAddress?.neighborhood}, ${selectedAddress?.zip_code} ${selectedAddress?.city}, ${selectedAddress?.state}"
                                ),
                                colors = listOf(Tekhelet, White, AntiFlashWhite),
                            )

                            HorizontalDivider(
                                modifier = Modifier.padding(top = 12.dp, bottom = 24.dp),
                                thickness = 2.dp,
                                color = Tekhelet.copy(0.6f)
                            )

                            AnimatedContent(
                                targetState = flipped,
                                transitionSpec = {
                                    fadeIn(
                                        animationSpec = tween(900)
                                    ) + slideInVertically(
                                        initialOffsetY = { it / 2 }
                                    ) togetherWith fadeOut(
                                        animationSpec = tween(900)
                                    ) + slideOutVertically(
                                        targetOffsetY = { -it / 2 }
                                    )
                                },
                                label = "bottom_text_animation"
                            ) { isFlipped ->
                                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                    if (!isFlipped) {
                                        Text(
                                            text = "Contacto disponible en...",
                                            color = Color.Gray,
                                            fontSize = 15.sp
                                        )
                                        Text(
                                            text = email,
                                            modifier = Modifier.padding(start = 16.dp),
                                            color = Color.LightGray,
                                            fontSize = 13.sp
                                        )

                                        Spacer(modifier = Modifier.height(6.dp))

                                        Text(
                                            text = "Acerca de su especialidad...",
                                            color = Color.Gray,
                                            fontSize = 15.sp
                                        )
                                        Text(
                                            text = speciality?.description
                                                ?: "Sin descripción de especialidad",
                                            modifier = Modifier
                                                .padding(start = 16.dp)
                                                .fillMaxWidth(),
                                            color = Color.LightGray,
                                            fontSize = 13.sp,
                                            textAlign = TextAlign.Justify
                                        )
                                    } else {
                                        Text(
                                            text = "Clínica ubicada en...",
                                            color = Color.Gray,
                                            fontSize = 15.sp
                                        )
                                        Text(
                                            text = "${selectedAddress?.street}, ${selectedAddress?.neighborhood}, ${selectedAddress?.zip_code} ${selectedAddress?.city}, ${selectedAddress?.state}",
                                            modifier = Modifier.padding(start = 16.dp),
                                            color = Color.LightGray,
                                            fontSize = 13.sp
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        if (isRefreshing) {
            SplashScreen()
        }
    }
}