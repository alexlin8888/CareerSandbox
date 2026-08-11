package com.careersandbox.app.data.local

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first

// 另外開一個檔案存設定，跟 session 分開
private val Context.settingsDataStore by preferencesDataStore(name = "settings")

object SettingsStore {
    private val KEY_PUSH_ENABLED = booleanPreferencesKey("push_enabled")
    private val KEY_DAILY_DIGEST = booleanPreferencesKey("daily_digest")
    private val KEY_INTERVIEW_REMINDER = booleanPreferencesKey("interview_reminder")
    private val KEY_NEW_JOB_MATCH = booleanPreferencesKey("new_job_match")
    private val KEY_WEEKLY_REPORT = booleanPreferencesKey("weekly_report")

    private lateinit var appContext: Context

    // 跟 SessionManager 一樣，App 啟動時呼叫一次
    fun init(context: Context) {
        appContext = context.applicationContext
    }

    // 把 5 個通知設定包成一包，方便一次讀取
    data class NotificationSettings(
        val pushEnabled: Boolean = true,
        val dailyDigest: Boolean = true,
        val interviewReminder: Boolean = true,
        val newJobMatch: Boolean = false,
        val weeklyReport: Boolean = true,
    )

    // 畫面一打開就呼叫這個，把上次存的值讀回來
    suspend fun loadNotificationSettings(): NotificationSettings {
        val prefs = appContext.settingsDataStore.data.first()
        return NotificationSettings(
            pushEnabled = prefs[KEY_PUSH_ENABLED] ?: true,
            dailyDigest = prefs[KEY_DAILY_DIGEST] ?: true,
            interviewReminder = prefs[KEY_INTERVIEW_REMINDER] ?: true,
            newJobMatch = prefs[KEY_NEW_JOB_MATCH] ?: false,
            weeklyReport = prefs[KEY_WEEKLY_REPORT] ?: true,
        )
    }

    // 使用者點開關時，呼叫對應的這幾個函式存起來
    suspend fun setPushEnabled(value: Boolean) {
        appContext.settingsDataStore.edit { it[KEY_PUSH_ENABLED] = value }
    }
    suspend fun setDailyDigest(value: Boolean) {
        appContext.settingsDataStore.edit { it[KEY_DAILY_DIGEST] = value }
    }
    suspend fun setInterviewReminder(value: Boolean) {
        appContext.settingsDataStore.edit { it[KEY_INTERVIEW_REMINDER] = value }
    }
    suspend fun setNewJobMatch(value: Boolean) {
        appContext.settingsDataStore.edit { it[KEY_NEW_JOB_MATCH] = value }
    }
    suspend fun setWeeklyReport(value: Boolean) {
        appContext.settingsDataStore.edit { it[KEY_WEEKLY_REPORT] = value }
    }
}