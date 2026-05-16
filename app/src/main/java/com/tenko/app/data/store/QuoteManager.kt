package com.tenko.app.data.store

import android.content.Context
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.first

object QuoteManager {
    private const val DAY_IN_MILLIS = 24 * 60 * 60 * 1000L
    private val quotes = listOf(
        "Eres más fuerte de lo que imaginas.",
        "Cada día es una nueva oportunidad para brillar.",
        "Tu voz merece ser escuchada.",
        "Nunca subestimes el poder de una mujer decidida.",
        "Confía en ti y en todo lo que eres.",
        "Tu valentía inspira a otros.",
        "Eres capaz de lograr cosas increíbles.",
        "La belleza más grande está en tu autenticidad.",
        "No necesitas ser perfecta para ser extraordinaria.",
        "Hoy es un gran día para creer en ti."
    )

    suspend fun getDailyQuote(context: Context): String {
        val prefs = context.dataStore.data.first()
        val lastUpdate =
            prefs[QuotePreferences.LAST_UPDATE] ?: 0L

        val currentQuote =
            prefs[QuotePreferences.CURRENT_QUOTE]

        val currentTime = System.currentTimeMillis()

        val shouldUpdate =
            currentTime - lastUpdate >= DAY_IN_MILLIS

        return if (shouldUpdate || currentQuote == null) {
            val newQuote = quotes.random()

            context.dataStore.edit { settings ->
                settings[QuotePreferences.CURRENT_QUOTE] = newQuote
                settings[QuotePreferences.LAST_UPDATE] = currentTime
            }

            newQuote
        } else {
            currentQuote
        }
    }
}