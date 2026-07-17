package com.careersandbox.app.data.local

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first

// One small key-value file on the device, named "session"
private val Context.sessionDataStore by preferencesDataStore(name = "session")

object SessionManager {
    private val KEY_TOKEN = stringPreferencesKey("token")
    private val KEY_USER_ID = stringPreferencesKey("userId")

    private lateinit var appContext: Context

    // In-memory copy so the network layer can read it synchronously
    @Volatile
    var token: String? = null
        private set

    @Volatile
    var userId: String? = null
        private set

    // Call once at app start (MainActivity.onCreate)
    fun init(context: Context) {
        appContext = context.applicationContext
    }

    // Load the persisted session back into memory (app restart)
    suspend fun load() {
        val prefs = appContext.sessionDataStore.data.first()
        token = prefs[KEY_TOKEN]
        userId = prefs[KEY_USER_ID]
    }

    // Save to disk AND memory (called on successful login/register)
    suspend fun save(newToken: String, newUserId: String) {
        appContext.sessionDataStore.edit { prefs ->
            prefs[KEY_TOKEN] = newToken
            prefs[KEY_USER_ID] = newUserId
        }
        token = newToken
        userId = newUserId
    }

    // For logout later
    suspend fun clear() {
        appContext.sessionDataStore.edit { it.clear() }
        token = null
        userId = null
    }
}