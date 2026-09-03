package com.careersandbox.app.ui.components

import android.Manifest
import android.content.pm.PackageManager
import android.media.MediaRecorder
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.io.File
import java.io.IOException

/* =====================================================================
   持續錄音元件（取代 InPageVoice.kt 裡 SpeechRecognizer 的做法）
   用 MediaRecorder 錄一整段完整音檔，錄完才一次性丟給後端做 Whisper 轉錄，
   不會有 SpeechRecognizer 重啟空窗期漏字的問題。

   用法：
     val recorder = rememberInPageAudioRecorder(maxDurationMs = 120_000L) { file ->
         // 錄完會拿到完整音檔，這裡接上傳/轉錄邏輯
     }
     // 麥克風按鈕：onClick = { if (recorder.isRecording) recorder.stop() else recorder.start() }
     // 動態效果：recorder.amplitude（0f..1f，已平滑過，直接拿去畫波紋）
     // 倒數／進度條：recorder.elapsedMs
   ===================================================================== */

interface InPageAudioRecorder {
    val isRecording: Boolean
    val amplitude: Float   // 0f..1f，平滑後的即時音量，只有錄音中才有意義
    val elapsedMs: Long    // 這次錄音已經錄了多久
    fun start()
    fun stop()
}

@Composable
fun rememberInPageAudioRecorder(
    maxDurationMs: Long = 120_000L,
    onStopped: (File) -> Unit,
): InPageAudioRecorder {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var recording by remember { mutableStateOf(false) }
    var amplitudeState by remember { mutableFloatStateOf(0f) }
    var elapsed by remember { mutableLongStateOf(0L) }
    var hasPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) ==
                    PackageManager.PERMISSION_GRANTED,
        )
    }
    var pendingStart by remember { mutableStateOf(false) }
    var mediaRecorder by remember { mutableStateOf<MediaRecorder?>(null) }
    var outputFile by remember { mutableStateOf<File?>(null) }

    fun releaseRecorder() {
        try {
            mediaRecorder?.stop()
        } catch (e: Exception) {
            // 錄不到 1 秒就 stop() 時，系統本來就會丟例外，這裡吞掉即可
        }
        mediaRecorder?.release()
        mediaRecorder = null
    }

    fun finishAndDeliver(deliver: Boolean) {
        recording = false
        amplitudeState = 0f
        releaseRecorder()
        val file = outputFile
        outputFile = null
        if (deliver && file != null && file.exists() && file.length() > 0) {
            onStopped(file)
        }
    }

    fun reallyStart() {
        val file = File(context.cacheDir, "answer_${System.currentTimeMillis()}.m4a")
        val recorder = MediaRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            setAudioEncodingBitRate(64_000)
            setAudioSamplingRate(44_100)
            setOutputFile(file.absolutePath)
        }
        try {
            recorder.prepare()
            recorder.start()
        } catch (e: IOException) {
            recorder.release(); return
        } catch (e: IllegalStateException) {
            recorder.release(); return
        }
        mediaRecorder = recorder
        outputFile = file
        recording = true
        elapsed = 0L

        scope.launch {
            val startedAt = System.currentTimeMillis()
            while (isActive && recording) {
                delay(100)
                elapsed = System.currentTimeMillis() - startedAt
                val raw = try { mediaRecorder?.maxAmplitude ?: 0 } catch (e: Exception) { 0 }
                // 改用分貝（dB）換算，比直接除以最大值更接近人耳對音量的感受，
                // 一般講話音量的動態範圍會被放大成更明顯的動畫變化
                val db = if (raw > 0) 20 * kotlin.math.log10(raw / 32767f) else -60f
                val normalized = ((db + 45f) / 45f).coerceIn(0f, 1f)
                // 平滑：新值佔 6 成，反應比之前快一點
                amplitudeState = amplitudeState * 0.4f + normalized * 0.6f
                if (elapsed >= maxDurationMs) {
                    finishAndDeliver(deliver = true)
                }
            }
        }
    }

    val permLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { granted ->
        hasPermission = granted
        if (granted && pendingStart) { pendingStart = false; reallyStart() }
    }

    DisposableEffect(Unit) {
        onDispose { finishAndDeliver(deliver = false) }
    }

    return object : InPageAudioRecorder {
        override val isRecording: Boolean get() = recording
        override val amplitude: Float get() = amplitudeState
        override val elapsedMs: Long get() = elapsed
        override fun start() {
            if (recording) return
            if (!hasPermission) {
                pendingStart = true
                permLauncher.launch(Manifest.permission.RECORD_AUDIO)
                return
            }
            reallyStart()
        }
        override fun stop() {
            if (!recording) return
            finishAndDeliver(deliver = true)
        }
    }
}