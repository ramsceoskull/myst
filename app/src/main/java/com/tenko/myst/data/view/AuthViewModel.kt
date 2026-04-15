package com.tenko.myst.data.view

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.tenko.myst.data.api.ApiClient
import com.tenko.myst.data.api.TokenManager
import com.tenko.myst.data.serializable.ForgotPasswordRequest
import com.tenko.myst.data.serializable.Token
import com.tenko.myst.data.serializable.UserCreate
import com.tenko.myst.data.serializable.UserDelete
import com.tenko.myst.data.serializable.UserResponse
import com.tenko.myst.data.serializable.UserUpdate
import com.tenko.myst.navigation.AppScreens
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.forms.submitForm
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.patch
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.http.parameters
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {
    var isLoading by mutableStateOf(false)
    var loginError by mutableStateOf<String?>(null)
    var currentUser by mutableStateOf<UserResponse?>(null)
        private set

    fun login(email: String, password: String, navController: NavController, tokenManager: TokenManager) {
        viewModelScope.launch {
            isLoading = true
            loginError = null
            try {
                val response = ApiClient.client.submitForm(
                    url = "https://api-myst.onrender.com/auth/login",
                    formParameters = parameters {
                        append("username", email)
                        append("password", password)
                    }
                )

                if (response.status == HttpStatusCode.OK) {
                    val tokenData = response.body<Token>()
                    tokenManager.saveToken(tokenData.access_token)
                    ApiClient.clearAuthCache()
                    fetchUserData(navController)
                    // Aquí guardarías el token (ej. en SharedPreferences o DataStore)
                } else {
                    loginError = "Credenciales incorrectas"
                }
            } catch (e: Exception) {
                loginError = "Error de conexión: ${e.localizedMessage}"
            } finally {
//                print("Token guardado: ${tokenData!!.access_token}")
                isLoading = false
            }
        }
    }

    private suspend fun fetchUserData(navController: NavController) {
        try {
            val response = ApiClient.client.get("https://api-myst.onrender.com/users/me")
            if (response.status == HttpStatusCode.OK) {
                currentUser = response.body<UserResponse>()
                navController.navigate(AppScreens.MainScreen.route) {
                    popUpTo(AppScreens.LoginScreen.route) { inclusive = true }
                }
            }
        } catch (e: Exception) {
            println("Error cargando usuario: ${e.message}")
        }
    }

    private fun handleUnauthorized(navController: NavController) {
        viewModelScope.launch {
            // Limpiamos el estado local
            currentUser = null
            // Redirigimos al Login y limpiamos el stack de navegación
            navController.navigate(AppScreens.LoginScreen.route) {
                popUpTo(0)
            }
        }
    }

    fun forgotPassword(email: String, navController: NavController){
        viewModelScope.launch {
            isLoading = true
            try {
                val response = ApiClient.client.post("https://api-myst.onrender.com/auth/forgot-password"){
                    contentType(ContentType.Application.Json)
                    setBody(ForgotPasswordRequest(email))
                }
                if (response.status == HttpStatusCode.OK) {
                    loginError = "Correo de recuperación enviado"
                    navController.navigate(AppScreens.ForgotPasswordScreen.route)
                } else {
                    loginError = "Usuario no encontrado"
                }
            } catch (e: Exception) {
                loginError = "Error de red: ${e.localizedMessage}"
            } finally {
                isLoading = false
            }
        }
    }

    fun verifyEmail(token: String, navController: NavController) {
        viewModelScope.launch {
            isLoading = true
            try {
                val response = ApiClient.client.get("https://api-myst.onrender.com/auth/verify-email") {
                    parameter("token", token)
                }
                if (response.status == HttpStatusCode.OK) {
                    // Éxito: El backend devuelve un HTMLResponse
                    println("Email verificado con éxito")
                } else {
                    loginError = "Token inválido o expirado"
                }
            } catch (e: Exception) {
                loginError = "Error: ${e.localizedMessage}"
            } finally {
                isLoading = false
            }
        }
    }

    fun resetPassword(token: String, newPassword: String, navController: NavController){
        viewModelScope.launch {
            isLoading = true
            loginError = null
            try {
                // Usamos submitForm porque tu API espera Form(...)
                val response = ApiClient.client.submitForm(
                    url = "https://api-myst.onrender.com/auth/reset-password",
                    formParameters = parameters {
                        append("token", token)
                        append("new_password", newPassword)
                    }
                )

                if (response.status == HttpStatusCode.OK) {
                    // Éxito: Navegamos al login o mostramos mensaje
                    navController.navigate(AppScreens.LoginScreen.route) {
                        popUpTo(AppScreens.LoginScreen.route) { inclusive = true }
                    }
                } else {
                    // Aquí capturamos el "Token inválido" o "Token expirado"
                    loginError = "Error: ${response.status.description}"
                }
            } catch (e: Exception) {
                loginError = "Error de conexión: ${e.localizedMessage}"
            } finally {
                isLoading = false
            }
        }
    }

    fun createUser(userData: UserCreate, navController: NavController){
        viewModelScope.launch {
            isLoading = true
            loginError = null
            try {
                val response = ApiClient.client.post("https://api-myst.onrender.com/users/") {
                    contentType(ContentType.Application.Json)
                    setBody(userData)
                }

                if (response.status == HttpStatusCode.Created) {
                    // Registro exitoso. El usuario ahora debe revisar su correo.
                    // Podrías navegar a una pantalla de "Revisa tu correo"
                    navController.navigate("${AppScreens.ValidateEmailScreen.route}/${userData.email}")
                } else {
                    // Manejar errores como "Email already registered"
                    loginError = "El correo ya está registrado o los datos son inválidos"
                }
            } catch (e: Exception) {
                loginError = "Error de red: ${e.localizedMessage}"
            } finally {
                isLoading = false
            }
        }
    }

    fun getUser(navController: NavController) {
        viewModelScope.launch {
            isLoading = true
            try {
                val response = ApiClient.client.get("https://api-myst.onrender.com/users/me")
                when (response.status) {
                    HttpStatusCode.OK -> {
                        // Aquí se guarda el perfil en una variable de estado
                        currentUser = response.body<UserResponse>()
                        println("Usuario obtenido: ${currentUser?.name}")
                    }
                    HttpStatusCode.Unauthorized -> {
                        // Si el token es inválido o ha expirado, manejamos la situación
                        println("Token inválido o expirado. Redirigiendo al login.")
                        handleUnauthorized(navController)
                    }
                    else -> {
                        loginError = "Error al obtener perfil: ${response.status.description}"
                    }
                }
            } catch (e: Exception) {
                loginError = "Error al obtener perfil: ${e.localizedMessage}"
            } finally {
                isLoading = false
            }
        }
    }

    fun updateUser(userId: Int, updateData: UserUpdate) {
        viewModelScope.launch {
            isLoading = true
            try {
                val response = ApiClient.client.patch("https://api-myst.onrender.com/users/me") {
                    contentType(ContentType.Application.Json)
                    setBody(updateData)
                }
                if (response.status == HttpStatusCode.OK) {
                    val updatedUser = response.body<UserResponse>()
                    println("Usuario actualizado: ${updatedUser.name}")
                }
            } catch (e: Exception) {
                loginError = "Error al actualizar: ${e.localizedMessage}"
            } finally {
                isLoading = false
            }
        }
    }

    fun deleteUser(passwordConfirm: String, tokenManager: TokenManager, navController: NavController) {
        viewModelScope.launch {
            isLoading = true
            try {
                // Preparamos el objeto ANTES de la petición para ver si falla aquí
                val userDeleteObj = UserDelete(password = passwordConfirm)

                val response = ApiClient.client.delete("https://api-myst.onrender.com/users/me") {
                    contentType(ContentType.Application.Json)
                    setBody(userDeleteObj)
                }

                if (response.status == HttpStatusCode.NoContent || response.status == HttpStatusCode.OK) {
                    tokenManager.deleteToken()
                    ApiClient.clearAuthCache()
                    currentUser = null
                    navController.navigate(AppScreens.LoginScreen.route) {
                        popUpTo(0)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                isLoading = false
            }
        }
    }
}