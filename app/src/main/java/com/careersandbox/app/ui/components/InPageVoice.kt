package com.careersandbox.app.ui.components

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat

/* =====================================================================
   頁內語音輸入(不跳系統 Google 語音框)
   用 SpeechRecognizer 在 app 內聆聽,UI 由各頁自畫(麥克風鈕 + 聆聽中提示 + 即時字幕)。
   需要 RECORD_AUDIO 權限(首次按麥克風會跳系統權限詢問,不是 Google 框)。

   Manifest 需加一行:
     <uses-permission android:name="android.permission.RECORD_AUDIO" />

   用法:
     val voice = rememberInPageVoice(languageTag = "zh-TW") { transcript -> submitAnswer(transcript) }
     // 麥克風鈕 onClick = { voice.start() }
     // 顯示: voice.isListening(聆聽中) / voice.partialText(即時字幕)
   ===================================================================== */

interface InPageVoice {
    val isListening: Boolean
    val partialText: String
    val available: Boolean
    fun start()
    fun stop()
}

@Composable
fun rememberInPageVoice(
    languageTag: String = "zh-TW",
    onResult: (String) -> Unit,
): InPageVoice {
    val context = LocalContext.current
    var listening by remember { mutableStateOf(false) }
    var partial by remember { mutableStateOf("") }
    var accumulated by remember { mutableStateOf("") }
    var manualStopRequested by remember { mutableStateOf(false) }
    var hasPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) ==
                PackageManager.PERMISSION_GRANTED,
        )
    }
    var pendingStart by remember { mutableStateOf(false) }

    val recognizer = remember {
        if (SpeechRecognizer.isRecognitionAvailable(context)) {
            SpeechRecognizer.createSpeechRecognizer(context)
        } else {
            null
        }
    }
    DisposableEffect(Unit) { onDispose { recognizer?.destroy() } }

    fun finalizeAndReset() {
        listening = false
        partial = ""
        manualStopRequested = false
        val finalText = accumulated
        accumulated = ""
        if (finalText.isNotBlank()) onResult(finalText)
    }

    fun reallyStart() {
        val r = recognizer ?: return
        r.setRecognitionListener(object : RecognitionListener {
            override fun onReadyForSpeech(params: Bundle?) { listening = true; partial = accumulated }
            override fun onBeginningOfSpeech() {}
            override fun onRmsChanged(rmsdB: Float) {}
            override fun onBufferReceived(buffer: ByteArray?) {}
            override fun onEndOfSpeech() { listening = false }
            override fun onError(error: Int) {
                if (manualStopRequested || error == SpeechRecognizer.ERROR_SPEECH_TIMEOUT) {
                    finalizeAndReset()
                } else {
                    reallyStart()
                }
            }
            override fun onPartialResults(partialResults: Bundle?) {
                val t = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)?.firstOrNull()
                if (!t.isNullOrBlank()) partial = (accumulated + " " + t).trim()
            }
            override fun onResults(results: Bundle?) {
                val t = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)?.firstOrNull()
                if (!t.isNullOrBlank()) accumulated = (accumulated + " " + t).trim()
                if (manualStopRequested) {
                    finalizeAndReset()
                } else {
                    // 系統只是內部批次切斷,不是使用者講完,自動接續聽下一段
                    reallyStart()
                }
            }
            override fun onEvent(eventType: Int, params: Bundle?) {}
        })
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, languageTag)
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
        }
        try { r.startListening(intent) } catch (e: Exception) { listening = false }
    }

    val permLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { granted ->
        hasPermission = granted
        if (granted && pendingStart) { pendingStart = false; reallyStart() }
    }

    return object : InPageVoice {
        override val isListening: Boolean get() = listening
        override val partialText: String get() = partial
        override val available: Boolean get() = recognizer != null
        override fun start() {
            if (listening) return
            accumulated = ""
            manualStopRequested = false
            if (hasPermission) {
                reallyStart()
            } else {
                pendingStart = true
                permLauncher.launch(Manifest.permission.RECORD_AUDIO)
            }
        }
        override fun stop() {
            manualStopRequested = true
            recognizer?.stopListening()
        }
    }
}
