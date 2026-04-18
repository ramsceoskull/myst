package com.tenko.myst.data.view

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tenko.myst.data.api.ApiClient
import com.tenko.myst.data.model.ClinicalQuestion
import com.tenko.myst.data.model.clinicalHistoryQuestions
import com.tenko.myst.data.serializable.AssistantResponse
import com.tenko.myst.data.serializable.ChatMessage
import com.tenko.myst.data.serializable.ClinicalHistoryUpdate
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.patch
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import com.tenko.myst.data.model.ChatMessage as ChatUIModel

class ChatViewModel : ViewModel() {
    private val _messages = MutableStateFlow<List<ChatUIModel>>(emptyList())
    val messages: StateFlow<List<ChatUIModel>> = _messages
    private val _isTyping = MutableStateFlow(false)
    val isTyping: StateFlow<Boolean> = _isTyping
    private val _currentQuestion = MutableStateFlow<ClinicalQuestion?>(null)
    val currentQuestion: StateFlow<ClinicalQuestion?> = _currentQuestion

    var isQuestionnaireMode = false
    private var currentQuestionIndex = 0
    private val responses = mutableMapOf<String, Any>()

    init {
        // Mensaje de bienvenida inicial
        addAssistantMessage("¡Hola! Soy tu asistente. 🌸 ¿Qué te gustaría hacer hoy?")
        addAssistantMessage("Puedes contarme sobre tu día (síntomas, periodo) o podemos actualizar tu Historial Clínico.")
    }

    fun sendMessage(text: String) {
        if (text.isBlank()) return

        // Añadir mensaje del usuario
        _messages.value += ChatUIModel(id = System.currentTimeMillis(), text = text, isUser = true)

        if (isQuestionnaireMode) {
            handleQuestionnaireResponse(text)
        } else {
            when {
                // Caso A: El usuario quiere el historial
                text.contains("historial", ignoreCase = true) || text.contains("cuestionario", ignoreCase = true) -> {
                    startQuestionnaire()
                }

                // Caso B: El usuario solo presionó el botón "Daily Log"
                text.equals("Daily Log", ignoreCase = true) -> {
                    addAssistantMessage("¡Claro! Cuéntame, ¿cómo te sientes hoy o qué síntomas has tenido? ✨")
                }

                // Caso C: Es un mensaje real para la IA
                else -> {
                    processDailyLog(text)
                }
            }
        }
    }

    private fun startQuestionnaire() {
        isQuestionnaireMode = true
        currentQuestionIndex = 0
        responses.clear()
        addAssistantMessage("Perfecto, vamos a actualizar tu historial. Son unas cuantas preguntas para conocerte mejor. 💕")
        askNextQuestion()
    }

    fun processDailyLog(text: String) {
        if (text.isBlank()) return

        viewModelScope.launch {
            _isTyping.value = true
            try {
                val response =
                    ApiClient.client.post("https://api-myst.onrender.com/assistant/log-day") {
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

    private fun askNextQuestion() {
        if (currentQuestionIndex < clinicalHistoryQuestions.size) {
            val question = clinicalHistoryQuestions[currentQuestionIndex]

            _currentQuestion.value = question
            addAssistantMessage(question.label, question)
        } else {
            _currentQuestion.value = null
            addAssistantMessage("¡Hemos terminado! Dame un momento para guardar todo...")
            // Aquí llamarías a la función de guardado final
            saveClinicalHistory()
        }
    }

    private fun handleQuestionnaireResponse(text: String) {
        val currentQuestion = clinicalHistoryQuestions[currentQuestionIndex]

        // Guardamos la respuesta (Aquí podrías mapear de "Sí" a true, etc.)
        responses[currentQuestion.id] = text

        currentQuestionIndex++

        askNextQuestion()
    }

    private fun saveClinicalHistory() {
        viewModelScope.launch {
            _isTyping.value = true
            var retryCount = 0
            val maxAttempts = 3
            var success = false

            try {
                // Creamos el objeto siguiendo tus reglas de negocio
                val historyObject = mapResponsesToData()

                while (retryCount < maxAttempts && !success) {
                    val checkResponse = ApiClient.client.get("https://api-myst.onrender.com/clinical-history/me")

                    when (checkResponse.status) {
                        HttpStatusCode.OK -> {
                            // UPDATE: Usamos ClinicalHistoryUpdate
                            val updateResp = ApiClient.client.patch("https://api-myst.onrender.com/clinical-history/me") {
                                contentType(ContentType.Application.Json)
                                setBody(historyObject)
                            }
                            success = updateResp.status.isSuccess()
                        }
                        HttpStatusCode.NotFound -> {
                            // CREATE: Usamos ClinicalHistoryCreate
                            // (Puedes convertir el objeto o simplemente pasar historyObject si los campos coinciden)
                            val createResp = ApiClient.client.post("https://api-myst.onrender.com/clinical-history/") {
                                contentType(ContentType.Application.Json)
                                setBody(historyObject)
                            }
                            success = createResp.status.isSuccess()
                        }
                        HttpStatusCode.Unauthorized -> {
                            retryCount++
                            delay(1000)
                            continue
                        }
                        else -> break
                    }
                }

                if (success) {
                    addAssistantMessage("¡Listo! He guardado tu historial clínico correctamente. ✨")
                    launch { ApiClient.client.post("https://api-myst.onrender.com/clinical-history/me/backfill-stats") }
                } else {
                    addAssistantMessage("No pude guardar los datos. Verifica que todos los campos sean correctos.")
                }

            } catch (e: Exception) {
                addAssistantMessage("Error: ${e.localizedMessage}")
            } finally {
                _isTyping.value = false
            }
        }
    }

    private fun mapResponsesToData(): ClinicalHistoryUpdate {
        return ClinicalHistoryUpdate(
            last_name = responses["last_name"] as? String,
            second_last_name = responses["second_last_name"] as? String,

            // Manejo de Fecha: Convertir String "yyyy-MM-dd" a LocalDate
            birthdate = (responses["birthdate"] as? String)?.let { java.time.LocalDate.parse(it) },

            sex_legally = responses["sex_legally"] as? String,
            sex_biology = responses["sex_biology"] as? String,

            depression_screening = responses["depression_screening"] as? Boolean,
            depression = responses["depression"] as? Boolean,
            memory_screening = responses["memory_screening"] as? Boolean,
            memory_alterations = responses["memory_alterations"] as? Boolean,
            dementia = responses["dementia"] as? Boolean,
            urinary_incontinence_screening = responses["urinary_incontinence_screening"] as? Boolean,
            urinary_incontinence = responses["urinary_incontinence"] as? Boolean,
            anemia_screening = responses["anemia_screening"] as? Boolean,
            obesity_screening = responses["obesity_screening"] as? Boolean,
            osteoporosis_screening = responses["osteoporosis_screening"] as? Boolean,
            diabetes_mellitus = responses["diabetes_mellitus"] as? String,
            arterial_hypertension = responses["arterial_hypertension"] as? Boolean,

            // Manejo de Listas (Sustancias e ITS): Convertir "alcohol, tabaco" a List<String>
            sustance_use = (responses["sustance_use"] as? String)?.split(", ")?.map { it.trim() },
            std = (responses["std"] as? String)?.split(", ")?.map { it.trim() },

            turner_syndrome_screening = responses["turner_syndrome_screening"] as? Boolean,
            endometriosis_screening = responses["endometriosis_screening"] as? Boolean,
            endometriosis = responses["endometriosis"] as? Boolean,
            pcos_screening = responses["pcos_screening"] as? Boolean,
            pcos = responses["pcos"] as? Boolean,

            sexually_active = responses["sexually_active"] as? Boolean,

            // Manejo de Int: Asegurar que sea numérico
            miscarriages_abortions = (responses["miscarriages_abortions"] as? String)?.toIntOrNull() ?: 0
        )
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

    private fun addAssistantMessage(text: String, questionRef: ClinicalQuestion? = null) {
        _messages.value += ChatUIModel(id = System.currentTimeMillis(), text = text, isUser = false, questionRef)
    }
}