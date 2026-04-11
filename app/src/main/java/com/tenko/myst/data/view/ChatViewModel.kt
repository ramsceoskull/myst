package com.tenko.myst.data.view

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tenko.myst.data.api.ApiClient
import com.tenko.myst.data.model.ChatMessage as ChatUIModel
import com.tenko.myst.data.serializable.ChatMessage
import com.tenko.myst.data.serializable.AssistantResponse
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay

class ChatViewModel : ViewModel() {
    private val _messages = MutableStateFlow<List<ChatUIModel>>(emptyList())
    val messages: StateFlow<List<ChatUIModel>> = _messages

    private val _isTyping = MutableStateFlow(false)
    val isTyping: StateFlow<Boolean> = _isTyping
    fun sendMessage(text: String, token: String) {
        if (text.isBlank()) return

        // Añadir mensaje del usuario a la lista
        _messages.value += ChatUIModel(id = System.currentTimeMillis(), text = text, isUser = true)

        viewModelScope.launch {
            _isTyping.value = true
            try {
                val response =
                    ApiClient.client.post("https://api-myst.onrender.com/assistant/log-day") {
                        bearerAuth(token)
                        contentType(ContentType.Application.Json)
                        setBody(ChatMessage(message = text))
                    }

                if (response.status == HttpStatusCode.OK) {
                    val apiResult = response.body<AssistantResponse>()

                    // Aquí transformamos el JSON en algo humano y variado
                    val humanResponse = formatAssistantSpeech(apiResult)
                    addAssistantMessage(humanResponse)
                } else {
                    addAssistantMessage("Hubo un pequeño error técnico, pero aquí sigo para escucharte. 💕")
                }
            } catch (e: Exception) {
                addAssistantMessage("Parece que hay un problema de conexión. Inténtalo de nuevo en un momento.")
            } finally {
                _isTyping.value = false
            }
        }
    }

    private fun formatAssistantSpeech(response: AssistantResponse): String {
        return when (response.intent) {
            "start_period" -> listOf(
                "Entiendo, he anotado que tu ciclo comenzó el ${response.date}. ¡No olvides descansar! 🌸",
                "Registro listo. Tu periodo inició ayer. Estoy aquí para lo que necesites. ✨",
                "Hecho. Ya marqué el inicio de tu ciclo en el calendario. 💕"
            ).random()

            "end_period" -> listOf(
                "¡Perfecto! He registrado que tu periodo terminó. 🌟",
                "Listo, ya cerré el registro de este ciclo por ti. Que tengas un lindo día.",
                "Entendido, anoté la fecha de finalización correctamente. ✅"
            ).random()

            else -> listOf( // Para síntomas o logs generales
                "Gracias por compartirlo, ya guardé tus síntomas en el registro del día. 💕",
                "Entendido. Lo he anotado todo para que puedas revisarlo después.",
                "Gracias por confiar en mí, ya quedó registrado. ¿Hay algo más que sientas? ✨",
                "He tomado nota de todo. Estoy aquí contigo."
            ).random()
        }
    }

    private fun addAssistantMessage(text: String) {
        _messages.value += ChatUIModel(id = System.currentTimeMillis(), text = text, isUser = false)
    }
}