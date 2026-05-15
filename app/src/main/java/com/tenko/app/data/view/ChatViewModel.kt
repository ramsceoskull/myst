package com.tenko.app.data.view

import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.tenko.app.data.api.ApiClient
import com.tenko.app.data.model.ClinicalQuestion
import com.tenko.app.data.model.clinicalHistoryQuestions
import com.tenko.app.data.serializable.AssistantResponse
import com.tenko.app.data.serializable.ChatMessage
import com.tenko.app.data.serializable.ClinicalHistoryResponse
import com.tenko.app.data.serializable.ClinicalHistoryUpdate
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.patch
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import com.tenko.app.data.model.ChatMessage as ChatUIModel

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

    private val _historyState = MutableStateFlow<ClinicalHistoryUpdate?>(null)
    val historyState: StateFlow<ClinicalHistoryUpdate?> = _historyState

    // Estado para manejar la carga y los datos
    private val _historyData = MutableStateFlow<ClinicalHistoryResponse?>(null)
    val historyData: StateFlow<ClinicalHistoryResponse?> = _historyData
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    init {
        // Mensaje de bienvenida inicial
        addAssistantMessage("¡Hola! Soy Tenko, tu asistente de salud. 🌸\n¿Qué te gustaría hacer hoy?")
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
                text.contains("historial", ignoreCase = true) || text.contains(
                    "cuestionario",
                    ignoreCase = true
                ) -> {
                    startQuestionnaire()
                }

                // Caso B: El usuario solo presionó el botón "Daily Log"
                text.equals("mi día", ignoreCase = true) -> {
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
                val httpResponse =
                    ApiClient.client.post("https://api-myst.onrender.com/assistant/log-day") {
                        contentType(ContentType.Application.Json)
                        setBody(ChatMessage(message = text))
                    }

                if (httpResponse.status == HttpStatusCode.OK) {
                    val apiResult = httpResponse.body<AssistantResponse>()

                    // Extraemos la respuesta empática de la IA
                    val aiSpeech = apiResult.data_extracted.response

                    if (apiResult.data_extracted.is_red_flag) {
                        // Priorizar mensaje de seguridad si hay bandera roja
                        addAssistantMessage(" He detectado algo que requiere atención: $aiSpeech")
                    } else {
                        addAssistantMessage(aiSpeech)
                    }

                    // Opcional: Si el intent fue start_period, podrías disparar una pequeña confeti en UI
                    if (apiResult.intent == "start_period") {
                        // triggerPeriodUIEvent()
                    }

                } else {
                    val errorMsg = "No pude procesar eso, pero cuéntame más. "
                    addAssistantMessage(errorMsg)
                }
            } catch (e: Exception) {
                addAssistantMessage("Tuve un problema de conexión, ¿me lo repites? ")
                Log.e("ChatViewModel", "Error en log-day: ${e.message}")
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

        // Normalización básica para campos booleanos
        val processedValue: Any = when {
            text.equals("Sí", true) -> true
            text.equals("No", true) -> false
            // Intentar convertir a Int si el campo es numérico (como abortos)
            text.all { it.isDigit() } -> text.toIntOrNull() ?: text
            else -> text
        }

        responses[currentQuestion.id] = processedValue
        currentQuestionIndex++
        askNextQuestion()
    }

    // Nueva función de guardado que acepta el objeto directo desde la UI
    fun updateSingleField(fieldName: String, newValue: Any?, navController: NavController) {
        viewModelScope.launch {
            try {
                // 1. Creamos un objeto Update vacío
                // 2. Usamos una técnica de mapeo o una instancia con un solo campo
                // Nota: ClinicalHistoryUpdate permite nulos, así que solo mandamos lo que cambia

                val updatePayload = when (fieldName) {
                    "last_name" -> ClinicalHistoryUpdate(last_name = newValue as? String)
                    "second_last_name" -> ClinicalHistoryUpdate(second_last_name = newValue as? String)
                    "birthdate" -> ClinicalHistoryUpdate(birthdate = (newValue as? String)?.let {
                        java.time.LocalDate.parse(it)
                    })

                    "sex_legally" -> ClinicalHistoryUpdate(sex_legally = newValue as? String)
                    "sex_biology" -> ClinicalHistoryUpdate(sex_biology = newValue as? String)
                    "depression_screening" -> ClinicalHistoryUpdate(depression_screening = newValue as? Boolean)
                    "depression" -> ClinicalHistoryUpdate(depression = newValue as? Boolean)
                    "memory_screening" -> ClinicalHistoryUpdate(memory_screening = newValue as? Boolean)
                    "memory_alterations" -> ClinicalHistoryUpdate(memory_alterations = newValue as? Boolean)
                    "dementia" -> ClinicalHistoryUpdate(dementia = newValue as? Boolean)
                    "urinary_incontinence_screening" -> ClinicalHistoryUpdate(
                        urinary_incontinence_screening = newValue as? Boolean
                    )

                    "urinary_incontinence" -> ClinicalHistoryUpdate(urinary_incontinence = newValue as? Boolean)
                    "anemia_screening" -> ClinicalHistoryUpdate(anemia_screening = newValue as? Boolean)
                    "obesity_screening" -> ClinicalHistoryUpdate(obesity_screening = newValue as? Boolean)
                    "osteoporosis_screening" -> ClinicalHistoryUpdate(osteoporosis_screening = newValue as? Boolean)
                    "diabetes_mellitus" -> ClinicalHistoryUpdate(diabetes_mellitus = newValue as? String)
                    "arterial_hypertension" -> ClinicalHistoryUpdate(arterial_hypertension = newValue as? Boolean)
                    "sustance_use" -> ClinicalHistoryUpdate(
                        sustance_use = (newValue as? String)?.split(", ")?.map { it.trim() }
                    )

                    "std" -> ClinicalHistoryUpdate(
                        std = (newValue as? String)?.split(", ")?.map { it.trim() }
                    )

                    "turner_syndrome_screening" -> ClinicalHistoryUpdate(turner_syndrome_screening = newValue as? Boolean)
                    "endometriosis_screening" -> ClinicalHistoryUpdate(endometriosis_screening = newValue as? Boolean)
                    "endometriosis" -> ClinicalHistoryUpdate(endometriosis = newValue as? Boolean)
                    "pcos_screening" -> ClinicalHistoryUpdate(pcos_screening = newValue as? Boolean)
                    "pcos" -> ClinicalHistoryUpdate(pcos = newValue as? Boolean)
                    "sexually_active" -> ClinicalHistoryUpdate(sexually_active = newValue as? Boolean)
                    "miscarriages_abortions" -> ClinicalHistoryUpdate(miscarriages_abortions = newValue as? Int)
                    else -> null
                }

                if (updatePayload != null) {
                    val response =
                        ApiClient.client.patch("https://api-myst.onrender.com/clinical-history/me") {
                            contentType(ContentType.Application.Json)
                            setBody(updatePayload)
                        }

                    if (response.status.isSuccess()) {
                        // Actualizamos el estado local para que la UI se refresque instantáneamente
                        val updatedHistory = response.body<ClinicalHistoryResponse>()
                        syncLocalState(updatedHistory)
                        fetchMyHistory()
                        Toast.makeText(
                            navController.context,
                            "Campo actualizado correctamente",
                            Toast.LENGTH_SHORT
                        ).show()
                        delay(2000) // Pequeña pausa para que el usuario vea el cambio reflejado
                    }
                }
            } catch (e: Exception) {
                println("Error al actualizar campo: ${e.localizedMessage}")
                Toast.makeText(
                    navController.context,
                    "Error al guardar el cambio. Intenta de nuevo.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    // Función auxiliar para mantener la UI sincronizada
    private fun syncLocalState(data: ClinicalHistoryResponse) {
        _historyState.value = ClinicalHistoryUpdate(
            last_name = data.last_name,
            second_last_name = data.second_last_name,
            birthdate = data.birthdate,
            sex_legally = data.sex_legally,
            sex_biology = data.sex_biology,
            depression_screening = data.depression_screening,
            depression = data.depression,
            memory_screening = data.memory_screening,
            memory_alterations = data.memory_alterations,
            dementia = data.dementia,
            urinary_incontinence_screening = data.urinary_incontinence_screening,
            urinary_incontinence = data.urinary_incontinence,
            anemia_screening = data.anemia_screening,
            obesity_screening = data.obesity_screening,
            osteoporosis_screening = data.osteoporosis_screening,
            diabetes_mellitus = data.diabetes_mellitus,
            arterial_hypertension = data.arterial_hypertension,
            sustance_use = data.sustance_use,
            std = data.std,
            turner_syndrome_screening = data.turner_syndrome_screening,
            endometriosis_screening = data.endometriosis_screening,
            endometriosis = data.endometriosis,
            pcos_screening = data.pcos_screening,
            pcos = data.pcos,
            sexually_active = data.sexually_active,
            miscarriages_abortions = data.miscarriages_abortions
        )
    }

    fun loadClinicalHistory() {
        viewModelScope.launch {
            try {
                val response =
                    ApiClient.client.get("https://api-myst.onrender.com/clinical-history/me")
                if (response.status == HttpStatusCode.OK) {
                    val data = response.body<ClinicalHistoryResponse>()

                    // Mapeamos de RESPONSE (lo que viene del server) a UPDATE (lo que es editable)
                    _historyState.value = ClinicalHistoryUpdate(
                        last_name = data.last_name,
                        second_last_name = data.second_last_name,
                        birthdate = data.birthdate,
                        sex_legally = data.sex_legally,
                        sex_biology = data.sex_biology,
                        depression_screening = data.depression_screening,
                        depression = data.depression,
                        memory_screening = data.memory_screening,
                        memory_alterations = data.memory_alterations,
                        dementia = data.dementia,
                        urinary_incontinence_screening = data.urinary_incontinence_screening,
                        urinary_incontinence = data.urinary_incontinence,
                        anemia_screening = data.anemia_screening,
                        obesity_screening = data.obesity_screening,
                        osteoporosis_screening = data.osteoporosis_screening,
                        diabetes_mellitus = data.diabetes_mellitus,
                        arterial_hypertension = data.arterial_hypertension,
                        sustance_use = data.sustance_use,
                        std = data.std,
                        turner_syndrome_screening = data.turner_syndrome_screening,
                        endometriosis_screening = data.endometriosis_screening,
                        endometriosis = data.endometriosis,
                        pcos_screening = data.pcos_screening,
                        pcos = data.pcos,
                        sexually_active = data.sexually_active,
                        miscarriages_abortions = data.miscarriages_abortions
                    )
                }
            } catch (e: Exception) {
                // Manejar error de carga
            }
        }
    }

    fun saveClinicalHistory() {
        viewModelScope.launch {
            _isTyping.value = true
            var retryCount = 0
            val maxAttempts = 3
            var success = false
            lateinit var checkResponse: HttpResponse

            try {
                // Creamos el objeto siguiendo tus reglas de negocio
                val historyObject = mapResponsesToData()

                while (retryCount < maxAttempts && !success) {
                    checkResponse =
                        ApiClient.client.get("https://api-myst.onrender.com/clinical-history/me")

                    when (checkResponse.status) {
                        HttpStatusCode.OK -> {
                            // UPDATE: Usamos ClinicalHistoryUpdate
                            val updateResp =
                                ApiClient.client.patch("https://api-myst.onrender.com/clinical-history/me") {
                                    contentType(ContentType.Application.Json)
                                    setBody(historyObject)
                                }
                            success = updateResp.status.isSuccess()
                        }

                        HttpStatusCode.NotFound -> {
                            // CREATE: Usamos ClinicalHistoryCreate
                            // (Puedes convertir el objeto o simplemente pasar historyObject si los campos coinciden)
                            val createResp =
                                ApiClient.client.post("https://api-myst.onrender.com/clinical-history/") {
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
                    delay(3000) // Pequeña pausa para que el mensaje se vea antes de desaparecer el "typing"

                    val humanResponse = listOf(
                        "¡Listo! He guardado tu historial clínico correctamente. ✨ Ahora que terminamos, ¿quieres contarme cómo te has sentido hoy o tienes alguna duda en la que pueda ayudarte?",
                        "¡Historial actualizado! 💕 Ya quedó todo listo en tu perfil. ¿Hay algo más que quieras registrar hoy, como algún síntoma o cómo va tu día?",
                        "He guardado todo con éxito. 🌸 Regresamos al chat normal; puedes preguntarme lo que quieras o contarme qué tal va tu semana."
                    ).random()
                    addAssistantMessage(humanResponse)
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
            miscarriages_abortions = (responses["miscarriages_abortions"] as? String)?.toIntOrNull()
                ?: 0
        )
    }

    fun fetchMyHistory() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response =
                    ApiClient.client.get("https://api-myst.onrender.com/clinical-history/me")

                if (response.status == HttpStatusCode.OK) {
                    _historyData.value = response.body<ClinicalHistoryResponse>()
                } else if (response.status == HttpStatusCode.NotFound) {
                    // El usuario no tiene historial aún, podemos inicializar uno vacío
                    _historyData.value = null
                }
            } catch (e: Exception) {
                // Error de conexión
            } finally {
                _isLoading.value = false
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

            /*"end_questionnaire" -> listOf(
                "¡Listo! He guardado tu historial clínico correctamente. ✨ Ahora que terminamos, ¿quieres contarme cómo te has sentido hoy o tienes alguna duda en la que pueda ayudarte?",
                "¡Historial actualizado! 💕 Ya quedó todo listo en tu perfil. ¿Hay algo más que quieras registrar hoy, como algún síntoma o cómo va tu día?",
                "He guardado todo con éxito. 🌸 Regresamos al chat normal; puedes preguntarme lo que quieras o contarme qué tal va tu semana.",
                "¡Genial! He guardado toda la información que me diste. Gracias por mantener tu historial actualizado. 💕",
                "¡Listo! He registrado todo en tu historial clínico. Si quieres actualizar algo más, solo dime.",
                "Perfecto, ya quedó todo guardado. Estoy aquí para ayudarte a mantener tu salud al día. ✨"
            ).random()*/

            else -> listOf( // Para síntomas o logs generales
                "Gracias por compartirlo, ya guardé tus síntomas en el registro del día. 💕",
                "Entendido. Lo he anotado todo para que puedas revisarlo después.",
                "Gracias por confiar en mí, ya quedó registrado. ¿Hay algo más que sientas? ✨",
                "He tomado nota de todo. Estoy aquí contigo."
            ).random()
        }
    }

    private fun addAssistantMessage(text: String, questionRef: ClinicalQuestion? = null) {
        _messages.value += ChatUIModel(
            id = System.currentTimeMillis(),
            text = text,
            isUser = false,
            questionRef
        )
    }
}