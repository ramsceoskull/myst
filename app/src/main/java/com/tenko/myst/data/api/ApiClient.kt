package com.tenko.myst.data.api

import android.annotation.SuppressLint
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.flow.first
import kotlinx.serialization.json.Json

object ApiClient {
    @SuppressLint("StaticFieldLeak")
    private var tokenManager: TokenManager? = null

    // Cambiamos a 'lateinit var' para poder reconfigurarlo si es necesario
    var client : HttpClient = createClient()

    fun init(manager: TokenManager) {
        tokenManager = manager
    }

    // Esta función recrea el cliente, limpiando CUALQUIER caché de error anterior
    fun clearAuthCache() {
        client.close()
        client = createClient()
    }

    private fun createClient() = HttpClient(Android) {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                isLenient = true
            })
        }

        install(Auth) {
            bearer {
                // Evitamos que intente autenticar el login para no ensuciar el proceso
                sendWithoutRequest { request ->
                    request.url.pathSegments.contains("login")
                }

                loadTokens {
                    val token = tokenManager?.getToken?.first()
                    // Si el token es nulo o vacío, no enviamos nada
                    if (!token.isNullOrBlank())
                        BearerTokens(token, "")
                    else
                        null
                }
            }
        }

        // Log para ver qué está pasando realmente (puedes quitarlo luego)
         install(Logging) { level = LogLevel.ALL }
    }
}