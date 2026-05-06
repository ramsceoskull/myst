package com.tenko.app.data.view

import android.net.Uri
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.google.firebase.Firebase
import com.google.firebase.storage.storage
import com.tenko.app.data.api.ApiClient
import com.tenko.app.data.api.TokenManager
import com.tenko.app.data.serializable.ForgotPasswordRequest
import com.tenko.app.data.serializable.Token
import com.tenko.app.data.serializable.UserCreate
import com.tenko.app.data.serializable.UserDelete
import com.tenko.app.data.serializable.UserResponse
import com.tenko.app.data.serializable.UserUpdate
import com.tenko.app.navigation.AppScreens
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.forms.submitForm
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.client.request.patch
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.http.parameters
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class AuthViewModel : ViewModel() {

    var isLoading by mutableStateOf(false)
    var isCheckingAuth by mutableStateOf(true) // Controla si la app está validando sesión al inicio
    var loginError by mutableStateOf<String?>(null)

    var currentUser by mutableStateOf<UserResponse?>(null)
        private set

    /**
     * Inicializa la sesión. Lee el ID del disco y si existe, pide el perfil.
     */
    fun initAuth(tokenManager: TokenManager) {
        viewModelScope.launch {
            isCheckingAuth = true
            try {
                val savedId = tokenManager.getUserId.firstOrNull()

                if (!savedId.isNullOrBlank()) {
                    fetchUserInternal()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                isCheckingAuth = false
            }
        }
    }

    private suspend fun fetchUserInternal() {
        try {
            val response = ApiClient.client.get("https://api-myst.onrender.com/users/me")

            if (response.status == HttpStatusCode.OK) {
                currentUser = response.body<UserResponse>()
            }
        } catch (e: Exception) {
            // Si falla el fetch automático, no hacemos nada,
            // currentUser se queda null y la UI mandará al Login.
        }
    }

    fun login(email: String, password: String, navController: NavController, tokenManager: TokenManager) {
        viewModelScope.launch {
            isLoading = true
            loginError = null
            try {
                // 1. LOGIN (OAuth2 Form)
                val response = ApiClient.client.submitForm(
                    url = "https://api-myst.onrender.com/auth/login",
                    formParameters = parameters {
                        append("username", email)
                        append("password", password)
                    }
                )

                if (response.status == HttpStatusCode.OK) {
                    val tokenData = response.body<Token>()

                    // 2. OBTENER PERFIL INMEDIATO
                    // Inyectamos el token manualmente para evitar el lag de DataStore en esta petición
                    val userResponse = ApiClient.client.get("https://api-myst.onrender.com/users/me") {
                        headers { append(HttpHeaders.Authorization, "Bearer ${tokenData.access_token}") }
                    }

                    if (userResponse.status == HttpStatusCode.OK) {
                        val user = userResponse.body<UserResponse>()
                        Toast.makeText(navController.context, "¡Bienvenida, ${user.name}!", Toast.LENGTH_LONG).show()

                        // 3. ACTUALIZAR MEMORIA PRIMERO (Para que la UI reaccione ya)
                        currentUser = user

                        // 4. PERSISTENCIA Y LIMPIEZA
                        tokenManager.saveAuthData(tokenData.access_token, user.id_user.toString())
                        ApiClient.clearAuthCache()

                        // 5. NAVEGACIÓN
                        withContext(Dispatchers.Main) {
                            navController.navigate(AppScreens.MainScreen.route) {
                                popUpTo(AppScreens.LoginScreen.route) { inclusive = true }
                            }
                        }
                        return@launch
                    }
                } else {
                    loginError = "Credenciales incorrectas"
                    Toast.makeText(navController.context, loginError, Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                loginError = "Error: ${e.localizedMessage}"
            } finally {
                isLoading = false
            }
        }
    }

    fun logout(tokenManager: TokenManager, onLogoutComplete: () -> Unit) {
        viewModelScope.launch {
            currentUser = null
            tokenManager.deleteAuthData()
            ApiClient.clearAuthCache()
            withContext(Dispatchers.Main) {
                onLogoutComplete()
            }
        }
    }

    private fun handleUnauthorized(navController: NavController) {
        viewModelScope.launch {
            currentUser = null
            navController.navigate(AppScreens.LoginScreen.route) {
                popUpTo(0)
            }
        }
    }

    // --- MÉTODOS DE PERFIL Y CRUD ---

    fun getUser(navController: NavController) {
        viewModelScope.launch {
            isLoading = true
            try {
                val response = ApiClient.client.get("https://api-myst.onrender.com/users/me")

                if (response.status == HttpStatusCode.OK) {
                    currentUser = response.body<UserResponse>()
                } else if (response.status == HttpStatusCode.Unauthorized) {
                    handleUnauthorized(navController)
                }
            } catch (e: Exception) {
                loginError = "Error al obtener perfil"
            } finally {
                isLoading = false
            }
        }
    }

    fun updateUser(updateData: UserUpdate) {
        viewModelScope.launch {
            isLoading = true
            try {
                val response = ApiClient.client.patch("https://api-myst.onrender.com/users/me") {
                    contentType(ContentType.Application.Json)
                    setBody(updateData)
                }

                if (response.status == HttpStatusCode.OK) {
                    currentUser = response.body<UserResponse>()
                }
            } catch (e: Exception) {
                loginError = "Error al actualizar datos"
            } finally {
                isLoading = false
            }
        }
    }

    fun deleteUser(passwordConfirm: String, tokenManager: TokenManager, navController: NavController) {
        viewModelScope.launch {
            isLoading = true
            try {
                val response = ApiClient.client.delete("https://api-myst.onrender.com/users/me") {
                    contentType(ContentType.Application.Json)
                    setBody(UserDelete(password = passwordConfirm))
                }

                if (response.status == HttpStatusCode.NoContent || response.status == HttpStatusCode.OK) {
                    Toast.makeText(navController.context, "Eliminando cuenta...", Toast.LENGTH_SHORT).show()
                    delay(1500) // Pequeña pausa para que el usuario vea el mensaje
                    logout(tokenManager) {
                        navController.navigate(AppScreens.LoginScreen.route) { popUpTo(0) }
                    }
                } else if(response.status == HttpStatusCode.BadRequest) {
                    Toast.makeText(navController.context, "Contraseña incorrecta", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                isLoading = false
            }
        }
    }

    // --- GESTIÓN DE CONTRASEÑA Y REGISTRO ---

    fun forgotPassword(email: String, navController: NavController) {
        viewModelScope.launch {
            isLoading = true
            try {
                val response = ApiClient.client.post("https://api-myst.onrender.com/auth/forgot-password") {
                    contentType(ContentType.Application.Json)
                    setBody(ForgotPasswordRequest(email))
                }

                if (response.status == HttpStatusCode.OK) {
                    loginError = "Correo de recuperación enviado"
                    navController.navigate(AppScreens.ForgotPasswordScreen.route)
                }
            } catch (e: Exception) {
                loginError = "Error de red"
            } finally {
                isLoading = false
            }
        }
    }

    fun resetPassword(token: String, newPassword: String, navController: NavController) {
        viewModelScope.launch {
            isLoading = true
            try {
                val response = ApiClient.client.submitForm(
                    url = "https://api-myst.onrender.com/auth/reset-password",
                    formParameters = parameters {
                        append("token", token)
                        append("new_password", newPassword)
                    }
                )

                if (response.status == HttpStatusCode.OK) {
                    navController.navigate(AppScreens.LoginScreen.route) {
                        popUpTo(AppScreens.LoginScreen.route) { inclusive = true }
                    }
                }
            } catch (e: Exception) {
                loginError = "Error de conexión"
            } finally {
                isLoading = false
            }
        }
    }

    fun createUser(userData: UserCreate, navController: NavController) {
        viewModelScope.launch {
            isLoading = true
            try {
                val response = ApiClient.client.post("https://api-myst.onrender.com/users/") {
                    contentType(ContentType.Application.Json)
                    setBody(userData)
                }

                if (response.status == HttpStatusCode.Created) {
                    navController.navigate(AppScreens.ValidateEmailScreen.route)
                }
            } catch (e: Exception) {
                loginError = "Error al crear cuenta"
            } finally {
                isLoading = false
            }
        }
    }

    // --- IMÁGENES (Firebase + Render) ---
    fun updateProfilePicture(uri: Uri) {
        viewModelScope.launch {
            isLoading = true
            try {
                val userId = currentUser?.id_user ?: return@launch
                val fileRef = Firebase.storage.reference.child("user_profiles/$userId.jpg")

                fileRef.putFile(uri).await()
                val downloadUrl = fileRef.downloadUrl.await().toString()

                updateUserWithUrl(UserUpdate(picture = downloadUrl))
            } catch (e: Exception) {
                loginError = "Error al subir imagen"
            } finally {
                isLoading = false
            }
        }
    }

    private suspend fun updateUserWithUrl(updateData: UserUpdate) {
        try {
            val response = ApiClient.client.patch("https://api-myst.onrender.com/users/me") {
                contentType(ContentType.Application.Json)
                setBody(updateData)
            }
            if (response.status == HttpStatusCode.OK) {
                currentUser = response.body<UserResponse>()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}