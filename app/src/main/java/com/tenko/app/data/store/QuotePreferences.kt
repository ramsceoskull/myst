package com.tenko.app.data.store

import android.content.Context
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore

val Context.dataStore by preferencesDataStore(name = "quote_preferences")

object QuotePreferences {
    val LAST_UPDATE = longPreferencesKey("last_update")
    val CURRENT_QUOTE = stringPreferencesKey("current_quote")
}