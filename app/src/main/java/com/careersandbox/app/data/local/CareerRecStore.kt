package com.careersandbox.app.data.local

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.careersandbox.app.data.repository.CareerRecommendation
import com.google.gson.Gson
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.first

// Separate small file on the device, like "session" and "settings"
private val Context.careerRecDataStore by preferencesDataStore(name = "career_recs")

/** What the career page showed last time. */
data class CareerRecSnapshot(
    val query: String = "",                              // "" means random catalog picks
    val randomIds: List<String> = emptyList(),           // catalog ids, when random
    val recs: List<CareerRecommendation> = emptyList(),  // AI results, when query is set
)

/**
 * Keeps the last career recommendations per user, so reopening the page shows the
 * same cards without calling the AI service again. Kept until the next search.
 */
object CareerRecStore {
    private val gson = Gson()

    private fun key() = stringPreferencesKey("snapshot_${SessionManager.userId ?: "guest"}")

    // A missing or unreadable snapshot just means "nothing saved yet".
    suspend fun load(context: Context): CareerRecSnapshot? = try {
        val json = context.applicationContext.careerRecDataStore.data.first()[key()]
        json?.let { gson.fromJson(it, CareerRecSnapshot::class.java) }
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        null
    }

    suspend fun save(context: Context, snapshot: CareerRecSnapshot) {
        try {
            context.applicationContext.careerRecDataStore.edit { it[key()] = gson.toJson(snapshot) }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            // Not worth failing the screen over; the cards are still shown.
        }
    }
}