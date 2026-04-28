package com.tenko.app.data.api

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class TokenManager(private val context: Context) {
    companion object {
        private val Context.dataStore by preferencesDataStore(name = "settings")
        val TOKEN_KEY = stringPreferencesKey("jwt_token")
        val USER_ID_KEY = stringPreferencesKey("user_id") // Nueva llave
    }

    // Guardamos ambos
    suspend fun saveAuthData(token: String, userId: String) {
        context.dataStore.edit { preferences ->
            preferences[TOKEN_KEY] = token
            preferences[USER_ID_KEY] = userId
        }
    }

    suspend fun deleteAuthData() {
        context.dataStore.edit { preferences ->
            preferences.remove(TOKEN_KEY)
            preferences.remove(USER_ID_KEY)
        }
    }

    // Leer el token (como un Flow para que la UI reaccione a cambios)
    val getToken: Flow<String?> = context.dataStore.data
        .map { it[TOKEN_KEY] }
    val getUserId: Flow<String?> = context.dataStore.data
        .map { it[USER_ID_KEY] }
}
